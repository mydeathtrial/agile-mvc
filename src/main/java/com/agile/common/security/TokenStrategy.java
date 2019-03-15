package com.agile.common.security;

import com.agile.common.cache.Cache;
import com.agile.common.properties.SecurityProperties;
import com.agile.common.util.CacheUtil;
import com.agile.common.util.IdUtil;
import com.agile.common.util.ServletUtil;
import com.agile.common.util.StringUtil;
import com.agile.common.util.TokenUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author 佟盟 on 2018/7/4
 */
public class TokenStrategy implements SessionAuthenticationStrategy {
    private final CustomerUserDetailsService securityUserDetailsService;
    private final SecurityProperties securityProperties;
    private Cache cache;

    public TokenStrategy(CustomerUserDetailsService securityUserDetailsService, SecurityProperties securityProperties) {
        this.securityUserDetailsService = securityUserDetailsService;
        this.securityProperties = securityProperties;
        this.cache = CacheUtil.getCache(securityProperties.getTokenHeader());
    }

    @Override
    public void onAuthentication(Authentication authentication, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws SessionAuthenticationException {
        String token;

        //判断策略
        if (securityProperties.getTokenType() == SecurityProperties.TokenType.DIFFICULT) {
            token = difficultToken(authentication, httpServletRequest);
        } else {
            token = easyToken(authentication, httpServletRequest);
        }

        //通知前端
        notice(httpServletResponse, token);

    }

    /**
     * 复杂令牌策略
     *
     * @param authentication     权限信息
     * @param httpServletRequest 请求
     * @return 令牌
     */
    private String difficultToken(Authentication authentication, HttpServletRequest httpServletRequest) {
        CustomerUserDetails userDetails = (CustomerUserDetails) authentication.getDetails();
        String sessionPassword = Long.toString(IdUtil.generatorId());
        String userInfoCacheKey = authentication.getName();
        String sessionPasswordCacheKey = userInfoCacheKey + "_SALT";

        securityUserDetailsService.loadLoginInfo(userDetails, ServletUtil.getCustomerIPAddr(httpServletRequest), sessionPassword);

        cache.put(sessionPasswordCacheKey, cache.get(sessionPasswordCacheKey) == null ? sessionPassword : String.format("%s,%s", Objects.requireNonNull(cache.get(sessionPasswordCacheKey)).toString(), sessionPassword));
        cache.put(userInfoCacheKey, authentication);
        return TokenUtil.generateToken(userInfoCacheKey, sessionPassword, userDetails.getPassword());
    }

    /**
     * 简单令牌策略
     *
     * @param authentication     权限信息
     * @param httpServletRequest 请求
     * @return 令牌
     */
    private String easyToken(Authentication authentication, HttpServletRequest httpServletRequest) {
        CustomerUserDetails userDetails = (CustomerUserDetails) authentication.getDetails();
        String token = TokenUtil.getToken(httpServletRequest, securityProperties.getTokenHeader());
        if (StringUtil.isEmpty(token) || !TokenUtil.isToken(token) || !TokenUtil.validateToken(token)) {
            token = TokenUtil.generateToken(userDetails.getUsername(), userDetails.getPassword());
        }
        cache.put(token, authentication, securityProperties.getTokenTimeout());

        securityUserDetailsService.loadLoginInfo(userDetails, ServletUtil.getCustomerIPAddr(httpServletRequest), token);
        return token;
    }

    /**
     * 通知前端
     */
    private void notice(HttpServletResponse httpServletResponse, String agileToken) {
//        Cookie cookie = new Cookie(securityProperties.getTokenHeader(), agileToken);
//        cookie.setHttpOnly(true);
//        cookie.setPath(Constant.RegularAbout.SLASH);
//        httpServletResponse.addCookie(cookie);
        httpServletResponse.setHeader(securityProperties.getTokenHeader(), agileToken);
    }
}
