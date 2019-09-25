package com.agile.common.security;

import com.agile.common.exception.SpringExceptionHandler;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.properties.SecurityProperties;
import com.agile.common.util.ViewUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 佟盟 on 2018/6/25
 */
public class FailureHandler implements AuthenticationFailureHandler, AccessDeniedHandler {
    private SecurityProperties properties;

    public FailureHandler(SecurityProperties properties) {
        this.properties = properties;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        render(request, response, exception);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) {
        render(request, response, exception);
    }

    private void render(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        LoggerFactory.AUTHORITY_LOG.debug(exception);
        ModelAndView view;

        view = SpringExceptionHandler.createModelAndView(exception);
        try {
            ViewUtil.render(view, request, response);
        } catch (Exception e) {
            LoggerFactory.AUTHORITY_LOG.debug(e);
        }
    }
}
