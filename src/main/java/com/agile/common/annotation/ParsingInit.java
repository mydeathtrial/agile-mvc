package com.agile.common.annotation;

import com.agile.common.util.ObjectUtil;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
    @Override
    public void parsing(String beanName, Object bean, Method method) {
        parse(bean, method);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return Init.class;
    }

    @Transactional
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
}
