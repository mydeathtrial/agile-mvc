package com.agile.common.security;

import cloud.agileframework.cache.support.AgileCache;
import cloud.agileframework.cache.util.CacheUtil;
import cloud.agileframework.common.util.date.DateUtil;
import cloud.agileframework.spring.util.ServletUtil;
import cloud.agileframework.spring.util.spring.IdUtil;
import com.agile.common.base.Constant;
import com.agile.common.properties.SecurityProperties;
import com.agile.common.util.TokenUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 佟盟
 * 日期 2019/3/20 14:20
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class TokenStrategy implements SessionAuthenticationStrategy {
    private final CustomerUserDetailsService securityUserDetailsService;
    private final SecurityProperties securityProperties;
    private final AgileCache cache;

    public TokenStrategy(CustomerUserDetailsService securityUserDetailsService, SecurityProperties securityProperties) {
        this.securityUserDetailsService = securityUserDetailsService;
        this.securityProperties = securityProperties;
        this.cache = CacheUtil.getCache(securityProperties.getTokenHeader());
    }

    @Override
    public void onAuthentication(Authentication authentication, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws SessionAuthenticationException {
        CustomerUserDetails userDetails = (CustomerUserDetails) authentication.getDetails();
        String username = userDetails.getUsername();
        long sessionToken = IdUtil.generatorId();

        //创建token令牌
        String token = TokenUtil.generateToken(username, sessionToken, DateUtil.add(new Date(), Duration.of(1, ChronoUnit.YEARS)));

        //创建登录信息
        LoginCacheInfo loginCacheInfo = LoginCacheInfo.createLoginCacheInfo(username, authentication, sessionToken, token, new Date(), DateUtil.add(new Date(), securityProperties.getTokenTimeout()));

        //放入缓存
        cache.put(username, loginCacheInfo);

        //存储登录信息
        securityUserDetailsService.loadLoginInfo(userDetails, ServletUtil.getRequestIP(httpServletRequest), Long.toString(sessionToken));

        //通知前端
        Map<String, Object> extension = new HashMap<String, Object>(Constant.NumberAbout.THREE) {{
            put("token", token);
            put("realName", ((CustomerUserDetails) authentication.getDetails()).getName());
            put("detail", authentication);
        }};
        TokenUtil.notice(httpServletRequest, httpServletResponse, token, extension);
    }
}
