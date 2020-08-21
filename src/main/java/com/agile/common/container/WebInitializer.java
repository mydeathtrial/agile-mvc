package com.agile.common.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.util.IntrospectorCleanupListener;

import javax.servlet.ServletContext;

/**
 * @author 佟盟 on 2017/9/27
 */
public class WebInitializer implements WebApplicationInitializer, ServletContextInitializer {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public void onStartup(ServletContext servletContext) {

        if (logger.isDebugEnabled()) {
            logger.debug("完成初始化内存溢出监听");
        }
        servletContext.addListener(IntrospectorCleanupListener.class);
    }
}
