package com.agile.common.security;

import cloud.agileframework.cache.support.AgileCache;
import cloud.agileframework.cache.util.CacheUtil;
import cloud.agileframework.common.util.security.AesUtil;
import cloud.agileframework.kaptcha.properties.KaptchaConfigProperties;
import cloud.agileframework.spring.util.ServletUtil;
import com.agile.common.base.Constant;
import com.agile.common.exception.AuthenticationException;
import com.agile.common.exception.LoginErrorLockException;
import com.agile.common.exception.NoCompleteFormSign;
import com.agile.common.exception.VerificationCodeException;
import com.agile.common.exception.VerificationCodeExpire;
import com.agile.common.exception.VerificationCodeNon;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.properties.SecurityProperties;
import com.agile.common.security.provider.LockSignProviderInterface;
import com.agile.common.util.ParamUtil;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * @author 佟盟 on 2017/1/13
 */
public class LoginFilter extends AbstractAuthenticationProcessingFilter {

    private final String username;
    private final String password;
    private final String code;
    private final FailureHandler failureHandler;
    private final SuccessHandler successHandler;
    private final AuthenticationProvider loginStrategyProvider;
    private final SessionAuthenticationStrategy tokenStrategy;
    private final KaptchaConfigProperties kaptchaConfigProperties;
    private final SecurityProperties securityProperties;
    private final CustomerUserDetailsService customerUserDetailsService;

    public LoginFilter(AuthenticationProvider loginStrategyProvider,
                       TokenStrategy tokenStrategy,
                       SecurityProperties securityProperties,
                       KaptchaConfigProperties kaptchaConfigProperties,
                       SuccessHandler successHandler,
                       FailureHandler failureHandler,
                       CustomerUserDetailsService customerUserDetailsService) {
        super(new AntPathRequestMatcher(securityProperties.getLoginUrl()));
        this.username = securityProperties.getLoginUsername();
        this.password = securityProperties.getLoginPassword();
        this.code = securityProperties.getVerificationCode();
        this.securityProperties = securityProperties;
        this.failureHandler = failureHandler;
        this.successHandler = successHandler;
        this.loginStrategyProvider = loginStrategyProvider;
        this.tokenStrategy = tokenStrategy;
        this.customerUserDetailsService = customerUserDetailsService;
        setAllowSessionCreation(false);
        afterPropertiesSet();
        this.kaptchaConfigProperties = kaptchaConfigProperties;
    }

    @Override
    public void afterPropertiesSet() {
        this.setAuthenticationSuccessHandler(successHandler);
        this.setAuthenticationFailureHandler(failureHandler);
        ProviderManager providerManager = new ProviderManager(Collections.singletonList(loginStrategyProvider));
        providerManager.setEraseCredentialsAfterAuthentication(false);
        this.setAuthenticationManager(providerManager);
        this.setSessionAuthenticationStrategy(tokenStrategy);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws LoginErrorLockException, NoCompleteFormSign, VerificationCodeException, VerificationCodeExpire, AuthenticationException {
        //获取用户名密码
        Map<String, Object> params = ParamUtil.handleInParam(request);
        Map<String, Object> map = ParamUtil.coverToMap(params);
        String sourceUsername = ParamUtil.getInParam(map, this.username, String.class);
        String sourcePassword = ParamUtil.getInParam(map, this.password, String.class);
        String validateCode = ParamUtil.getInParam(map, this.code, String.class);

        LoggerFactory.AUTHORITY_LOG.info(String.format("正在登陆...[账号：%s][密码：%s][验证码：%s]", sourceUsername, sourcePassword, validateCode));

        judgeLoginErrorLock(request, sourceUsername);

        //解密
        sourcePassword = AesUtil.aesDecrypt(sourcePassword, securityProperties.getPassword().getAesKey(), securityProperties.getPassword().getAesOffset(), securityProperties.getPassword().getAlgorithmModel());

        if (StringUtils.isEmpty(sourceUsername) || StringUtils.isEmpty(sourcePassword)) {
            throw new NoCompleteFormSign();
        }


        //验证验证码
        if (kaptchaConfigProperties.isEnable()) {
            validateCode(validateCode, request, response);
        }

        //生成认证信息
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(sourceUsername, sourcePassword);
        this.setDetails(request, authRequest);
        try {
            return this.getAuthenticationManager().authenticate(authRequest);
        } catch (Exception e) {
            throw new AuthenticationException(e);
        }
    }

    private void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    private void validateCode(String inCode, HttpServletRequest request, HttpServletResponse response) throws VerificationCodeException, VerificationCodeExpire {
        if (inCode == null) {
            failureHandler.render(request, response, new VerificationCodeNon());
            return;
        }
        String codeToken = ParamUtil.getInfo(request, kaptchaConfigProperties.getTokenHeader());
        if (codeToken == null) {
            throw new VerificationCodeException();
        }
        Object cacheCodeToken = CacheUtil.get(codeToken);
        if (cacheCodeToken == null) {
            throw new VerificationCodeExpire();
        }
        if (!cacheCodeToken.toString().equalsIgnoreCase(inCode)) {
            throw new VerificationCodeException(String.format("正确值:%s;输入值:%s", cacheCodeToken, inCode));
        }
        Cookie cookie = new Cookie(kaptchaConfigProperties.getTokenHeader(), null);
        String cookiePath = request.getContextPath() + Constant.RegularAbout.SLASH;
        cookie.setPath(cookiePath);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    /**
     * 判断登录失败锁
     *
     * @param request 请求
     */
    private void judgeLoginErrorLock(HttpServletRequest request, String sourceUsername) throws LoginErrorLockException {
        if (!ServletUtil.matcherRequest(request, securityProperties.getLoginUrl()) || !securityProperties.getErrorSign().isEnable()) {
            return;
        }

        AgileCache cache = securityProperties.getErrorSign().getCache();

        //获取锁定标识
        ErrorSignInfo errorSignInfo = errorSignInfo(request, sourceUsername);

        //计数过期时间
        Duration countTimeout = securityProperties.getErrorSign().getErrorSignCountTimeout();

        //已失败次数
        Integer errorCount = cache.get(errorSignInfo.getLockObject(), Integer.class);
        if (errorCount == null) {
            cache.put(errorSignInfo.getLockObject(), 1, countTimeout);
        } else if (errorCount < securityProperties.getErrorSign().getMaxErrorCount()) {
            cache.put(errorSignInfo.getLockObject(), ++errorCount, countTimeout);
        } else {
            errorSignInfo.setLockTime(new Date());

            //锁定过期时间
            Duration timeout = securityProperties.getErrorSign().getErrorSignLockTime();
            boolean alwaysLock = timeout.toMillis() <= 0;
            if (alwaysLock) {
                cache.put(errorSignInfo.getLockObject(), ++errorCount);
            } else {
                cache.put(errorSignInfo.getLockObject(), ++errorCount, timeout);
                errorSignInfo.setTimeOut(new Date(errorSignInfo.getLockTime().getTime() + timeout.toMillis()));
            }

            //通知
            notice(errorSignInfo);

            throw new LoginErrorLockException(alwaysLock ? "请联系管理员解锁" : securityProperties.getErrorSign().getErrorSignLockTime().toMinutes() + "分钟");
        }
    }

    /**
     * 获取锁定对象
     *
     * @param request        请求
     * @param sourceUsername 账户
     * @return 锁定依据
     */
    private ErrorSignInfo errorSignInfo(HttpServletRequest request, String sourceUsername) {
        ErrorSignInfo.ErrorSignInfoBuilder builder = ErrorSignInfo.builder();

        StringBuilder lockObject = new StringBuilder();
        SecurityProperties.LockType[] lockTypes = securityProperties.getErrorSign().getLockType();

        if (ArrayUtils.contains(lockTypes, SecurityProperties.LockType.IP)) {
            lockObject.append(ServletUtil.getCurrentRequestIP());
            builder.ip(ServletUtil.getRequestIP(request));
        }
        if (ArrayUtils.contains(lockTypes, SecurityProperties.LockType.SESSION_ID)) {
            lockObject.append(request.getSession().getId());
            builder.sessionId(request.getSession().getId());
        }
        if (ArrayUtils.contains(lockTypes, SecurityProperties.LockType.ACCOUNT)) {
            lockObject.append(sourceUsername);
            builder.account(sourceUsername);
        }

        return builder
                .lockObject(lockObject.toString())
                .build();
    }

    @Autowired
    private ObjectProvider<LockSignProviderInterface> lockSignProviders;

    /**
     * 通知
     */
    private void notice(ErrorSignInfo errorSignInfo) {
        // 调用钩子
        lockSignProviders.orderedStream().forEach(provider -> provider.lock(errorSignInfo));
    }

    /**
     * 错误登录信息
     */
    @Builder
    @Getter
    public static final class ErrorSignInfo {
        private final String lockObject;
        private final String ip;
        private final String sessionId;
        private final String account;
        private Date lockTime;
        private Date timeOut;

        public void setTimeOut(Date timeOut) {
            this.timeOut = timeOut;
        }

        public void setLockTime(Date lockTime) {
            this.lockTime = lockTime;
        }
    }

}
