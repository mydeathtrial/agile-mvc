package com.agile.common.filter;

import cloud.agileframework.spring.util.MappingUtil;
import cloud.agileframework.spring.util.RequestWrapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.handler.MatchableHandlerMapping;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @author 佟盟
 * 日期 2020/6/4 15:25
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class RequestWrapperFilter extends OncePerRequestFilter implements Filter {
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest toUseRequest = httpServletRequest;
        if (!isAsyncDispatch(httpServletRequest) && httpServletRequest instanceof RequestWrapper) {
            toUseRequest = new RequestWrapper(httpServletRequest);
        }

        filterChain.doFilter(toUseRequest, httpServletResponse);
    }


}
