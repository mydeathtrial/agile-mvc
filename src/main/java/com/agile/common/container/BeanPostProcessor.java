package com.agile.common.container;

import com.agile.common.annotation.AnnotationProcessor;
import com.agile.common.util.APIUtil;
import com.agile.common.util.ArrayUtil;
import com.agile.common.util.ObjectUtil;
import com.agile.common.mvc.model.dao.Dao;
import com.agile.common.util.PropertiesUtil;
import com.agile.mvc.entity.SysTaskTargetEntity;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
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
        if(PropertiesUtil.getProperty("agile.task.enable",boolean.class)){
            initSysTaskTarget(bean);
        }
        methodAnnotationProcessor(bean,beanName);
        classAnnotationProcessor(bean);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
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
    void initSysTaskTarget(Object bean){
        Class<?> clazz = bean.getClass();
        Service service = clazz.getAnnotation(Service.class);
        if(service == null)return;

        Method[] methods =  clazz.getMethods();
        for(int i = 0; i < methods.length; i++){
            Method method = methods[i];
            String methodName = method.getName();

            addApiCache(bean.getClass().getName(),methodName,method);

//            String[] excludeMethod = {"save","delete","update","query"};
//            if(ArrayUtil.contains(excludeMethod,methodName))continue;
            String id = clazz.getName() + "." + methodName;
            SysTaskTargetEntity entity = dao.findOne(SysTaskTargetEntity.class, id);
            if(!ObjectUtil.isEmpty(entity))continue;
            SysTaskTargetEntity sysTaskTargetEntity = new SysTaskTargetEntity();
            sysTaskTargetEntity.setSysTaskTargetId(id);
            sysTaskTargetEntity.setTargetPackage(clazz.getPackage().getName());
            sysTaskTargetEntity.setTargetClass(clazz.getSimpleName());
            sysTaskTargetEntity.setTargetMethod(methodName);
            sysTaskTargetEntity.setName(id);
            dao.update(sysTaskTargetEntity);
        }
    }

    /**
     * 初始化API Hash表
     * @param className bean名
     * @param methodName 方法名
     * @param method 方法
     */
    private void addApiCache(String className,String methodName,Method method){
        method.setAccessible(true);
        APIUtil.addApiCache(String.format("%s.%s",className,methodName),method);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
