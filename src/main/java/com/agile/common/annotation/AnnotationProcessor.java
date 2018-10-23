package com.agile.common.annotation;

import com.agile.common.util.*;
import org.springframework.stereotype.Component;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 佟盟 on 2018/1/5
 */
@Component
public class AnnotationProcessor {
    /**
     * 准备处理的注解
     */
    public static Class[] beforeClassAnnotations = {Properties.class};
    public static Class[] afterClassAnnotations = {};
    public static Class[] methodAnnotations = {Init.class};

    void Init(Init init, Object bean,Method method){
        method.setAccessible(true);
        if(!ObjectUtil.isEmpty(init)){
            try {
                method.invoke(bean);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    void Properties(Properties properties, Object bean) throws InstantiationException, IllegalAccessException {
        if(ObjectUtil.isEmpty(properties))return;
        String prefix = properties.prefix();
        setProperties(bean,prefix);
    }

    private void setProperties(Object target,String prefix) throws IllegalAccessException, InstantiationException {
        if(ObjectUtil.isEmpty(target))return;
        Class<?> targetClass = target.getClass();
        Field[] fields = targetClass.getDeclaredFields();
        for(int i = 0 ; i < fields.length;i++){
            Field field = fields[i];
            field.setAccessible(true);
            Class<?> type = field.getType();
            String name = field.getName();
            if(type.isAssignableFrom(List.class)){
                Type genericType = field.getGenericType();
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                if(ArrayUtil.isEmpty(typeArguments))continue;
                Class innerClass = (Class)typeArguments[0];

                List<Object> list = new ArrayList<>();
                int j = 0 ;
                boolean hasNext = true;

                if(ClassUtil.isCustomClass(innerClass)){
                    while (hasNext){
                        String key = String.format("%s.%s[%s]",prefix,name,j);
                        if(j==0){
                            key = PropertiesUtil.properties.containsKey(key)?key:prefix+"."+name;
                        }
                        if(PropertiesUtil.properties.containsKey(key)){
                            list.add(PropertiesUtil.getProperty(key,innerClass));
                            j++;
                        }else{
                            hasNext = false;
                        }
                    }
                }else{
                    while (hasNext){
                        Object temp = innerClass.newInstance();
                        String key = String.format("%s.%s[%s]",prefix,name,j);
                        setProperties(temp,key);
                        if(ObjectUtil.compareValue(temp,innerClass.newInstance()) && j==0){
                            setProperties(temp,String.format("%s.%s",prefix,name));
                        }else if(ObjectUtil.compareValue(temp,innerClass.newInstance()) && j!=0){
                            hasNext = false;
                            continue;
                        }
                        j++;
                        list.add(temp);
                    }
                }
                field.set(target,list);
            }else{
                String key = prefix + "." + StringUtil.camelToUnderline(name);
                if(ClassUtil.isCustomClass(type)){
                    if(PropertiesUtil.properties.containsKey(key))
                    field.set(target,PropertiesUtil.getProperty(key,type));
                }else{
                    Object temp = type.newInstance();
                    setProperties(temp,key);
                    field.set(target,temp);
                }
            }
        }
    }
}
