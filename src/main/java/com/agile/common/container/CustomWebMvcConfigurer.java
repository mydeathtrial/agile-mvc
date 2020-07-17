package com.agile.common.container;

import com.agile.common.viewResolver.JsonViewResolver;
import com.agile.common.viewResolver.JumpViewResolver;
import com.agile.common.viewResolver.PlainViewResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author 佟盟
 * 日期 2020/6/1 15:21
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class CustomWebMvcConfigurer implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CustomHandlerMethodArgumentResolver());
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.viewResolver(new JsonViewResolver());
        registry.viewResolver(new PlainViewResolver());
        registry.viewResolver(new JumpViewResolver());
        registry.enableContentNegotiation();
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        handlers.add(new CustomHandlerMethodReturnValueHandler());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CustomHandlerInterceptor());
        registry.addInterceptor(new CustomAsyncHandlerInterceptor());
    }
}
