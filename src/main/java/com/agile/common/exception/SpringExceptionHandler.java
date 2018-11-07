package com.agile.common.exception;

import com.agile.common.base.AbstractResponseFormat;
import com.agile.common.base.Constant;
import com.agile.common.base.Head;
import com.agile.common.base.RETURN;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.util.FactoryUtil;
import com.agile.common.util.StringUtil;
import org.apache.commons.logging.Log;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by 佟盟 on 2018/6/25
 */
@ControllerAdvice
public class SpringExceptionHandler {
    /**
     * 日志工具
     */
    private Log logger = LoggerFactory.createLogger(Constant.FileAbout.SERVICE_LOGGER_FILE, this.getClass());

    private static String MESSAGE_PRIFIX = "agile.exception.%s";
    @ExceptionHandler(Throwable.class)
    public ModelAndView allExceptionHandler(Throwable e){
        return createModelAndView(e);
    }


    public ModelAndView createModelAndView(Throwable e){
        ModelAndView modelAndView;

        RETURN r;
        if(e instanceof AbstractCustomException){
            r = RETURN.getMessage(String.format(MESSAGE_PRIFIX, e.getClass().getSimpleName()),((AbstractCustomException) e).getParams());
        }else{
            if(e.getCause() == null){
                r = RETURN.getMessage(String.format(MESSAGE_PRIFIX, e.getClass().getSimpleName()));

            }else{
                r = RETURN.getMessage(String.format(MESSAGE_PRIFIX, e.getCause().getClass().getSimpleName()));
            }
        }
        if(r == null){
            r = RETURN.EXPRESSION;
        }

        assert r != null;
        Head head = new Head(r);
        String msgStr = printLog(e);

        AbstractResponseFormat abstractResponseFormat = FactoryUtil.getBean(AbstractResponseFormat.class);
        if(abstractResponseFormat!=null){
            modelAndView = FactoryUtil.getBean(AbstractResponseFormat.class).buildResponse(head,msgStr);
        }else{
            modelAndView = new ModelAndView();
            modelAndView.addObject(Constant.ResponseAbout.HEAD,head);
            modelAndView.addObject(Constant.ResponseAbout.RESULT,msgStr);
        }

        return modelAndView;
    }

    private String printLog(Throwable e){
        StackTraceElement msg = e.getStackTrace()[0];
        String exclass = msg.getClassName();
        String method = msg.getMethodName();
        int lineNumber = msg.getLineNumber();

        String msgStr = String.format("【异常定位:[类:%s]调用[方法:%s]时在第%s行代码处发生错误!】",exclass,method,lineNumber);

        if(!StringUtil.isEmpty(e.getMessage())){
            msgStr+=(String.format(" | 【详情:%s】", e.getMessage()));
        }
        logger.error(msgStr);
        return msgStr;
    }

}
