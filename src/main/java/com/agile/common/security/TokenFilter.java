package com.agile.common.security;

import com.agile.common.exception.NoSignInException;
import com.agile.common.exception.NoSuchIDException;
import com.agile.common.exception.TokenIllegalException;
import com.agile.common.properties.SecurityProperties;
import com.agile.common.util.CacheUtil;
import com.agile.common.util.ObjectUtil;
import com.agile.common.util.RandomStringUtil;
import com.agile.common.util.StringUtil;
import com.agile.common.util.TokenUtil;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 登陆验证码拦截器
 *
 * @author 佟盟 on 2017/9/27
 */
public class TokenFilter extends OncePerRequestFilter {
    private final FailureHandler failureHandler;
    private final SecurityUserDetailsService securityUserDetailsService;
    private RequestMatcher[] matches;
    private AntPathRequestMatcher signOutUrl;

    public TokenFilter(SecurityUserDetailsService securityUserDetailsService, String[] immuneUrl) {
        matches = new RequestMatcher[immuneUrl.length];
        int i = 0;
        while (i < immuneUrl.length) {
            matches[i] = new AntPathRequestMatcher(immuneUrl[i]);
            i++;
        }
        this.failureHandler = new FailureHandler();
        this.securityUserDetailsService = securityUserDetailsService;
        this.signOutUrl = new AntPathRequestMatcher(SecurityProperties.getLoginOutUrl());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            if (!requiresAuthentication(request)) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = TokenUtil.getToken(request, SecurityProperties.getTokenHeader());
            if (StringUtil.isBlank(token)) {
                throw new NoSignInException(null);
            }

            if (!TokenUtil.validateToken(token)) {
                throw new TokenIllegalException(null);
            }

            Claims claims = TokenUtil.getClaimsFromToken(token);
            assert claims != null;
            String cacheKey = claims.get(TokenUtil.AUTHENTICATION_CACHE_KEY).toString();
            String saltsKey = cacheKey + "_SALT";
            String oldSalt = claims.get(TokenUtil.AUTHENTICATION_CACHE_SALT_KEY).toString();
            String salts = Objects.requireNonNull(CacheUtil.get(saltsKey)).toString();
            Authentication authentication = (Authentication) CacheUtil.get(cacheKey);
            if (ObjectUtil.isEmpty(authentication)) {
                throw new TokenIllegalException(null);
            }
            if (!salts.contains(oldSalt)) {
                throw new TokenIllegalException(null);
            }
            SecurityUser userDetails = (SecurityUser) authentication.getDetails();
            securityUserDetailsService.validate(userDetails);

            String oldSaltValue = claims.get(TokenUtil.AUTHENTICATION_CREATE_SALT_VALUE).toString();
            String currentSaltValue = userDetails.getPassword();
            if (!oldSaltValue.equals(currentSaltValue)) {
                throw new TokenIllegalException(null);
            }
            if (signOutUrl.matches(request)) {
                securityUserDetailsService.updateLoginInfo(oldSalt);
                CacheUtil.put(saltsKey, salts.replaceAll(oldSalt, ""));
            } else {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                refreshToken(authentication, response, oldSalt, userDetails.getSaltValue());
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            if (e instanceof AuthenticationException) {
                exceptionHandler(request, response, (AuthenticationException) e);
            } else {
                exceptionHandler(request, response, new TokenIllegalException(null));
            }
        }
    }

    private void exceptionHandler(HttpServletRequest req, HttpServletResponse res, AuthenticationException e) {
        failureHandler.onAuthenticationFailure(req, res, e);
    }

    private void refreshToken(Authentication authentication, ServletResponse httpServletResponse, String oldSalt, String saltValue) throws NoSuchIDException {
        String cacheKey = authentication.getName();
        String saltKey = cacheKey + "_SALT";
        final int digit = 8;
        String newSalt = RandomStringUtil.getRandom(digit, RandomStringUtil.Random.LETTER_UPPER);
        CacheUtil.put(saltKey, Objects.requireNonNull(CacheUtil.get(saltKey)).toString().replaceAll(oldSalt, newSalt));
        securityUserDetailsService.updateLoginInfo(oldSalt, newSalt);

        String agileToken = TokenUtil.generateToken(cacheKey, newSalt, saltValue);
        Cookie cookie = new Cookie(SecurityProperties.getTokenHeader(), agileToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        HttpServletResponse response = (HttpServletResponse) httpServletResponse;
        response.addCookie(cookie);
        response.setHeader(SecurityProperties.getTokenHeader(), agileToken);
    }

    private boolean requiresAuthentication(HttpServletRequest request) {
        for (RequestMatcher matcher : matches) {
            if (matcher.matches(request)) {
                return false;
            }
        }
        return true;
    }
}
