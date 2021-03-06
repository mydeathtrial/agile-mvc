package cloud.agileframework.mvc.container;

import cloud.agileframework.mvc.base.AbstractResponseFormat;
import cloud.agileframework.mvc.param.AgileReturn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean supportsReturnType(MethodParameter methodParameter) {
        return AbstractResponseFormat.class.isAssignableFrom(methodParameter.getParameterType());
    }

    @Override
    public void handleReturnValue(Object o, MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest) throws Exception {
        ModelAndView modelAndView = ((AbstractResponseFormat) o).buildResponse(AgileReturn.getHead(), AgileReturn.getBody());
        modelAndViewContainer.setView(modelAndView.getView());
        modelAndViewContainer.setViewName(modelAndView.getViewName());
        modelAndViewContainer.setStatus(modelAndView.getStatus());
        modelAndViewContainer.addAllAttributes(modelAndView.getModel());
        if (logger.isDebugEnabled()) {
            logger.debug("返回参数已完成处理：{}", modelAndView);
        }
    }
}
