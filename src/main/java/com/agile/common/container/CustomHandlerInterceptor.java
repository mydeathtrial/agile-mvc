package com.agile.common.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 佟盟
 * 日期 2020/6/1 21:48
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class CustomHandlerInterceptor implements HandlerInterceptor {
    private Logger log = LoggerFactory.getLogger(CustomHandlerInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        List<ValidateMsg> validateMessages = ParamUtil.handleInParamValidate(getService(), getMethod());
//        Optional<List<ValidateMsg>> optionalValidateMsgList = ParamUtil.aggregation(validateMessages);
//        if (optionalValidateMsgList.isPresent()) {
//            return ParamUtil.getResponseFormatData(new Head(RETURN.PARAMETER_ERROR), optionalValidateMsgList.get());
//        }
//        throw new NoSuchRequestServiceException();
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
