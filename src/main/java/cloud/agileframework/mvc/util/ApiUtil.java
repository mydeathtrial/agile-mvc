package cloud.agileframework.mvc.util;

import cloud.agileframework.common.util.clazz.ClassUtil;
import cloud.agileframework.mvc.annotation.AgileService;
import cloud.agileframework.mvc.annotation.NotAPI;
import cloud.agileframework.mvc.container.AgileHandlerMapping;
import cloud.agileframework.spring.util.BeanUtil;
import cloud.agileframework.spring.util.MappingUtil;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author 佟盟 on 2018/8/23
 */
public class ApiUtil {
    private static final Map<String, RequestMappingInfo> API_INFO_CACHE = new HashMap<>();

    public static RequestMappingInfo getApiCache(HttpServletRequest request) {
        HandlerMethod handlerMethod = MappingUtil.matching(request);
        return API_INFO_CACHE.get(handlerMethod.getMethod().toGenericString());
    }

    /**
     * 注册API
     *
     * @param beanName bean名
     * @param bean     bean
     */
    public static void registerApiMapping(String beanName, Object bean) {
        Class<?> realClass = BeanUtil.getBeanClass(bean);
        if (realClass == Class.class) {
            realClass = bean.getClass();
        }

        if (realClass == null || realClass.getAnnotation(NotAPI.class) != null) {
            return;
        }

        AgileService agileService = AnnotationUtils.findAnnotation(realClass, AgileService.class);
        if (agileService == null) {
            return;
        }

        Set<Method> methods = ClassUtil.getAllMethod(realClass);
        for (Method method : methods) {
            if (!Modifier.isPublic(method.getModifiers()) || method.getAnnotation(NotAPI.class) != null) {
                continue;
            }
            registerApiMapping(bean, method, realClass);
        }
    }

    /**
     * 注册API信息
     *
     * @param bean   bean
     * @param method 方法
     * @param clazz  真实类
     */
    private static void registerApiMapping(Object bean, Method method, Class<?> clazz) {
        AgileHandlerMapping agileHandlerMapping = BeanUtil.getBean(AgileHandlerMapping.class);
        //@Mapping信息
        RequestMappingInfo requestMappingInfo = agileHandlerMapping.getMappingForMethod(method, clazz);
        if (requestMappingInfo != null) {
            agileHandlerMapping.registerHandlerMethod(bean, method, requestMappingInfo);
            API_INFO_CACHE.put(method.toGenericString(), requestMappingInfo);
        }
    }
}
