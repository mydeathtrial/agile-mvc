package com.agile.common.base;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 佟盟 on 2018/11/22
 */
@Setter
@Getter
public class ApiInfo {
    private Object bean;
    private Method method;
    private String beanName;
    private Parameter[] params;

    public ApiInfo(Object bean, Method method, String beanName) {
        this.bean = bean;
        this.method = method;
        this.params = method.getParameters();
        this.beanName = beanName;
    }

    private Set<RequestMappingInfo> requestMappingInfos = new HashSet<>();

    public ApiInfo(Object bean, Method method) {
        this(bean, method, null);
    }

    public ApiInfo(Object bean, Method method, String beanName, RequestMappingInfo requestMappingInfo) {
        this(bean, method, beanName);
        this.requestMappingInfos.add(requestMappingInfo);
    }

    public void add(RequestMappingInfo requestMappingInfo) {
        this.requestMappingInfos.add(requestMappingInfo);
    }
}
