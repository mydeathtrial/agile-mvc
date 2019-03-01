package com.agile.common.exception;

import com.agile.common.base.AbstractResponseFormat;
import com.agile.common.base.Constant;
import com.agile.common.base.Head;
import com.agile.common.base.RETURN;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.util.FactoryUtil;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author 佟盟 on 2018/6/25
 */
@Order(0)
@ControllerAdvice
public class SpringExceptionHandler {

    private static final String MESSAGE_PREFIX = "agile.exception.%s";

    @ExceptionHandler(Throwable.class)
    public ModelAndView allExceptionHandler(Throwable e) {
        return createModelAndView(e);
    }


    public ModelAndView createModelAndView(Throwable e) {
        LoggerFactory.COMMON_LOG.error(e);
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

        AbstractResponseFormat abstractResponseFormat = FactoryUtil.getBean(AbstractResponseFormat.class);
        if (abstractResponseFormat != null) {
            modelAndView = abstractResponseFormat.buildResponse(head, null);
        } else {
            modelAndView = new ModelAndView();
            modelAndView.addObject(Constant.ResponseAbout.HEAD, head);
            modelAndView.addObject(Constant.ResponseAbout.RESULT, null);
        }

        return modelAndView;
    }

}
