package com.agile.common.annotation;

import com.agile.common.util.FactoryUtil;
import org.springframework.context.ApplicationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 描述：注解解析器.
 * <p>创建时间：2018/11/29<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
public class AnnotationProcessor {
    /**
     * 方法注解解析器触发器.
     *
     * @param applicationContext spring容器对象
     * @param beanName           beanName
     * @param annotationClass    注解类型
     */
    public static void methodAnnotationProcessor(ApplicationContext applicationContext, String beanName, Class annotationClass) {
        String[] annotationParsings = applicationContext.getBeanNamesForType(annotationClass);
        for (String parsingName : annotationParsings) {
            Parsing parsing = (Parsing) applicationContext.getBean(parsingName);
            Class<? extends Annotation> annotation = parsing.getAnnotation();
            if (annotation == null) {
                continue;
            }
            Class beanClass = FactoryUtil.getBeanClass(beanName);
            if (beanClass != null) {
                methodAnnotationProcessor(beanName, beanClass, parsing);
            }
        }

    }

    /**
     * bean 类注解解析器触发器.
     *
     * @param applicationContext spring容器对象
     * @param annotationClass    注解类型
     */
    public static void beanAnnotationProcessor(ApplicationContext applicationContext, Class annotationClass) {
        String[] annotationParsings = applicationContext.getBeanNamesForType(annotationClass);
        for (String parsingName : annotationParsings) {
            Parsing parsing = (Parsing) applicationContext.getBean(parsingName);
            Class<? extends Annotation> annotation = parsing.getAnnotation();
            if (annotation == null) {
                continue;
            }

            Map<String, Object> beans = applicationContext.getBeansWithAnnotation(annotation);
            for (Map.Entry<String, Object> map : beans.entrySet()) {
                String beanName = map.getKey();
                Object bean = map.getValue();
                if (parsing instanceof ParsingBeanAfter) {
                    ((ParsingBeanAfter) parsing).parsing(beanName, bean);
                } else if (parsing instanceof ParsingBeanBefore) {
                    ((ParsingBeanBefore) parsing).parsing(beanName, bean);
                }
            }
        }
    }

    /**
     * 方法注解解析器触发器.
     *
     * @param beanName  beanName
     * @param realClass 被spring代理的真实类
     * @param parsing   目标注解解析器
     */
    private static void methodAnnotationProcessor(String beanName, Class realClass, Parsing parsing) {
        Method[] methods = realClass.getDeclaredMethods();
        for (Method method : methods) {
            method.setAccessible(true);
            Class<? extends Annotation> annotation = parsing.getAnnotation();
            if (annotation == null || method.getAnnotation(annotation) == null) {
                continue;
            }
            if (parsing instanceof ParsingMethodAfter) {
                ((ParsingMethodAfter) parsing).parsing(beanName, method);
            } else if (parsing instanceof ParsingMethodBefore) {
                ((ParsingMethodBefore) parsing).parsing(beanName, method);
            }
        }
    }
}
