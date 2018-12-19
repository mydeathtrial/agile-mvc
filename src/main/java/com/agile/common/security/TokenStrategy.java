package com.agile.common.security;

import com.agile.common.properties.SecurityProperties;
import com.agile.common.util.CacheUtil;
import com.agile.common.util.DateUtil;
import com.agile.common.util.RandomStringUtil;
import com.agile.common.util.ServletUtil;
import com.agile.common.util.TokenUtil;
import com.agile.mvc.entity.SysLoginEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * Created by 佟盟 on 2018/7/4
 */
@Component
public class TokenStrategy implements SessionAuthenticationStrategy {
    private final SecurityUserDetailsService securityUserDetailsService;

    @Autowired
    public TokenStrategy(SecurityUserDetailsService securityUserDetailsService) {
        this.securityUserDetailsService = securityUserDetailsService;
    }

    @Override
    public void onAuthentication(Authentication authentication, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws SessionAuthenticationException {
        SecurityUser userDetails = (SecurityUser) authentication.getDetails();
        String salt = RandomStringUtil.getRandom(8, RandomStringUtil.Random.LETTER_UPPER);
        String cacheKey = authentication.getName();
        String saltsKey = cacheKey + "_SALT";

        SysLoginEntity loginEntity = SysLoginEntity.builder().setSysLoginId(RandomStringUtil.getRandom(8, RandomStringUtil.Random.LETTER_UPPER))
                .setSysUserId(userDetails.getSysUsersId())
                .setLoginTime(DateUtil.getCurrentDate())
                .setLoginIp(ServletUtil.getCustomerIPAddr(httpServletRequest))
                .setToken(salt).build();

        securityUserDetailsService.addLoginInfo(loginEntity);

        CacheUtil.put(saltsKey, CacheUtil.get(saltsKey) == null ? salt : String.format("%s%s", Objects.requireNonNull(CacheUtil.get(saltsKey)).toString(), salt));
        CacheUtil.put(cacheKey, authentication);

        String agileToken = TokenUtil.generateToken(cacheKey, salt, userDetails.getPassword());
        Cookie cookie = new Cookie(SecurityProperties.getTokenHeader(), agileToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        httpServletResponse.addCookie(cookie);
        httpServletResponse.setHeader(SecurityProperties.getTokenHeader(), agileToken);
    }
}
