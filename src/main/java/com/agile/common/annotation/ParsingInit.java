package com.agile.common.annotation;

import com.agile.common.util.CollectionsUtil;
import com.agile.common.util.ObjectUtil;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    public void parsing(String beanName, Object bean, Method method) {
        Init init = (Init) method.getAnnotation(getAnnotation());
        if (!ObjectUtil.isEmpty(init)) {
            inits.add(InitApiInfo.builder().order(init.order()).bean(bean).method(method).build());
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
        if (!ObjectUtil.isEmpty(init)) {
            try {
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
            parse(initApiInfo.bean, initApiInfo.method);
        }
    }

    /**
     * 初始化API信息
     */
    @Data
    @Builder
    private static class InitApiInfo {
        private int order;
        private Object bean;
        private Method method;
    }
}
