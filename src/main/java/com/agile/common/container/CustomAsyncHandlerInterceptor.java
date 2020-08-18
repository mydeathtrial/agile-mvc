package com.agile.common.container;

import cloud.agileframework.spring.util.ParamUtil;
import com.agile.common.base.ApiInfo;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        ApiInfo info = ApiUtil.getApiCache(currentRequest);

        //处理路径入参
        if (info != null) {
            Set<RequestMappingInfo> requestMappingInfos = info.getRequestMappingInfos();
            if (requestMappingInfos != null) {
                Set<String> mappingCache = new HashSet<>();
                for (RequestMappingInfo requestMappingInfo : requestMappingInfos) {
                    PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
                    if (patternsCondition != null) {
                        mappingCache.addAll(patternsCondition.getPatterns());
                    }
                }
                for (String mapping : mappingCache) {
                    try {
                        uriVariables.putAll(new AntPathMatcher().extractUriTemplateVariables(mapping, uri));
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        return uriVariables;
    }
}
