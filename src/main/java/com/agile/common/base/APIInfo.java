package com.agile.common.base;

import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;

/**
 * Created by 佟盟 on 2018/11/22
 */
public class APIInfo {
    private Object bean;
    private Method method;
    private String beanName;
    private RequestMappingInfo requestMappingInfo;

    public APIInfo(Object bean, Method method, String beanName) {
        this.bean = bean;
        this.method = method;
        this.beanName = beanName;
    }

    public APIInfo(Object bean, Method method, String beanName, RequestMappingInfo requestMappingInfo) {
        this.bean = bean;
        this.method = method;
        this.beanName = beanName;
        this.requestMappingInfo = requestMappingInfo;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public RequestMappingInfo getRequestMappingInfo() {
        return requestMappingInfo;
    }

    public void setRequestMappingInfo(RequestMappingInfo requestMappingInfo) {
        this.requestMappingInfo = requestMappingInfo;
    }
}
