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
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
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
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        FastJsonConfig config = new FastJsonConfig();
        //Long类型转String类型
        SerializeConfig serializeConfig = SerializeConfig.globalInstance;
        // ToStringSerializer 是这个包 com.alibaba.fastjson.serializer.ToStringSerializer
        serializeConfig.put(BigInteger.class, ToStringSerializer.instance);
        serializeConfig.put(Long.class, ToStringSerializer.instance);
        serializeConfig.put(Long.TYPE, ToStringSerializer.instance);

        config.setSerializeConfig(serializeConfig);
        config.setSerializerFeatures(
                // 保留map空的字段
                SerializerFeature.WriteMapNullValue,
                // 将String类型的null转成""
                SerializerFeature.WriteNullStringAsEmpty,
                // 将Number类型的null转成0
                SerializerFeature.WriteNullNumberAsZero,
                // 将List类型的null转成[]
                SerializerFeature.WriteNullListAsEmpty,
                // 将Boolean类型的null转成false
                SerializerFeature.WriteNullBooleanAsFalse,
                //日期格式转换
                SerializerFeature.WriteDateUseDateFormat,
                // 避免循环引用
                SerializerFeature.DisableCircularReferenceDetect
        );
        config.setSerializeFilters(VALUE_FILTER);
        converter.setFastJsonConfig(config);
        converter.setDefaultCharset(StandardCharsets.UTF_8);
        // 解决中文乱码问题，相当于在Controller上的@RequestMapping中加了个属性produces = "application/json"
        List<MediaType> mediaTypeList = new ArrayList<>();
        mediaTypeList.add(MediaType.APPLICATION_JSON);
        converter.setSupportedMediaTypes(mediaTypeList);

        converters.add(new ByteArrayHttpMessageConverter());
        // @ResponseBody 解决乱码
        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new AllEncompassingFormHttpMessageConverter());
        converters.add(converter);
    }

    public static final ValueFilter VALUE_FILTER = (obj, s, v) -> {
        if (v == null) {
            return "";
        }
        return v;
    };

    private FastJsonJsonView fastJsonView() {
        FastJsonJsonView fastJsonView = new FastJsonJsonView();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.PrettyFormat,
                SerializerFeature.WriteDateUseDateFormat,
                SerializerFeature.DisableCircularReferenceDetect);

        SerializeConfig serializeConfig = SerializeConfig.globalInstance;
        serializeConfig.put(Long.class, ToStringSerializer.instance);
        serializeConfig.put(Long.TYPE, ToStringSerializer.instance);
        fastJsonConfig.setSerializeConfig(serializeConfig);
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
