package com.agile.common.util;

import com.agile.common.base.APIInfo;
import com.agile.common.container.MappingHandlerMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.*;
import java.util.*;

/**
 * Created by 佟盟 on 2018/8/23
 */
public class APIUtil {
    private static Map<String, Object> serviceCache = new HashMap<>();
    private static List<APIInfo> apiInfoCache = new ArrayList<>();
    private static MappingHandlerMapping mappingHandlerMapping;

    public static HandlerMethod getApiCache(HttpServletRequest request) {
        if(mappingHandlerMapping == null)return null;
        try {
            HandlerExecutionChain handlerExecutionChain = getMappingHandlerMapping().getHandler(request);
            if(handlerExecutionChain!=null){
                return (HandlerMethod) (handlerExecutionChain.getHandler());
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static void addMappingInfoCache(String beanName,Object bean,Method method,Class clazz) {
        RequestMappingInfo requestMappingInfo = APIUtil.getMappingHandlerMapping().getMappingForMethod(method, clazz);
        if(requestMappingInfo!=null){
            getMappingHandlerMapping().registerHandlerMethod(bean,method,requestMappingInfo);
        }
        addApiInfoCache(new APIInfo(bean,method,beanName,requestMappingInfo));
    }

    public static void addApiInfoCache(APIInfo apiInfo){
        apiInfoCache.add(apiInfo);
    }

    public static List<APIInfo> getApiInfoCache(){
        return apiInfoCache;
    }
    public static MappingHandlerMapping getMappingHandlerMapping() {
        if(mappingHandlerMapping == null){
            mappingHandlerMapping = new MappingHandlerMapping();
            mappingHandlerMapping.afterPropertiesSet();
        }
        return mappingHandlerMapping;
    }

    public static Object getServiceCache(String key){
        return serviceCache.get(key);
    }
    public static void addServiceCache(String key, Object o){
        serviceCache.put(key, o);
    }
}
