package com.agile.common.container;

import cloud.agileframework.common.util.clazz.TypeReference;
import cloud.agileframework.common.util.object.ObjectUtil;
import cloud.agileframework.spring.util.spring.BeanUtil;
import com.agile.common.exception.NoSignInException;
import com.agile.common.properties.SimulationProperties;
import com.agile.common.security.CustomerUserDetails;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @author 佟盟
 * 日期 2020/6/1 14:17
 * 描述 Agile参数解析器
 * @version 1.0
 * @since 1.0
 */
public class CustomHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().isAssignableFrom(CustomerUserDetails.class);
    }

    @Override
    @Nullable
    public Object resolveArgument(@Nullable MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        // 判断模拟配置
        SimulationProperties simulation = BeanUtil.getBean(SimulationProperties.class);
        if (simulation != null && simulation.isEnable()) {
            return ObjectUtil.to(simulation.getUser(),
                    new TypeReference<>(simulation.getUserClass()));
        }
        // 非模拟情况
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new NoSignInException("账号尚未登录，服务中无法获取登录信息");
        } else {
            return authentication.getDetails();
        }
    }
}
