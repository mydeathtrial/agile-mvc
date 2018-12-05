package com.agile.common.annotation;

import com.agile.common.util.APIUtil;
import com.agile.common.util.StringUtil;
import org.springframework.aop.support.AopUtils;
import org.springframework.data.util.ProxyUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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
//@Component
public class ParsingMapping implements ParsingMethodBefore {
    @Override
    public void parsing(String beanName,Object object, Method method) {
//        Class<?> realClass = ProxyUtils.getUserClass(object);
//        if(realClass == null)return;
//        Service service = realClass.getAnnotation(Service.class);
//        if(service == null)return;
//        //service缓存
//        APIUtil.addServiceCache(beanName,object);
//        if(service.value().length()>0){
//            APIUtil.addServiceCache(service.value(),object);
//            APIUtil.addServiceCache(StringUtil.toLowerName(object.getClass().getSimpleName()),object);
//        }
//
//        //method缓存
//        APIUtil.addMappingInfoCache(beanName,object,method,realClass);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return Mapping.class;
    }
}
