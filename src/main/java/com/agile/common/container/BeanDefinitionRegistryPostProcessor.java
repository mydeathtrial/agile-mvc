package com.agile.common.container;

import com.agile.common.annotation.AnnotationProcessor;
import com.agile.common.annotation.ParsingBeanBefore;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

/**
 * @author 佟盟 on 2018/1/19
 * bean定义过程
 */
@Component
public class BeanDefinitionRegistryPostProcessor implements PriorityOrdered, org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor, ApplicationContextAware {
    private ApplicationContext applicationContext;
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        this.beanAnnotationProcessor();
    }

    /**
     * 1：处理自定义注解
     */
    private void beanAnnotationProcessor() {
        AnnotationProcessor.beanAnnotationProcessor(applicationContext, ParsingBeanBefore.class);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
