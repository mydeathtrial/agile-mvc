package com.agile.common.listener;

import com.agile.common.annotation.AnnotationProcessor;
import com.agile.common.annotation.ParsingBeanAfter;
import com.agile.common.annotation.ParsingMethodAfter;
import com.agile.common.util.DateUtil;
import com.agile.common.util.PrintUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author 佟盟 on 2018/11/9
 */
@Component
public class ListenerContainerRefreshed implements ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        annotationHandler();

        long current = contextRefreshedEvent.getTimestamp();
        String currentTime = DateUtil.convertToString(new Date(current), "yyyy年MM月dd日 HH:mm:ss");
        PrintUtil.writeln("\n :: 敏捷开发框架 Agile Framework :: ", PrintUtil.CYAN);
        PrintUtil.write(" :: 启动状态 :: ", PrintUtil.CYAN);
        PrintUtil.writeln("已成功启动", PrintUtil.YELLOW);
        PrintUtil.write(" :: 启动时间 :: ", PrintUtil.CYAN);
        PrintUtil.writeln(currentTime, PrintUtil.YELLOW);
    }

    /**
     * 处理自定义注解
     */
    private void annotationHandler() {
        AnnotationProcessor.beanAnnotationProcessor(applicationContext, ParsingBeanAfter.class);
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            AnnotationProcessor.methodAnnotationProcessor(applicationContext, beanName, applicationContext.getBean(beanName), ParsingMethodAfter.class);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
