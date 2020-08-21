package com.agile.common.security;

import cloud.agileframework.spring.util.spring.BeanUtil;
import com.agile.common.exception.SpringExceptionHandler;
import com.agile.common.util.ViewUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static LogoutHandler logoutHandler = BeanUtil.getBean(LogoutHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        // 强制账号退出
        try {
            logoutHandler.processingExit(request, response);
        } catch (Exception ignored) {
        }

        render(request, response, exception);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) {
        // 强制账号退出
        try {
            logoutHandler.processingExit(request, response);
        } catch (Exception ignored) {
        }

        render(request, response, exception);
    }

    public void render(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        try {
            logger.debug("fail", exception);
            ModelAndView view = SpringExceptionHandler.createModelAndView(exception);
            ViewUtil.render(view, request, response);
        } catch (Exception e) {
            logger.debug("fail", e);
        }
    }
}
