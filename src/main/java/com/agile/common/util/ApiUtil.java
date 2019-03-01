package com.agile.common.util;

import com.agile.common.annotation.NotAPI;
import com.agile.common.base.ApiInfo;
import com.agile.common.container.MappingHandlerMapping;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.mvc.service.ServiceInterface;
import org.springframework.data.util.ProxyUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author 佟盟 on 2018/8/23
 */
public class ApiUtil {
    private static Map<String, ApiInfo> apiInfoCache = new HashMap<>();
    private static MappingHandlerMapping mappingHandlerMapping;

    public static ApiInfo getApiCache(HttpServletRequest request) {
        if (mappingHandlerMapping == null) {
            return null;
        }
        try {
            HandlerExecutionChain handlerExecutionChain = getMappingHandlerMapping().getHandler(request);
            if (handlerExecutionChain != null) {
                HandlerMethod handler = (HandlerMethod) (handlerExecutionChain.getHandler());
                return getApiInfoCache(handler.getBean(), handler.getMethod());
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private static void addMappingInfoCache(String beanName, Object bean, Method method, RequestMappingInfo requestMappingInfo) {
        if (requestMappingInfo == null) {
            return;
        }

        addApiInfoCache(bean, method, beanName, requestMappingInfo);
    }

    private static void printLog(String beanName, Method method, RequestMappingInfo requestMappingInfo) {
        //打印API信息
        if (LoggerFactory.COMMON_LOG.isDebugEnabled()) {
            PatternsRequestCondition patterns = requestMappingInfo.getPatternsCondition();
            if (patterns != null) {
                for (String path : patterns.getPatterns()) {
                    LoggerFactory.COMMON_LOG.debug(String.format("[Mapping:%s][Service:%s][method:%s]", path, beanName, method.getName()));
                }
            }
        }
    }

    /**
     * 注册API
     *
     * @param beanName bean名
     * @param bean     bean
     */
    public static void registerApiMapping(String beanName, Object bean) {
        Class<?> realClass = ProxyUtils.getUserClass(bean);
        if (realClass == Class.class) {
            realClass = bean.getClass();
        }
        if (realClass == null || realClass.getAnnotation(NotAPI.class) != null) {
            return;
        }
        if (!ServiceInterface.class.isAssignableFrom(realClass)) {
            return;
        }
        Method[] methods = realClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getParameters().length > 0 || method.getAnnotation(NotAPI.class) != null) {
                continue;
            }
            registerApiMapping(beanName, bean, method, realClass);
        }
    }

    /**
     * 注册API信息
     *
     * @param beanName bean名
     * @param bean     bean
     * @param method   方法
     * @param clazz    真实类
     */
    private static void registerApiMapping(String beanName, Object bean, Method method, Class clazz) {
        MappingHandlerMapping handler = ApiUtil.getMappingHandlerMapping();

        //@Mapping信息
        RequestMappingInfo requestMappingInfo = handler.getMappingForMethod(method, clazz);
        if (requestMappingInfo != null) {
            getMappingHandlerMapping().registerHandlerMethod(bean, method, requestMappingInfo);
            addMappingInfoCache(beanName, bean, method, requestMappingInfo);
            printLog(beanName, method, requestMappingInfo);
        }

        //默认映射信息
        RequestMappingInfo defaultRequestMappingInfo = handler.getDefaultFroMethod(method, clazz);
        if (defaultRequestMappingInfo != null) {
            getMappingHandlerMapping().registerHandlerMethod(bean, method, defaultRequestMappingInfo);
            addMappingInfoCache(beanName, bean, method, defaultRequestMappingInfo);
            printLog(beanName, method, defaultRequestMappingInfo);
        }
    }

    /**
     * API信息缓存
     *
     * @param bean               服务
     * @param method             方法
     * @param beanName           服务名
     * @param requestMappingInfo 映射信息
     */
    private static void addApiInfoCache(Object bean, Method method, String beanName, RequestMappingInfo requestMappingInfo) {
        String key = String.format("%s.%s", bean, method);

        ApiInfo apiInfo;
        if (apiInfoCache.containsKey(key)) {
            apiInfo = apiInfoCache.get(key);
            apiInfo.add(requestMappingInfo);
        } else {
            apiInfo = new ApiInfo(bean, method, beanName, new HashSet<RequestMappingInfo>() {{
                add(requestMappingInfo);
            }});
        }
        apiInfoCache.put(key, apiInfo);
    }

    public static ApiInfo getApiInfoCache(Object bean, Method method) {
        String key = String.format("%s.%s", bean, method);
        return apiInfoCache.get(key);
    }

    public static Collection<ApiInfo> getApiInfoCache() {
        return apiInfoCache.values();
    }

    private static MappingHandlerMapping getMappingHandlerMapping() {
        if (mappingHandlerMapping == null) {
            mappingHandlerMapping = new MappingHandlerMapping();
            mappingHandlerMapping.afterPropertiesSet();
        }
        return mappingHandlerMapping;
    }
}
