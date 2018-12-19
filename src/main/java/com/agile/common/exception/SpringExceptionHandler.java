package com.agile.common.exception;

import com.agile.common.base.AbstractResponseFormat;
import com.agile.common.base.Constant;
import com.agile.common.base.Head;
import com.agile.common.base.RETURN;
import com.agile.common.util.FactoryUtil;
import com.agile.common.util.StringUtil;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by 佟盟 on 2018/6/25
 */
@Order(0)
@ControllerAdvice
public class SpringExceptionHandler {

    private static final String MESSAGE_PREFIX = "agile.exception.%s";
    private static final String ERROR_MESSAGE_TEMPLATE = "[类:%s][方法:%s][行:%s]";
    private static final String ERROR_DETAIL_MESSAGE_TEMPLATE = "[信息:%s]";

    @ExceptionHandler(Throwable.class)
    public ModelAndView allExceptionHandler(Throwable e) {
        return createModelAndView(e);
    }


    public ModelAndView createModelAndView(Throwable e) {
        ModelAndView modelAndView;

        RETURN r;
        if (e instanceof AbstractCustomException) {
            r = RETURN.getMessage(String.format(MESSAGE_PREFIX, e.getClass().getSimpleName()), ((AbstractCustomException) e).getParams());
        } else {
            if (e.getCause() == null) {
                r = RETURN.getMessage(String.format(MESSAGE_PREFIX, e.getClass().getSimpleName()));

            } else {
                r = RETURN.getMessage(String.format(MESSAGE_PREFIX, e.getCause().getClass().getSimpleName()));
            }
        }
        if (r == null) {
            r = RETURN.EXPRESSION;
        }

        assert r != null;
        Head head = new Head(r);
        String msgStr = printLog(e);

        AbstractResponseFormat abstractResponseFormat = FactoryUtil.getBean(AbstractResponseFormat.class);
        if (abstractResponseFormat != null) {
            modelAndView = abstractResponseFormat.buildResponse(head, msgStr);
        } else {
            modelAndView = new ModelAndView();
            modelAndView.addObject(Constant.ResponseAbout.HEAD, head);
            modelAndView.addObject(Constant.ResponseAbout.RESULT, msgStr);
        }

        return modelAndView;
    }

    private String printLog(Throwable e) {
        StackTraceElement msg = e.getStackTrace()[0];
        String exclass = msg.getClassName();
        String method = msg.getMethodName();
        int lineNumber = msg.getLineNumber();

        String msgStr = String.format(ERROR_MESSAGE_TEMPLATE, exclass, method, lineNumber);

        if (!StringUtil.isEmpty(e.getMessage())) {
            msgStr += String.format(ERROR_DETAIL_MESSAGE_TEMPLATE, e.getMessage());
        }
        return msgStr;
    }

}
