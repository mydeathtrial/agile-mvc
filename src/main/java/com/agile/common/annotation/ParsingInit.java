package com.agile.common.annotation;

import cloud.agileframework.common.util.collection.CollectionsUtil;
import cloud.agileframework.spring.util.spring.BeanUtil;
import com.agile.common.factory.LoggerFactory;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：框架启动后立即执行
 * <p>创建时间：2018/11/28<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
@Component
public class ParsingInit implements ParsingMethodAfter {
    private List<InitApiInfo> inits = new ArrayList<>();

    @Override
    public void parsing(String beanName, Method method) {
        Init init = (Init) method.getAnnotation(getAnnotation());
        if (!ObjectUtils.isEmpty(init)) {
            inits.add(InitApiInfo.builder().order(init.order()).beanName(beanName).method(method).build());
        }
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return Init.class;
    }

    /**
     * Init注解解析过程
     *
     * @param bean   bean
     * @param method init注解下的方法
     */
    @Transactional(rollbackFor = Exception.class)
    public void parse(Object bean, Method method) {

        method.setAccessible(true);
        Init init = (Init) method.getAnnotation(getAnnotation());
        if (!ObjectUtils.isEmpty(init)) {
            try {
                LoggerFactory.COMMON_LOG.info(String.format("启动初始化方法:%s\n", method.toGenericString()));
                method.invoke(bean);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.getTargetException().printStackTrace();
            }
        }
    }

    public void parse() {
        CollectionsUtil.sort(inits, "order");
        for (InitApiInfo initApiInfo : inits) {
            try {
                parse(BeanUtil.getBean(initApiInfo.beanName), initApiInfo.method);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 初始化API信息
     */
    @Data
    @Builder
    private static class InitApiInfo {
        private int order;
        private String beanName;
        private Method method;
    }
}
