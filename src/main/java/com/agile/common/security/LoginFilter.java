package com.agile.common.security;

import com.agile.common.exception.NoCompleteFormSign;
import com.agile.common.exception.VerificationCodeException;
import com.agile.common.exception.VerificationCodeNon;
import com.agile.common.properties.KaptchaConfigProperties;
import com.agile.common.properties.SecurityProperties;
import com.agile.common.util.CacheUtil;
import com.agile.common.util.StringUtil;
import com.agile.common.util.TokenUtil;
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

/**
 * @author 佟盟 on 2017/1/13
 */
public class LoginFilter extends AbstractAuthenticationProcessingFilter {

    private final String username;
    private final String password;
    private final FailureHandler failureHandler;
    private final SuccessHandler successHandler;
    private final AuthenticationProvider loginStrategyProvider;
    private final SessionAuthenticationStrategy tokenStrategy;

    public LoginFilter(AuthenticationProvider loginStrategyProvider, TokenStrategy tokenStrategy) {
        super(new AntPathRequestMatcher(SecurityProperties.getLoginUrl()));
        this.username = SecurityProperties.getLoginUsername();
        this.password = SecurityProperties.getLoginPassword();
        this.failureHandler = new FailureHandler();
        this.successHandler = new SuccessHandler();
        this.loginStrategyProvider = loginStrategyProvider;
        this.tokenStrategy = tokenStrategy;
        setAllowSessionCreation(false);
        afterPropertiesSet();
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
        String sourceUsername = request.getParameter(this.username);
        String sourcePassword = request.getParameter(this.password);

        if (StringUtil.isEmpty(sourceUsername)) {
            throw new NoCompleteFormSign();
        }
        if (StringUtil.isEmpty(sourcePassword)) {
            throw new NoCompleteFormSign();
        }

        //验证验证码
        if (KaptchaConfigProperties.isEnable()) {
            validateCode(request, response);
        }

        //生成认证信息
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(sourceUsername, sourcePassword);
        this.setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    private void validateCode(HttpServletRequest request, HttpServletResponse response) {
        String inCode = request.getParameter(SecurityProperties.getVerificationCode());
        if (inCode == null) {
            throw new VerificationCodeNon(null);
        }
        String codeToken = TokenUtil.getToken(request, KaptchaConfigProperties.getKey());
        if (codeToken == null) {
            throw new VerificationCodeException(null);
        }
        Object code = CacheUtil.get(codeToken);
        if (code == null || !code.toString().equalsIgnoreCase(inCode)) {
            throw new VerificationCodeException(null);
        }
        Cookie cookie = new Cookie(KaptchaConfigProperties.getKey(), null);
        String cookiePath = request.getContextPath() + "/";
        cookie.setPath(cookiePath);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

}
