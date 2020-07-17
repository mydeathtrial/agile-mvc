package com.agile.common.container;

import com.agile.common.mvc.controller.MainController;
import com.agile.common.param.AgileParam;
import com.agile.common.util.ParamUtil;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author 佟盟
 * 日期 2020/7/13 13:58
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class CustomAsyncHandlerInterceptor implements AsyncHandlerInterceptor {
    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        AgileParam.clear();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String, Object> params = ParamUtil.handleInParam(request);
        AgileParam.init(params);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MainController.clear();
    }
}
