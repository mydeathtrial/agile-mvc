package com.agile.common.exception;

import cloud.agileframework.spring.util.spring.BeanUtil;
import com.agile.common.base.AbstractResponseFormat;
import com.agile.common.base.Constant;
import com.agile.common.base.Head;
import com.agile.common.base.RETURN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 佟盟 on 2018/6/25
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class SpringExceptionHandler implements HandlerExceptionResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringExceptionHandler.class);
    private static final String MESSAGE_HEAD = "统一异常捕捉";

    @ExceptionHandler(Throwable.class)
    public ModelAndView allExceptionHandler(Throwable e) {
        return createModelAndView(e);
    }

    public static ModelAndView createModelAndView(Throwable e) {
        ModelAndView modelAndView;

        if (e.getCause() != null) {
            e = e.getCause();
        }

        if (e instanceof RuntimeException && !(e instanceof AuthenticationException)) {
            LOGGER.error(MESSAGE_HEAD, e);
        }

        RETURN r = to(e);

        Head head = new Head(r);

        AbstractResponseFormat abstractResponseFormat = BeanUtil.getBean(AbstractResponseFormat.class);
        if (abstractResponseFormat != null) {
            modelAndView = abstractResponseFormat.buildResponse(head, null);
        } else {
            modelAndView = new ModelAndView();
            modelAndView.addObject(Constant.ResponseAbout.HEAD, head);
            modelAndView.addObject(Constant.ResponseAbout.RESULT, null);
        }

        return modelAndView;
    }

    /**
     * 提取国际化响应文
     *
     * @param e 异常
     * @return RETURN
     */
    private static RETURN to(Throwable e) {
        RETURN r;
        if (e instanceof AbstractCustomException) {
            r = RETURN.byMessage(HttpStatus.INTERNAL_SERVER_ERROR, e.getClass().getName(), ((AbstractCustomException) e).getParams());
        } else if (e instanceof RuntimeException) {
            r = RETURN.byMessage(HttpStatus.INTERNAL_SERVER_ERROR, e.getClass().getName(), e.getMessage());
        } else {
            r = RETURN.byMessage(HttpStatus.OK, e.getClass().getName(), e.getMessage());
        }
        return r;
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        return createModelAndView(ex);
    }
}
