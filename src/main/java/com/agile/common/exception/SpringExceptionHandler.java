package com.agile.common.exception;

import com.agile.common.base.AbstractResponseFormat;
import com.agile.common.base.Constant;
import com.agile.common.base.Head;
import com.agile.common.base.RETURN;
import com.agile.common.util.FactoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
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
@Order(0)
@ControllerAdvice
public class SpringExceptionHandler implements HandlerExceptionResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringExceptionHandler.class);

    private static final String EXCEPTION_MESSAGE_PREFIX = "agile.exception.%s";
    private static final String ERROR_MESSAGE_PREFIX = "agile.error.%s";
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

        RETURN r = get(e, ERROR_MESSAGE_PREFIX);
        if (r == null) {
            r = get(e, EXCEPTION_MESSAGE_PREFIX);
        }
        if (r == null) {
            r = RETURN.EXPRESSION;
        }

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

    /**
     * 提取国际化响应文
     *
     * @param e      异常
     * @param prefix 国际化key前缀
     * @return RETURN
     */
    private static RETURN get(Throwable e, String prefix) {
        RETURN r;
        if (e instanceof AbstractCustomException) {
            r = RETURN.getReturn(String.format(prefix, e.getClass().getSimpleName()), ((AbstractCustomException) e).getParams());
        } else {
            r = RETURN.getReturn(String.format(prefix, e.getClass().getSimpleName()), e.getMessage());
        }
        return r;
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        return createModelAndView(ex);
    }
}
