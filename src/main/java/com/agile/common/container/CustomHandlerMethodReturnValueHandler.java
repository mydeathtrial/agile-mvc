package com.agile.common.container;

import com.agile.common.param.AgileReturn;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author 佟盟
 * 日期 2020/6/1 16:05
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class CustomHandlerMethodReturnValueHandler implements HandlerMethodReturnValueHandler {
    @Override
    public boolean supportsReturnType(MethodParameter methodParameter) {
        return AgileReturn.class.isAssignableFrom(methodParameter.getParameterType());
    }

    @Override
    public void handleReturnValue(Object o, MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest) throws Exception {
        AgileReturn agileReturn = (AgileReturn) o;
        ModelAndView modelAndView = agileReturn.build();

        modelAndViewContainer.setView(modelAndView.getView());
        modelAndViewContainer.setViewName(modelAndView.getViewName());
        modelAndViewContainer.setStatus(modelAndView.getStatus());
        modelAndViewContainer.addAllAttributes(modelAndView.getModel());
    }
}
