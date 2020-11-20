package cloud.agileframework.mvc.listener;

import cloud.agileframework.mvc.annotation.AnnotationProcessor;
import cloud.agileframework.mvc.annotation.ParsingBeanAfter;
import cloud.agileframework.mvc.annotation.ParsingMethodAfter;
import org.springframework.beans.BeansException;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;

/**
 * @author 佟盟 on 2018/11/9
 */
public class ListenerContainerInit implements ApplicationListener<WebServerInitializedEvent>, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        annotationHandler();
        ProjectContextHolder.event(event);
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
