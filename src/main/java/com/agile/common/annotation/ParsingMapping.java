package com.agile.common.annotation;

import com.agile.common.util.APIUtil;
import org.springframework.data.util.ProxyUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 描述：
 * <p>创建时间：2018/11/29<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
@Component
public class ParsingMapping implements ParsingMethodBefore {
    @Override
    public void parsing(String beanName, Object object, Method method) {
        Class<?> realClass = ProxyUtils.getUserClass(object);
        if (realClass.getAnnotation(NotAPI.class) != null || method.getAnnotation(NotAPI.class) != null) {
            return;
        }
        APIUtil.addMappingInfoCache(beanName, object);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return Mapping.class;
    }
}
