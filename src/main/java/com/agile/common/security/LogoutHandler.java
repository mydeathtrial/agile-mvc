package com.agile.common.security;

import com.agile.common.base.RETURN;
import com.agile.common.util.ViewUtil;
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

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(RETURN.LOGOUT_SUCCESS);
        try {
            ViewUtil.render(modelAndView, request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
