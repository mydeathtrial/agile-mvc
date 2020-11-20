package cloud.agileframework.mvc.exception;

import cloud.agileframework.common.constant.Constant;
import cloud.agileframework.common.util.pattern.PatternUtil;
import cloud.agileframework.mvc.base.AbstractResponseFormat;
import cloud.agileframework.mvc.base.Head;
import cloud.agileframework.mvc.base.RETURN;
import cloud.agileframework.mvc.util.ViewUtil;
import cloud.agileframework.spring.util.BeanUtil;
import cloud.agileframework.spring.util.MessageUtil;
import cloud.agileframework.spring.util.ServletUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
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
    private static final Logger logger = LoggerFactory.getLogger(SpringExceptionHandler.class);
    private static final String MESSAGE_HEAD = "统一异常捕捉";

    @ExceptionHandler(Throwable.class)
    public ModelAndView allExceptionHandler(Throwable e) {
        return createModelAndView(e);
    }

    public static ModelAndView createModelAndView(Throwable e) {
        ModelAndView modelAndView;

        if (e instanceof AgileArgumentException) {
            Object attributeErrors = ServletUtil.getCurrentRequest().getAttribute(Constant.RequestAttributeAbout.ATTRIBUTE_ERROR);
            if (attributeErrors != null) {
                return ViewUtil.getResponseFormatData(new Head(RETURN.PARAMETER_ERROR), attributeErrors);
            }
        }

        if (e.getCause() != null) {
            e = e.getCause();
        }

        RETURN r = to(e);
        if (r.getStatus() == HttpStatus.INTERNAL_SERVER_ERROR) {
            e.printStackTrace();
            logger.error(MESSAGE_HEAD, e);
        }

        Head head = new Head(r);

        AbstractResponseFormat abstractResponseFormat = BeanUtil.getBean(AbstractResponseFormat.class);
        if (abstractResponseFormat != null) {
            modelAndView = abstractResponseFormat.buildResponse(head, null);
        } else {
            modelAndView = new ModelAndView();
            modelAndView.addObject(Constant.ResponseAbout.HEAD, head);
            modelAndView.addObject(Constant.ResponseAbout.RESULT, null);
        }
        modelAndView.setStatus(head.getStatus());

        return modelAndView;
    }

    /**
     * 提取国际化响应文
     *
     * @param e 异常
     * @return RETURN
     */
    private static RETURN to(Throwable e) {
        String message;
        if (e instanceof AbstractCustomException) {
            message = MessageUtil.message(e.getClass().getName(), ((AbstractCustomException) e).getParams());
        } else if (e instanceof NoSuchRequestServiceException || e instanceof NoSuchRequestMethodException) {
            return RETURN.NOT_FOUND;
        } else {
            message = MessageUtil.message(e.getClass().getName(), e.getMessage());
        }
        if (StringUtils.isEmpty(message)) {
            return RETURN.of(RETURN.FAIL.getCode(), RETURN.FAIL.getMsg(), HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            final String codePrefix = "^[\\d]{6}";
            if (PatternUtil.find(codePrefix, message) && !message.startsWith(Constant.NumberAbout.TWO + "")) {
                return RETURN.byMessage(HttpStatus.OK, message);
            }
            return RETURN.byMessage(HttpStatus.INTERNAL_SERVER_ERROR, message);
        }
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        return createModelAndView(ex);
    }
}
