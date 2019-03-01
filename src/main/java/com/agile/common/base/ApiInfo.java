package com.agile.common.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author 佟盟 on 2018/11/22
 */
@AllArgsConstructor
@Setter
@Getter
public class ApiInfo {
    private Object bean;
    private Method method;
    private String beanName;
    private Set<RequestMappingInfo> requestMappingInfos;

    public void add(RequestMappingInfo requestMappingInfo) {
        this.requestMappingInfos.add(requestMappingInfo);
    }
}
