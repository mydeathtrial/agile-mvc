package com.agile.common.config;

import com.agile.common.properties.SpringMVCProperties;
import com.agile.common.viewResolver.PlainViewResolver;
import com.agile.common.viewResolver.JsonViewResolver;
import com.agile.common.viewResolver.JumpViewResolver;
import com.agile.common.viewResolver.XmlViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 佟盟 on 2017/8/22
 */
@Configuration
@EnableWebMvc
public class SpringMvcConfig implements WebMvcConfigurer {

    private static Map<String, MediaType> map = new HashMap<>();
    static {
        map.put("plain",MediaType.TEXT_PLAIN);
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.setOrder(-1).addResourceHandler("/static/**","/favicon.ico")
                .addResourceLocations("classpath:com/agile/static/","classpath:com/agile/static/img/","classpath:com/agile/static/plus/jquery/","classpath:com/agile/static/plus/swagger/");
    }

    /**
     * 视图解析器
     * 配置视图解析器视图列表
     * @param manager 略
     * @return 视图解析器
     */
    @Bean
    public ViewResolver contentNegotiatingViewResolver(ContentNegotiationManager manager){
        List<ViewResolver> list = new ArrayList<>();
        list.add(new JsonViewResolver());
        list.add(new XmlViewResolver());
        list.add(new PlainViewResolver());
        list.add(new JumpViewResolver());

        ContentNegotiatingViewResolver viewResolver = new ContentNegotiatingViewResolver();
        viewResolver.setContentNegotiationManager(manager);
        viewResolver.setViewResolvers(list);
        return viewResolver;
    }

    /**
     * 文件上传配置
     * @return 文件上传下载解析器
     */
    @Bean
    public CommonsMultipartResolver contentCommonsMultipartResolver(){
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setMaxUploadSize(SpringMVCProperties.getUpload().getMaxUploadSize());
        resolver.setDefaultEncoding(SpringMVCProperties.getUpload().getDefaultEncoding());
        return resolver;
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.ignoreAcceptHeader(false)
                .favorPathExtension(true)
                .favorParameter(false)
                .defaultContentType(MediaType.APPLICATION_JSON_UTF8)
                .mediaTypes(map);
    }

    @Bean
    ThreadPoolTaskScheduler threadPoolTaskScheduler(){
        return new ThreadPoolTaskScheduler();
    }

    @Bean
    ResourceBundleMessageSource resourceBundleMessageSource(){
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setDefaultEncoding("UTF-8");
        resourceBundleMessageSource.setBasename("com.agile.conf.message");
        return resourceBundleMessageSource;
    }

    public static Map<String, MediaType> getMap() {
        return map;
    }
}
