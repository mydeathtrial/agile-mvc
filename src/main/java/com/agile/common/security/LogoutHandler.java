package com.agile.common.security;

import com.agile.common.base.Constant;
import com.agile.common.base.Head;
import com.agile.common.base.RETURN;
import com.agile.common.cache.Cache;
import com.agile.common.properties.SecurityProperties;
import com.agile.common.util.CacheUtil;
import com.agile.common.util.TokenUtil;
import com.agile.common.util.ViewUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 佟盟 on 2018/7/6
 */
public class LogoutHandler extends AbstractAuthenticationTargetUrlRequestHandler implements LogoutSuccessHandler {
    @Autowired
    private SecurityProperties securityProperties;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        clearToken(request, response);

        try {
            assert RETURN.LOGOUT_SUCCESS != null;
            ViewUtil.render(new Head(RETURN.LOGOUT_SUCCESS), null, request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String token = TokenUtil.getToken(httpServletRequest, securityProperties.getTokenHeader());
        Cache tokenCache = CacheUtil.getCache(securityProperties.getTokenHeader());
        tokenCache.evict(token);

        Cookie cookie = new Cookie(securityProperties.getTokenHeader(), null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath(Constant.RegularAbout.SLASH);
        httpServletResponse.addCookie(cookie);
    }
}
