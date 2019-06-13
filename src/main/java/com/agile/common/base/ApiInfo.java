package com.agile.common.base;

import com.agile.common.task.ApiBase;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 佟盟 on 2018/11/22
 */
@Setter
@Getter
public class ApiInfo extends ApiBase {

    private Set<RequestMappingInfo> requestMappingInfos = new HashSet<>();

    public ApiInfo(Object bean, Method method) {
        super(bean, method, null);
    }

    public ApiInfo(Object bean, Method method, String beanName, RequestMappingInfo requestMappingInfo) {
        super(bean, method, beanName);
        this.requestMappingInfos.add(requestMappingInfo);
    }

    public void add(RequestMappingInfo requestMappingInfo) {
        this.requestMappingInfos.add(requestMappingInfo);
    }
}
