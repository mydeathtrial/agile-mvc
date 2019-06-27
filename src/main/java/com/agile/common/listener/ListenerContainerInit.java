package com.agile.common.listener;

import com.agile.common.annotation.AnnotationProcessor;
import com.agile.common.annotation.ParsingBeanAfter;
import com.agile.common.annotation.ParsingInit;
import com.agile.common.annotation.ParsingMethodAfter;
import com.agile.common.base.AgileApp;
import com.agile.common.util.DateUtil;
import com.agile.common.util.PrintUtil;
import org.springframework.beans.BeansException;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author 佟盟 on 2018/11/9
 */
@Component
public class ListenerContainerInit implements ApplicationListener<WebServerInitializedEvent>, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        annotationHandler();
        applicationContext.getBean(ParsingInit.class).parse();

        PrintUtil.writeMessage("敏捷开发框架 Agile Framework");
        PrintUtil.writeMessage("启动状态", "已成功启动");
        PrintUtil.writeMessage("启动时间", DateUtil.convertToString(new Date(event.getTimestamp()), "yyyy年MM月dd日 HH:mm:ss"));
        PrintUtil.writeMessage("启动端口", event.getWebServer().getPort());
        PrintUtil.writeMessage("启动耗时", AgileApp.getConsumeTime() + "秒");
    }


    /**
     * 处理自定义注解
     */
    private void annotationHandler() {
        AnnotationProcessor.beanAnnotationProcessor(applicationContext, ParsingBeanAfter.class);
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            AnnotationProcessor.methodAnnotationProcessor(applicationContext, beanName, ParsingMethodAfter.class);
        }
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
