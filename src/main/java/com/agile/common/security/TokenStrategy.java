package com.agile.common.security;

import com.agile.common.properties.SecurityProperties;
import com.agile.common.util.CacheUtil;
import com.agile.common.util.DateUtil;
import com.agile.common.util.RandomStringUtil;
import com.agile.common.util.ServletUtil;
import com.agile.common.util.TokenUtil;
import com.agile.mvc.entity.SysLoginEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author 佟盟 on 2018/7/4
 */
public class TokenStrategy implements SessionAuthenticationStrategy {
    private final SecurityUserDetailsService securityUserDetailsService;
    private final SecurityProperties securityProperties;

    public TokenStrategy(SecurityUserDetailsService securityUserDetailsService, SecurityProperties securityProperties) {
        this.securityUserDetailsService = securityUserDetailsService;
        this.securityProperties = securityProperties;
    }

    @Override
    public void onAuthentication(Authentication authentication, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws SessionAuthenticationException {
        SecurityUser userDetails = (SecurityUser) authentication.getDetails();
        final int digit = 8;
        String salt = RandomStringUtil.getRandom(digit, RandomStringUtil.Random.LETTER_UPPER);
        String cacheKey = authentication.getName();
        String saltsKey = cacheKey + "_SALT";

        SysLoginEntity loginEntity = SysLoginEntity.builder().sysLoginId(RandomStringUtil.getRandom(digit, RandomStringUtil.Random.LETTER_UPPER))
                .sysUserId(userDetails.getSysUsersId())
                .loginTime(DateUtil.getCurrentDate())
                .loginIp(ServletUtil.getCustomerIPAddr(httpServletRequest))
                .token(salt).build();

        securityUserDetailsService.addLoginInfo(loginEntity);

        CacheUtil.put(saltsKey, CacheUtil.get(saltsKey) == null ? salt : String.format("%s%s", Objects.requireNonNull(CacheUtil.get(saltsKey)).toString(), salt));
        CacheUtil.put(cacheKey, authentication);

        String agileToken = TokenUtil.generateToken(cacheKey, salt, userDetails.getPassword());
        Cookie cookie = new Cookie(securityProperties.getTokenHeader(), agileToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        httpServletResponse.addCookie(cookie);
        httpServletResponse.setHeader(securityProperties.getTokenHeader(), agileToken);
    }
}
