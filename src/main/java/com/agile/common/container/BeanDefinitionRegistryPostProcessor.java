package com.agile.common.container;

import com.agile.common.annotation.AnnotationProcessor;
import com.agile.common.annotation.ParsingBeanBefore;
import com.agile.common.util.FactoryUtil;
import com.agile.common.util.PropertiesUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Component;

import static org.springframework.context.support.PropertySourcesPlaceholderConfigurer.LOCAL_PROPERTIES_PROPERTY_SOURCE_NAME;

/**
 * @author 佟盟 on 2018/1/19
 * bean定义过程
 */
@Component
public class BeanDefinitionRegistryPostProcessor implements EnvironmentAware, PriorityOrdered, org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor, ApplicationContextAware {
    private ApplicationContext applicationContext;
    private Environment environment;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        this.envProcessor();
        this.factoryUtilProcessor();
        this.beanAnnotationProcessor();

    }

    /**
     * 1：配置变量处理
     */
    private void envProcessor() {
        PropertySource<?> localPropertySource = new PropertiesPropertySource(LOCAL_PROPERTIES_PROPERTY_SOURCE_NAME, PropertiesUtil.getProperties());
        ((StandardEnvironment) environment).getPropertySources().addLast(localPropertySource);
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
