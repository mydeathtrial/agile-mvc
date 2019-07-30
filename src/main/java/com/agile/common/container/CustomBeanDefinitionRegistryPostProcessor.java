package com.agile.common.container;

import com.agile.common.annotation.AnnotationProcessor;
import com.agile.common.annotation.ParsingBeanBefore;
import com.agile.common.util.FactoryUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author 佟盟 on 2018/1/19
 * bean定义过程
 */
@Component
public class CustomBeanDefinitionRegistryPostProcessor implements EnvironmentAware, PriorityOrdered, BeanDefinitionRegistryPostProcessor, ApplicationContextAware {
    private ApplicationContext applicationContext;
    private Environment environment;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        this.factoryUtilProcessor();
        this.beanAnnotationProcessor();

    }

    /**
     * 2：bean工厂工具处理
     */
    private void factoryUtilProcessor() {
        FactoryUtil.setApplicationContext(applicationContext);
    }

    /**
     * 3：处理自定义注解
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

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
