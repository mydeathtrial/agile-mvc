package com.agile.common.base;

import com.agile.common.task.ApiBase;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author 佟盟 on 2018/11/22
 */
@Setter
@Getter
public class ApiInfo extends ApiBase {

    private Set<RequestMappingInfo> requestMappingInfos;

    public ApiInfo(Object bean, Method method, String beanName, Set<RequestMappingInfo> requestMappingInfos) {
        super(bean, method, beanName);
        this.requestMappingInfos = requestMappingInfos;
    }

    public void add(RequestMappingInfo requestMappingInfo) {
        this.requestMappingInfos.add(requestMappingInfo);
    }
}
