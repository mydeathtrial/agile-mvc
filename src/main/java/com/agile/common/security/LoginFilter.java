package com.agile.common.security;

import com.agile.common.cache.AgileCache;
import com.agile.common.exception.LoginErrorLockException;
import com.agile.common.exception.NoCompleteFormSign;
import com.agile.common.exception.VerificationCodeException;
import com.agile.common.exception.VerificationCodeExpire;
import com.agile.common.exception.VerificationCodeNon;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.properties.KaptchaConfigProperties;
import com.agile.common.properties.SecurityProperties;
import com.agile.common.util.AesUtil;
import com.agile.common.util.CacheUtil;
import com.agile.common.util.ParamUtil;
import com.agile.common.util.ServletUtil;
import com.agile.common.util.StringUtil;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
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

    public LoginFilter(AuthenticationProvider loginStrategyProvider, TokenStrategy tokenStrategy, SecurityProperties securityProperties, KaptchaConfigProperties kaptchaConfigProperties, SuccessHandler successHandler, FailureHandler failureHandler) {
        super(new AntPathRequestMatcher(securityProperties.getLoginUrl()));
        this.username = securityProperties.getLoginUsername();
        this.password = securityProperties.getLoginPassword();
        this.code = securityProperties.getVerificationCode();
        this.securityProperties = securityProperties;
        this.failureHandler = failureHandler;
        this.successHandler = successHandler;
        this.loginStrategyProvider = loginStrategyProvider;
        this.tokenStrategy = tokenStrategy;
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
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        //获取用户名密码
        Map<String, Object> params = ParamUtil.handleInParam(request);
        String sourceUsername = ParamUtil.getInParam(params, this.username, String.class);
        String sourcePassword = ParamUtil.getInParam(params, this.password, String.class);
        String validateCode = ParamUtil.getInParam(params, this.code, String.class);

        LoggerFactory.AUTHORITY_LOG.info(String.format("正在登陆...[账号：%s][密码：%s][验证码：%s]", sourceUsername, sourcePassword, validateCode));
        judgeLoginErrorLock(request);


        //解密
        sourcePassword = AesUtil.aesDecrypt(sourcePassword, securityProperties.getPassword().getAesKey(), securityProperties.getPassword().getAesOffset(), securityProperties.getPassword().getAlgorithmModel());

        if (StringUtil.isEmpty(sourceUsername)) {
            throw new NoCompleteFormSign();
        }
        if (StringUtil.isEmpty(sourcePassword)) {
            throw new NoCompleteFormSign();
        }

        //验证验证码
        if (kaptchaConfigProperties.isEnable()) {
            validateCode(validateCode, request, response);
        }

        //生成认证信息
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(sourceUsername, sourcePassword);
        this.setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    private void validateCode(String inCode, HttpServletRequest request, HttpServletResponse response) {
        if (inCode == null) {
            throw new VerificationCodeNon(null);
        }
        String codeToken = ServletUtil.getInfo(request, kaptchaConfigProperties.getTokenHeader());
        if (codeToken == null) {
            throw new VerificationCodeException(null);
        }
        Object cacheCodeToken = CacheUtil.get(codeToken);
        if (cacheCodeToken == null) {
            throw new VerificationCodeExpire(null);
        }
        if (!cacheCodeToken.toString().equalsIgnoreCase(inCode)) {
            throw new VerificationCodeException(String.format("正确值:%s;输入值:%s", cacheCodeToken, inCode));
        }
        Cookie cookie = new Cookie(kaptchaConfigProperties.getTokenHeader(), null);
        String cookiePath = request.getContextPath() + "/";
        cookie.setPath(cookiePath);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    /**
     * 判断登录失败锁
     *
     * @param request 请求
     * @return 是否
     */
    private void judgeLoginErrorLock(HttpServletRequest request) throws LoginErrorLockException {
        if (!ServletUtil.matcherRequest(request, securityProperties.getLoginUrl())) {
            return;
        }
        AgileCache cache = CacheUtil.getCache(securityProperties.getTokenHeader());
        Integer sessionIdLoginCount = cache.get(request.getSession().getId(), Integer.class);
        if (sessionIdLoginCount == null) {
            CacheUtil.put(cache, request.getSession().getId(), 1, Integer.parseInt(Long.toString(securityProperties.getSign().getErrorSignCountTimeout().getSeconds())));
        } else if (sessionIdLoginCount >= securityProperties.getSign().getMaxErrorCount() - 1) {
            throw new LoginErrorLockException(String.valueOf(securityProperties.getSign().getErrorSignLockTime().toMinutes()));
        } else {
            CacheUtil.put(cache, request.getSession().getId(), ++sessionIdLoginCount, Integer.parseInt(Long.toString(securityProperties.getSign().getErrorSignCountTimeout().getSeconds())));
        }
    }

}
