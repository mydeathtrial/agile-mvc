package com.agile.common.security;

import com.agile.common.cache.AgileCache;
import com.agile.common.exception.LoginErrorLockException;
import com.agile.common.exception.SpringExceptionHandler;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.properties.SecurityProperties;
import com.agile.common.util.CacheUtil;
import com.agile.common.util.ServletUtil;
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

        if (!judgeLoginErrorLock(request)) {
            view = SpringExceptionHandler.createModelAndView(new LoginErrorLockException(properties.getLoginLockTime().toMinutes()));
        } else {
            view = SpringExceptionHandler.createModelAndView(exception);
        }
        try {
            ViewUtil.render(view, request, response);
        } catch (Exception e) {
            LoggerFactory.AUTHORITY_LOG.debug(e);
        }
    }

    /**
     * 判断登陆失败锁
     *
     * @param request 请求
     * @return 是否
     */
    private boolean judgeLoginErrorLock(HttpServletRequest request) {
        if (!ServletUtil.matcherRequest(request, properties.getLoginUrl())) {
            return true;
        }
        AgileCache cache = CacheUtil.getCache(properties.getTokenHeader());
        Integer sessionIdLoginCount = cache.get(request.getSession().getId(), Integer.class);
        if (sessionIdLoginCount == null) {
            CacheUtil.put(cache, request.getSession().getId(), 1, Integer.valueOf(Long.toString(properties.getLoginErrorTimeout().getSeconds())));
        } else if (sessionIdLoginCount >= properties.getLoginErrorCount() - 1) {
            return false;
        } else {
            CacheUtil.put(cache, request.getSession().getId(), ++sessionIdLoginCount, Integer.valueOf(Long.toString(properties.getLoginErrorTimeout().getSeconds())));
        }
        return true;
    }
}
