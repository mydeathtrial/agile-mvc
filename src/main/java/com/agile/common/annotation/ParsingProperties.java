package com.agile.common.annotation;

import com.agile.common.util.ArrayUtil;
import com.agile.common.util.ClassUtil;
import com.agile.common.util.ObjectUtil;
import com.agile.common.util.PropertiesUtil;
import com.agile.common.util.StringUtil;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：properties/yml...配置文件自动映射为javaObject
 * <p>创建时间：2018/11/28<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
@Component
public class ParsingProperties implements ParsingBeanBefore {

    @Override
    public void parsing(String beanName, Object bean) {
        Properties annotation = (Properties) bean.getClass().getAnnotation(getAnnotation());
        if (annotation == null) {
            return;
        }
        try {
            setProperties(bean, annotation.prefix());
        } catch (Exception ignored) {
        }
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return Properties.class;
    }

    /**
     * Properties注解解析器的解析过程
     *
     * @param target 目标对象
     * @param prefix 配置文件前缀
     * @throws IllegalAccessException 异常
     * @throws InstantiationException 异常
     */
    public void setProperties(Object target, String prefix) throws IllegalAccessException, InstantiationException {
        if (ObjectUtil.isEmpty(target)) {
            return;
        }
        Class<?> targetClass = target.getClass();
        Field[] fields = targetClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            Class<?> type = field.getType();
            String name = field.getName();
            if (type.isAssignableFrom(List.class)) {
                Type genericType = field.getGenericType();
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                if (ArrayUtil.isEmpty(typeArguments)) {
                    continue;
                }
                Class innerClass = (Class) typeArguments[0];

                List<Object> list = new ArrayList<>();
                int j = 0;
                boolean hasNext = true;

                if (ClassUtil.canCastClass(innerClass)) {
                    while (hasNext) {
                        String key = String.format("%s.%s[%s]", prefix, name, j);
                        if (j == 0) {
                            key = PropertiesUtil.getProperties().containsKey(key) ? key : prefix + "." + name;
                        }
                        if (PropertiesUtil.getProperties().containsKey(key)) {
                            list.add(PropertiesUtil.getProperty(key, innerClass));
                            j++;
                        } else {
                            hasNext = false;
                        }
                    }
                } else {
                    while (hasNext) {
                        Object temp = innerClass.newInstance();
                        String key = String.format("%s.%s[%s]", prefix, name, j);
                        setProperties(temp, key);
                        if (ObjectUtil.compareValue(temp, innerClass.newInstance()) && j == 0) {
                            setProperties(temp, String.format("%s.%s", prefix, name));
                        } else if (ObjectUtil.compareValue(temp, innerClass.newInstance()) && j != 0) {
                            hasNext = false;
                            continue;
                        }
                        j++;
                        list.add(temp);
                    }
                }
                field.set(target, list);
            } else {
                String key = prefix + "." + StringUtil.camelToUnderline(name);
                if (ClassUtil.canCastClass(type)) {
                    if (PropertiesUtil.getProperties().containsKey(key)) {
                        field.set(target, PropertiesUtil.getProperty(key, type));
                    }
                } else {
                    Object temp = type.newInstance();
                    setProperties(temp, key);
                    field.set(target, temp);
                }
            }
        }
    }
}
