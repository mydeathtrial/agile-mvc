package com.agile.common.container;

import com.agile.common.config.LoggerFactoryConfig;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.filter.CORSFilter;
import com.agile.common.kaptcha.KaptchaServlet;
import com.agile.common.util.DataBaseUtil;
import com.agile.common.util.ObjectUtil;
import com.agile.common.util.PropertiesUtil;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.jolokia.http.AgentServlet;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.IntrospectorCleanupListener;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

/**
 * Created by 佟盟 on 2017/9/27
 */
public class WebInitializer implements WebApplicationInitializer,ServletContextInitializer {

    @Override
    public void onStartup(ServletContext servletContext) {

        new AgileBanner().printBanner(null,null,System.out);

        System.setProperty("webapp.root",servletContext.getRealPath("/"));



        DataBaseUtil.tryLink(PropertiesUtil.getProperty("agile.druid.type"), PropertiesUtil.getProperty("agile.druid.data_base_ip"), PropertiesUtil.getProperty("agile.druid.data_base_port"), PropertiesUtil.getProperty("agile.druid.data_base_name"), PropertiesUtil.getProperty("agile.druid.data_base_username"), PropertiesUtil.getProperty("agile.druid.data_base_password"));

        servletContext.setRequestCharacterEncoding(PropertiesUtil.getProperty("agile.servlet.character","utf-8"));
        servletContext.setResponseCharacterEncoding(PropertiesUtil.getProperty("agile.servlet.character","utf-8"));


        /*
          优先启动log4j2配置
         */
        ConfigurationFactory.setConfigurationFactory(LoggerFactoryConfig.getInstance());

        /*
          编码过滤器
         */
        if(LoggerFactory.COMMON_LOG.isDebugEnabled()){
            LoggerFactory.COMMON_LOG.debug("初始化编码过滤器");
        }
        FilterRegistration.Dynamic encodingFilter = servletContext.addFilter("EncodingFilter", CharacterEncodingFilter.class);
        encodingFilter.setInitParameter("encoding", PropertiesUtil.getProperty("agile.servlet.character"));
        encodingFilter.setInitParameter("forceEncoding", "true");
        encodingFilter.setAsyncSupported(true);
        encodingFilter.addMappingForUrlPatterns(null, false, "/*");

        /*
          Druid监控过滤器
         */
        if(LoggerFactory.COMMON_LOG.isDebugEnabled()){
            LoggerFactory.COMMON_LOG.debug("初始化Druid监控过滤器");
        }
        FilterRegistration.Dynamic druidWebStatFilter = servletContext.addFilter("DruidWebStatFilter", WebStatFilter.class);
        druidWebStatFilter.setInitParameter("exclusions",PropertiesUtil.getProperty("agile.druid.exclusions"));
        druidWebStatFilter.setInitParameter("sessionStatMaxCount",PropertiesUtil.getProperty("agile.druid.session_stat_max_count"));
        druidWebStatFilter.addMappingForUrlPatterns(null, false, "/*");

        /*
          CORS过滤器
         */
        if(LoggerFactory.COMMON_LOG.isDebugEnabled()){
            LoggerFactory.COMMON_LOG.debug("初始化CORS过滤器");
        }
        FilterRegistration.Dynamic corsFilter = servletContext.addFilter("CORSFilter", CORSFilter.class);
        corsFilter.setInitParameter("allowOrigin",PropertiesUtil.getProperty("agile.servlet.allow_origin"));
        corsFilter.setInitParameter("allowMethods",PropertiesUtil.getProperty("agile.servlet.allow_methods"));
        corsFilter.setInitParameter("allowCredentials",PropertiesUtil.getProperty("agile.servlet.allow_credentials"));
        corsFilter.setInitParameter("allowHeaders",PropertiesUtil.getProperty("agile.servlet.allow_headers"));
        corsFilter.addMappingForUrlPatterns(null, false, "/*");

        /*
          Security过滤器
         */
        if(Boolean.valueOf(PropertiesUtil.getProperty("agile.security.enable"))){
            if(LoggerFactory.COMMON_LOG.isDebugEnabled()){
                LoggerFactory.COMMON_LOG.debug("初始化Security过滤链");
            }
            FilterRegistration.Dynamic securityFilter = servletContext.addFilter("springSecurityFilterChain", DelegatingFilterProxy.class);
            if(!ObjectUtil.isEmpty(securityFilter)){
                securityFilter.addMappingForUrlPatterns(null, false, "/*");
            }
        }

        /*
          验证码Servlet
         */
        if(Boolean.valueOf(PropertiesUtil.getProperty("agile.kaptcha.enable"))){
            if(LoggerFactory.COMMON_LOG.isDebugEnabled()){
                LoggerFactory.COMMON_LOG.debug("初始化验证码Servlet");
            }
            ServletRegistration.Dynamic kaptchaServlet = servletContext.addServlet("VerificationCodeServlet", KaptchaServlet.class);
            kaptchaServlet.addMapping(PropertiesUtil.getProperty("agile.kaptcha.url"));
        }

        /*
          DruidServlet
         */
        if(LoggerFactory.COMMON_LOG.isDebugEnabled()){
            LoggerFactory.COMMON_LOG.debug("初始化DruidServlet");
        }
        ServletRegistration.Dynamic druidStatViewServlet = servletContext.addServlet("DruidStatViewServlet", StatViewServlet.class);
        druidStatViewServlet.setInitParameter("resetEnable",PropertiesUtil.getProperty("agile.druid.reset_enable"));
        druidStatViewServlet.setInitParameter("loginUsername",PropertiesUtil.getProperty("agile.druid.manager_name"));
        druidStatViewServlet.setInitParameter("loginPassword",PropertiesUtil.getProperty("agile.druid.manager_password"));
        druidStatViewServlet.addMapping(PropertiesUtil.getProperty("agile.druid.url")+"/*");

        /*
          SpringDispatcherServlet
         */
        ServletRegistration springDispatcherServlet;
        if(servletContext.getServletRegistrations().containsKey("dispatcherServlet")){
            if(LoggerFactory.COMMON_LOG.isDebugEnabled()){
                LoggerFactory.COMMON_LOG.debug("当前环境识别为spring boot启动");
            }
            springDispatcherServlet = servletContext.getServletRegistrations().get("dispatcherServlet");
        }else{
            if(LoggerFactory.COMMON_LOG.isDebugEnabled()){
                LoggerFactory.COMMON_LOG.debug("初始化SpringDispatcherServlet");
            }
            springDispatcherServlet = servletContext.addServlet("SpringDispatcherServlet", DispatcherServlet.class);
        }
        springDispatcherServlet.setInitParameter("contextClass","org.springframework.web.context.support.AnnotationConfigWebApplicationContext");
//        springDispatcherServlet.setInitParameter("contextConfigLocation", ConfigProcessor.contextConfigLocation());
        springDispatcherServlet.setInitParameter("contextConfigLocation", "com.agile.common.config.SpringConfig");
        springDispatcherServlet.setInitParameter("dispatchOptionsRequest","true");
        springDispatcherServlet.addMapping("/*");

        ServletRegistration.Dynamic jolokiaAgent = servletContext.addServlet("jolokia-agent", AgentServlet.class);
        jolokiaAgent.addMapping("/jolokia/*");

        /*
          内存溢出监听
         */
        if(LoggerFactory.COMMON_LOG.isDebugEnabled()){
            LoggerFactory.COMMON_LOG.debug("初始化内存溢出监听");
        }
        servletContext.addListener(IntrospectorCleanupListener.class);

    }
}
