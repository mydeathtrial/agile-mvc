package com.agile.common.container;

import com.agile.common.annotation.AnnotationProcessor;
import com.agile.common.util.*;
import com.agile.common.mvc.model.dao.Dao;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.util.ProxyUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by 佟盟 on 2018/1/19
 * bean初始化对象过程
 */
@Component
public class BeanPostProcessor implements org.springframework.beans.factory.config.BeanPostProcessor,ApplicationContextAware{
    private ApplicationContext applicationContext;
    private final Dao dao;

    @Autowired
    public BeanPostProcessor(Dao dao) {
        this.dao = dao;
    }

    @Override
    @Transactional
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        methodAnnotationProcessor(bean,beanName);
        classAnnotationProcessor(bean);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        initRequestMapping(bean,beanName);
        return bean;
    }

    /**
     * 处理自定义注解
     */
    private void methodAnnotationProcessor(Object bean,String beanName) {
        Method[] methods =  ReflectionUtils.getUniqueDeclaredMethods(bean.getClass());
        for(int i = 0; i < methods.length; i++){
            Method method = methods[i];
            Annotation[] annotations = method.getAnnotations();
            for(int j = 0 ; j < annotations.length;j++){
                Class<? extends Annotation> clazz = annotations[j].annotationType();
                if(ArrayUtil.contains(AnnotationProcessor.methodAnnotations,clazz)){
                    try {
                        Method annotationMethod = AnnotationProcessor.class.getDeclaredMethod(clazz.getSimpleName(), clazz, Object.class,Method.class);
                        annotationMethod.setAccessible(true);
                        ReflectionUtils.invokeMethod(annotationMethod,applicationContext.getBean(AnnotationProcessor.class),annotations[j],bean,method);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 处理自定义注解
     */
    private void classAnnotationProcessor(Object bean){
        Class<?> clazz = bean.getClass();
        Annotation[] annotations = clazz.getDeclaredAnnotations();
        for(int i = 0 ; i < annotations.length;i++){
            Class<? extends Annotation> annotationClazz = annotations[i].annotationType();
            if(ArrayUtil.contains(AnnotationProcessor.afterClassAnnotations,annotationClazz)){
                try {
                    Method annotationMethod = AnnotationProcessor.class.getDeclaredMethod(annotationClazz.getSimpleName(), annotationClazz, Object.class);
                    annotationMethod.setAccessible(true);
                    ReflectionUtils.invokeMethod(annotationMethod,applicationContext.getBean(AnnotationProcessor.class),annotations[i],bean);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 检测任务
     */
    private void initRequestMapping(Object bean,String beanName){
        Class<?> realClass = ProxyUtils.getUserClass(bean);
        if(realClass == null)return;
        Service service = realClass.getAnnotation(Service.class);
        if(service == null)return;
        //service缓存
        APIUtil.addServiceCache(beanName,bean);
        if(service.value().length()>0){
            APIUtil.addServiceCache(service.value(),bean);
            APIUtil.addServiceCache(StringUtil.toLowerName(bean.getClass().getSimpleName()),bean);
        }

        //method缓存
        Method[] methods =  realClass.getMethods();
        for(int i = 0; i < methods.length; i++){
            Method method = methods[i];
            method.setAccessible(true);
            APIUtil.addMappingInfoCache(bean,method,realClass);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
