package com.agile.common.container;

import com.agile.common.annotation.AnnotationProcessor;
import com.agile.common.annotation.ParsingMethodBefore;
import com.agile.common.util.ApiUtil;
import com.agile.common.viewResolver.JsonViewResolver;
import com.agile.common.viewResolver.JumpViewResolver;
import com.agile.common.viewResolver.PlainViewResolver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.util.ProxyUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 佟盟 on 2018/1/19
 * bean初始化对象过程
 */
@Component
public class CustomBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        AnnotationProcessor.methodAnnotationProcessor(applicationContext, beanName, bean, ParsingMethodBefore.class);
        ApiUtil.registerApiMapping(beanName, bean);
        if (ProxyUtils.getUserClass(bean) == ContentNegotiatingViewResolver.class) {
            ContentNegotiatingViewResolver contentNegotiatingViewResolver = ((ContentNegotiatingViewResolver) bean);
            List<ViewResolver> list = new ArrayList<>();
            list.add(new JsonViewResolver());
            list.add(new PlainViewResolver());
            list.add(new JumpViewResolver());

            contentNegotiatingViewResolver.setViewResolvers(list);
        }
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
