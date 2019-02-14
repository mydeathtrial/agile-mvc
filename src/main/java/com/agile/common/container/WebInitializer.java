package com.agile.common.container;

import com.agile.common.factory.LoggerFactory;
import com.agile.common.properties.ServletProperties;
import com.agile.common.util.FactoryUtil;
import org.jolokia.http.AgentServlet;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.util.IntrospectorCleanupListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

/**
 * @author 佟盟 on 2017/9/27
 */
public class WebInitializer implements WebApplicationInitializer, ServletContextInitializer {

    @Override
    public void onStartup(ServletContext servletContext) {
        ServletRegistration.Dynamic jolokiaAgent = servletContext.addServlet("jolokia-agent", AgentServlet.class);
        jolokiaAgent.addMapping("/jolokia/*");

        if (LoggerFactory.COMMON_LOG.isDebugEnabled()) {
            LoggerFactory.COMMON_LOG.debug("完成初始化内存溢出监听");
        }
        servletContext.addListener(IntrospectorCleanupListener.class);
    }
}
