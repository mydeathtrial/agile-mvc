package com.agile.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.lang.annotation.Annotation;

/**
 * @author mydeathtrial on 2017/3/10
 */
public final class FactoryUtil {

    private static ApplicationContext applicationContext;

    /**
     * 根据bean名获取bean对象
     *
     * @param beanName bean名
     * @return bean对象
     */
    public static Object getBean(String beanName) throws BeansException {
        try {
            return applicationContext.getBean(beanName.substring(0, 1).toLowerCase() + beanName.substring(1));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据bean名获取bean对象
     *
     * @param clazz bean类型
     * @return bean对象
     */
    public static <T> T getBean(Class<T> clazz) {
        try {
            return applicationContext.getBean(clazz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据类型查询bean对象
     *
     * @param var1 bean类型
     * @return bean对象
     */
    public static String[] getBeanNamesForType(Class<?> var1) {
        try {
            return applicationContext.getBeanNamesForType(var1);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据类型查询bean对象
     *
     * @param var1 bean类型
     * @return bean对象
     */
    public static String[] getBeanNamesForAnnotation(Class<? extends Annotation> var1) {
        try {
            return applicationContext.getBeanNamesForAnnotation(var1);
        } catch (Exception e) {
            return null;
        }
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }
}
