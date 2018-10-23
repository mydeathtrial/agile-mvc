package com.agile.common.exception;

import com.agile.common.base.Constant;
import com.agile.common.base.Head;
import com.agile.common.base.RETURN;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.util.StringUtil;
import org.apache.commons.logging.Log;
import org.hibernate.PersistentObjectException;
import org.springframework.beans.BeansException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.PersistenceException;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.DateTimeException;
import java.util.concurrent.TimeoutException;

/**
 * Created by 佟盟 on 2018/6/25
 */
@ControllerAdvice
public class SpringExceptionHandler {
    /**
     * 日志工具
     */
    private Log logger = LoggerFactory.createLogger(Constant.FileAbout.SERVICE_LOGGER_FILE, this.getClass());

    @ExceptionHandler(Throwable.class)
    public ModelAndView allExceptionHandler(Throwable e){
        ModelAndView modelAndView = createModelAndView(e);

        String msgStr = printLog(e);

        modelAndView.addObject(Constant.ResponseAbout.RESULT,msgStr);
        return modelAndView;
    }


    public ModelAndView createModelAndView(Throwable e){
        ModelAndView modelAndView = new ModelAndView();

        RETURN r = RETURN.EXPRESSION;
        if(e instanceof SQLException){
            r = RETURN.SQL_EXPRESSION;
        }else if(e instanceof DateTimeException){
            r = RETURN.DATETIME_EXPRESSION;
        }else if(e instanceof NullPointerException){
            r = RETURN.NULL_POINTER_EXPRESSION;
        }else if(e instanceof ParseException){
            r = RETURN.PARSE_EXPRESSION;
        }else if(e instanceof TimeoutException){
            r = RETURN.TIMEOUT_EXPRESSION;
        }else if(e instanceof IllegalAccessException){
            r = RETURN.IIIEGAL_ACCESS_EXPRESSION;
        }else if(e instanceof IllegalArgumentException){
            r = RETURN.IIIEGAL_ARGUMENT_EXPRESSION;
        }else if(e instanceof InvocationTargetException){
            r = RETURN.INVOCATION_TARGET_EXPRESSION;
        }else if(e instanceof NoSuchMethodException){
            r = RETURN.NO_SUCH_METHPD_EXPRESSION;
        }else if(e instanceof SecurityException){
            r = RETURN.SECURITY_EXPRESSION;
        }else if(e instanceof ClassCastException){
            r = RETURN.CLASS_CAST_EXPRESSION;
        }else if(e instanceof BeansException){
            r = RETURN.BEAN_EXPRESSION;
        }else if (e instanceof MaxUploadSizeExceededException){
            r = RETURN.MAX_UPLOAD_SIZE_EXPRESSION;
        }else if (e instanceof FileNotFoundException){
            r = RETURN.FILE_NOT_FOUND_EXPRESSION;
        }else if (e instanceof NoSuchRequestServiceException || e instanceof HttpRequestMethodNotSupportedException){
            r = RETURN.NO_SERVICE;
        }else if (e instanceof NoSuchRequestMethodException){
            r = RETURN.NO_METHOD;
        }else if (e instanceof UnlawfulRequestException){
            r = RETURN.NO_COMPLETE;
        }else if (e instanceof NotFoundCacheProxyException){
            r = RETURN.NOT_FOUND_CACHEPROXY_EXPRESSION;
        }else if (e instanceof NoSignInException){
            r = RETURN.NO_SIGN_IN;
        }else if (e instanceof AccountExpiredException){
            r = RETURN.EXPIRED_ACCOUNT;
        }else if (e instanceof TokenIllegalException){
            r = RETURN.TOKEN_ILLEGAL;
        }else if (e instanceof NoCompleteFormSign){
            r = RETURN.NO_COMPLETE_FORM;
        }else if (e instanceof UsernameNotFoundException || e instanceof BadCredentialsException){
            r = RETURN.ILLEGAL_ACCOUNT;
        }else if (e instanceof DisabledException){
            r = RETURN.DISABLE_ACCOUNT;
        }else if (e instanceof LockedException){
            r = RETURN.LOCKED_ACCOUNT;
        }else if (e instanceof CredentialsExpiredException){
            r = RETURN.CREDENTIALS_EXPIRED_ACCOUNT;
        }else if (e instanceof RepeatAccount){
            r = RETURN.REPEAT_LOGIN;
        }else if (e instanceof VerificationCodeException){
            r = RETURN.VERIFICATION_CODE;
        }else if (e instanceof VerificationCodeExpire){
            r = RETURN.VERIFICATION_CODE_EXPIRE;
        }else if (e instanceof VerificationCodeNon){
            r = RETURN.VERIFICATION_CODE_NON;
        }else if (e instanceof AuthenticationException){
            r = RETURN.AUTHENTICATION;
        }else if (e instanceof PersistenceException){
            if(e.getCause() instanceof PersistentObjectException){
                r = RETURN.PERSISTENT_OBJECT_EXPRESSION;
            }
        }

        modelAndView.addObject(Constant.ResponseAbout.HEAD,new Head(r));

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
