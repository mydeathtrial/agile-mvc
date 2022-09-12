package cloud.agileframework.mvc.container;

import cloud.agileframework.mvc.annotation.AnnotationProcessor;
import cloud.agileframework.mvc.annotation.ParsingMethodBefore;
import cloud.agileframework.mvc.util.ApiUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author 佟盟 on 2018/1/19
 * bean初始化对象过程
 */
public class CustomBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        AnnotationProcessor.methodAnnotationProcessor(applicationContext, beanName, ParsingMethodBefore.class);
        ApiUtil.registerApiMapping(bean);
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
