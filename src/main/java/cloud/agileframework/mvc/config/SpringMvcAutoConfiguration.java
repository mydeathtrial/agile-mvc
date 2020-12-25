package cloud.agileframework.mvc.config;

import cloud.agileframework.mvc.container.AgileHandlerMapping;
import cloud.agileframework.mvc.container.AgileHandlerMethodArgumentResolver;
import cloud.agileframework.mvc.container.CustomAsyncHandlerInterceptor;
import cloud.agileframework.mvc.container.CustomHandlerInterceptor;
import cloud.agileframework.mvc.container.CustomHandlerMethodReturnValueHandler;
import cloud.agileframework.mvc.container.FileHandlerMethodReturnValueHandler;
import cloud.agileframework.mvc.container.ReturnHandlerMethodReturnValueHandler;
import cloud.agileframework.mvc.filter.CorsFilter;
import cloud.agileframework.mvc.filter.RequestWrapperFilter;
import cloud.agileframework.mvc.properties.CorsFilterProperties;
import cloud.agileframework.mvc.provider.ArgumentInitHandlerProvider;
import cloud.agileframework.mvc.provider.ArgumentValidationHandlerProvider;
import cloud.agileframework.mvc.view.FileViewResolver;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author 佟盟 on 2017/8/22
 */
@Configuration
public class SpringMvcAutoConfiguration implements WebMvcConfigurer {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CorsFilterProperties corsFilterProperties;
    private final WebMvcProperties webMvcProperties;

    @Autowired
    public SpringMvcAutoConfiguration(CorsFilterProperties corsFilterProperties, WebMvcProperties webMvcProperties) {
        this.corsFilterProperties = corsFilterProperties;
        this.webMvcProperties = webMvcProperties;
    }

    @Bean
    @ConditionalOnClass(CorsFilter.class)
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> corsFilter = new FilterRegistrationBean<>();
        corsFilter.setFilter(new CorsFilter());
        corsFilter.addUrlPatterns("/*");
        corsFilter.addInitParameter("allowOrigin", corsFilterProperties.getAllowOrigin());
        corsFilter.addInitParameter("allowMethods", corsFilterProperties.getAllowMethods());
        corsFilter.addInitParameter("allowCredentials", Boolean.toString(corsFilterProperties.isAllowCredentials()));
        corsFilter.addInitParameter("allowHeaders", corsFilterProperties.getAllowHeaders());
        logger.debug("完成初始化跨域过滤器");
        return corsFilter;
    }

    @Bean
    @ConditionalOnClass(RequestWrapperFilter.class)
    public FilterRegistrationBean<RequestWrapperFilter> requestWrapperFilter() {
        FilterRegistrationBean<RequestWrapperFilter> requestFilter = new FilterRegistrationBean<>();
        requestFilter.setFilter(new RequestWrapperFilter());
        requestFilter.addUrlPatterns("/*");
        logger.debug("完成初始化Request包装过滤器");
        return requestFilter;
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public AgileHandlerMapping agileHandlerMapping() {
        return new AgileHandlerMapping();
    }

    @Bean
    ArgumentInitHandlerProvider argumentInitHandlerProvider() {
        return new ArgumentInitHandlerProvider();
    }

    @Bean
    ArgumentValidationHandlerProvider argumentValidationHandlerProvider() {
        return new ArgumentValidationHandlerProvider();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(agileHandlerMethodArgumentResolver());
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.viewResolver(new FileViewResolver());
        registry.enableContentNegotiation(fastJsonView());
    }

    private FastJsonJsonView fastJsonView() {
        FastJsonJsonView fastJsonView = new FastJsonJsonView();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.PrettyFormat,
                SerializerFeature.WriteDateUseDateFormat,
                SerializerFeature.WriteNonStringValueAsString,
                SerializerFeature.DisableCircularReferenceDetect);

        fastJsonConfig.setDateFormat(webMvcProperties.getFormat().getDateTime());
        fastJsonView.setFastJsonConfig(fastJsonConfig);
        return fastJsonView;
    }

    @Bean
    AgileHandlerMethodArgumentResolver agileHandlerMethodArgumentResolver() {
        return new AgileHandlerMethodArgumentResolver();
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {

        handlers.add(new CustomHandlerMethodReturnValueHandler());
        handlers.add(new ReturnHandlerMethodReturnValueHandler());
        handlers.add(new FileHandlerMethodReturnValueHandler());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CustomHandlerInterceptor());
        registry.addInterceptor(customAsyncHandlerInterceptor());
    }

    @Bean
    CustomAsyncHandlerInterceptor customAsyncHandlerInterceptor() {
        return new CustomAsyncHandlerInterceptor();
    }

}
