package com.agile.common.container;

import cloud.agileframework.spring.util.ParamUtil;
import cloud.agileframework.spring.util.RequestWrapper;
import com.agile.common.mvc.controller.MainController;
import com.agile.common.param.AgileParam;
import com.agile.common.util.ApiUtil;
import com.google.common.collect.Maps;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
        Map<String, Object> params;
        if (request instanceof RequestWrapper) {
            params = ((RequestWrapper) request).getInParam();
        } else {
            params = ParamUtil.handleInParam(request);
        }

        params.putAll(parseUriVariable(request));
        AgileParam.init(params);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MainController.clear();
    }

    /**
     * 处理路径变量
     *
     * @param currentRequest 请求
     * @return 返回变量集合
     */
    private static Map<String, Object> parseUriVariable(HttpServletRequest currentRequest) {
        Map<String, Object> uriVariables = Maps.newHashMap();

        //处理Mapping参数
        String uri = currentRequest.getRequestURI();
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
        }
        RequestMappingInfo requestMappingInfo = ApiUtil.getApiCache(currentRequest);


        if (requestMappingInfo == null) {
            return uriVariables;
        }

        //处理路径入参
        PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
        if (patternsCondition != null) {
            for (String mapping : patternsCondition.getPatterns()) {
                uriVariables.putAll(new AntPathMatcher().extractUriTemplateVariables(mapping, uri));
            }
        }

        return uriVariables;
    }
}
