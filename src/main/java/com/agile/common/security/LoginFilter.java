package com.agile.common.security;

import com.agile.common.exception.NoCompleteFormSign;
import com.agile.common.exception.VerificationCodeException;
import com.agile.common.exception.VerificationCodeNon;
import com.agile.common.properties.KaptchaConfigProperties;
import com.agile.common.properties.SecurityProperties;
import com.agile.common.util.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

/**
 * Created by 佟盟 on 2017/1/13
 */
@Component
public class LoginFilter extends AbstractAuthenticationProcessingFilter {

    private final FailureHandler failureHandler;
    private final SuccessHandler SuccessHandler;
    private final JWTAuthenticationProvider loginStrategyProvider;
    private final SessionAuthenticationStrategy tokenStrategy;
    private static final String USERNAME = SecurityProperties.getLoginUsername();
    private static final String PASSWORD = SecurityProperties.getLoginPassword();

    public LoginFilter(JWTAuthenticationProvider loginStrategyProvider, TokenStrategy tokenStrategy) {
        super(new AntPathRequestMatcher(SecurityProperties.getLoginUrl()));
        this.failureHandler = new FailureHandler();
        this.SuccessHandler = new SuccessHandler();
        this.loginStrategyProvider = loginStrategyProvider;
        this.tokenStrategy = tokenStrategy;
        setAllowSessionCreation(false);
        afterPropertiesSet();
    }

    public void afterPropertiesSet(){
        this.setAuthenticationSuccessHandler(SuccessHandler);
        this.setAuthenticationFailureHandler(failureHandler);
        ProviderManager providerManager = new ProviderManager(Collections.singletonList(loginStrategyProvider));
        providerManager.setEraseCredentialsAfterAuthentication(false);
        this.setAuthenticationManager(providerManager);
        this.setSessionAuthenticationStrategy(tokenStrategy);
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)throws AuthenticationException {
        //获取用户名密码
        String username = request.getParameter(USERNAME);
        String password = request.getParameter(PASSWORD);

        if(StringUtil.isEmpty(username)){
            throw new NoCompleteFormSign(null);
        }
        if(StringUtil.isEmpty(password)){
            throw new NoCompleteFormSign(null);
        }

        //验证验证码
        if(KaptchaConfigProperties.isEnable()){
            validateCode(request,response);
        }

        //生成认证信息
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
        this.setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    private void validateCode(HttpServletRequest request, HttpServletResponse response){
        String inCode = request.getParameter(SecurityProperties.getVerificationCode());
        if(inCode==null){
            throw new VerificationCodeNon(null);
        }
        String codeToken = TokenUtil.getToken(request, KaptchaConfigProperties.getKey());
        if(codeToken==null){
            throw new VerificationCodeException(null);
        }
        Object code = CacheUtil.get(codeToken);
        if(code==null || !code.toString().equalsIgnoreCase(inCode)){
            throw new VerificationCodeException(null);
        }
        Cookie cookie = new Cookie(KaptchaConfigProperties.getKey(), null);
        String cookiePath = request.getContextPath() + "/";
        cookie.setPath(cookiePath);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

}
