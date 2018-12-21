package com.agile.common.util;

import com.agile.common.annotation.NotAPI;
import com.agile.common.base.APIInfo;
import com.agile.common.container.MappingHandlerMapping;
import com.agile.common.factory.LoggerFactory;
import org.springframework.data.util.ProxyUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 佟盟 on 2018/8/23
 */
public class APIUtil {
    private static Map<String, Object> serviceCache = new HashMap<>();
    private static List<APIInfo> apiInfoCache = new ArrayList<>();
    private static MappingHandlerMapping mappingHandlerMapping;

    public static HandlerMethod getApiCache(HttpServletRequest request) {
        if (mappingHandlerMapping == null) {
            return null;
        }
        try {
            HandlerExecutionChain handlerExecutionChain = getMappingHandlerMapping().getHandler(request);
            if (handlerExecutionChain != null) {
                return (HandlerMethod) (handlerExecutionChain.getHandler());
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static void addMappingInfoCache(String beanName, Object bean, Method method, Class clazz) {
        RequestMappingInfo requestMappingInfo = APIUtil.getMappingHandlerMapping().getMappingForMethod(method, clazz);
        if (requestMappingInfo != null) {
            getMappingHandlerMapping().registerHandlerMethod(bean, method, requestMappingInfo);
            if (LoggerFactory.getCommonLog().isDebugEnabled()) {
                PatternsRequestCondition patterns = requestMappingInfo.getPatternsCondition();
                if (patterns != null) {
                    for (String path : patterns.getPatterns()) {
                        LoggerFactory.getCommonLog().debug(String.format("[Mapping:%s][Service:%s][method:%s]", path, beanName, method.getName()));
                    }
                }
            }
        }

        addApiInfoCache(new APIInfo(bean, method, beanName, requestMappingInfo));
    }

    public static void addMappingInfoCache(String beanName, Object bean) {
        Class<?> realClass = ProxyUtils.getUserClass(bean);
        if (realClass == null || realClass.getAnnotation(NotAPI.class) != null) {
            return;
        }
        Service service = realClass.getAnnotation(Service.class);
        if (service == null) {
            return;
        }
        Method[] methods = realClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getParameters().length > 0 || method.getAnnotation(NotAPI.class) != null) {
                continue;
            }
            addMappingInfoCache(beanName, bean, method, realClass);
        }
    }

    public static void addApiInfoCache(APIInfo apiInfo) {
        apiInfoCache.add(apiInfo);
    }

    public static List<APIInfo> getApiInfoCache() {
        return apiInfoCache;
    }

    public static MappingHandlerMapping getMappingHandlerMapping() {
        if (mappingHandlerMapping == null) {
            mappingHandlerMapping = new MappingHandlerMapping();
            mappingHandlerMapping.afterPropertiesSet();
        }
        return mappingHandlerMapping;
    }

    public static Object getServiceCache(String key) {
        return serviceCache.get(key);
    }

    public static void addServiceCache(String key, Object o) {
        serviceCache.put(key, o);
    }
}
