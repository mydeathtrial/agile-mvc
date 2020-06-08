package com.agile.common.container;

import com.agile.common.param.AgileParam;
import com.agile.common.util.ParamUtil;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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
        return methodParameter.getParameterType().isAssignableFrom(AgileParam.class);
    }

    @Override
    @Nullable
    public Object resolveArgument(@Nullable MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        Map<String, Object> params = ParamUtil.handleInParam(nativeWebRequest.getNativeRequest(HttpServletRequest.class));
        return new AgileParam(params);
    }
}
