package com.agile.common.security;

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
import org.springframework.web.servlet.ModelAndView;

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
        clearToken(request);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(RETURN.LOGOUT_SUCCESS);
        try {
            ViewUtil.render(modelAndView, request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearToken(HttpServletRequest httpServletRequest) {
        String token = TokenUtil.getToken(httpServletRequest, securityProperties.getTokenHeader());
        Cache tokenCache = CacheUtil.getCache(securityProperties.getTokenHeader());
        tokenCache.evict(token);
    }
}
