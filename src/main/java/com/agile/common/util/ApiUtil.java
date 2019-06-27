package com.agile.common.util;

import com.agile.common.annotation.NotAPI;
import com.agile.common.base.ApiInfo;
import com.agile.common.container.AgileHandlerMapping;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.mvc.service.ServiceInterface;
import com.google.common.collect.Maps;
import org.springframework.data.util.ProxyUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author 佟盟 on 2018/8/23
 */
public class ApiUtil {
    private static Map<String, ApiInfo> apiInfoCache = new HashMap<>();
    private static AgileHandlerMapping mappingHandlerMapping;

    public static ApiInfo getApiCache(HttpServletRequest request) {
        if (mappingHandlerMapping == null) {
            return null;
        }
        try {
            HandlerExecutionChain handlerExecutionChain = getMappingHandlerMapping().getHandler(request);
            if (handlerExecutionChain != null) {
                HandlerMethod handler = (HandlerMethod) (handlerExecutionChain.getHandler());
                return getApiInfoCache(handler.getMethod());
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

    public static void printLog() {
        getApiInfoCache().parallelStream().forEach(apiInfo -> apiInfo.getRequestMappingInfos().parallelStream().forEach(requestMappingInfo -> {
            PatternsRequestCondition patternsRequestCondition = requestMappingInfo.getPatternsCondition();
            Optional.ofNullable(patternsRequestCondition)
                    .ifPresent(condition ->
                            condition.getPatterns().parallelStream()
                                    .forEach(path ->
                                            LoggerFactory.COMMON_LOG.debug(String.format("[Mapping:%s][Service:%s][method:%s]", path, apiInfo.getBeanName(), apiInfo.getMethod().getName()))
                                    )
                    );
        }));
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
        AgileHandlerMapping handler = ApiUtil.getMappingHandlerMapping();

        //@Mapping信息
        RequestMappingInfo requestMappingInfo = handler.getMappingForMethod(method, clazz);
        if (requestMappingInfo != null) {
            getMappingHandlerMapping().registerHandlerMethod(bean, method, requestMappingInfo);
            addMappingInfoCache(beanName, bean, method, requestMappingInfo);
        }

        //默认映射信息
        RequestMappingInfo defaultRequestMappingInfo = handler.getDefaultFroMethod(method, clazz);
        if (defaultRequestMappingInfo != null) {
            getMappingHandlerMapping().registerHandlerMethod(bean, method, defaultRequestMappingInfo);
            addMappingInfoCache(beanName, bean, method, defaultRequestMappingInfo);
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
        String key = method.toGenericString();

        ApiInfo apiInfo;
        if (apiInfoCache.containsKey(key)) {
            apiInfo = apiInfoCache.get(key);
            apiInfo.add(requestMappingInfo);
        } else {
            apiInfo = new ApiInfo(bean, method, beanName, requestMappingInfo);
        }
        apiInfoCache.put(key, apiInfo);
    }

    public static ApiInfo getApiInfoCache(Method method) {
        return apiInfoCache.get(method.toGenericString());
    }

    public static Collection<ApiInfo> getApiInfoCache() {
        return apiInfoCache.values();
    }

    public static HashMap<String, ApiInfo> getApiInfos() {
        return Maps.newHashMap(apiInfoCache);
    }

    public static AgileHandlerMapping getMappingHandlerMapping() {
        if (mappingHandlerMapping == null) {
            mappingHandlerMapping = new AgileHandlerMapping();
            mappingHandlerMapping.afterPropertiesSet();
        }
        return mappingHandlerMapping;
    }
}
