package com.agile.common.base;

import cloud.agileframework.spring.util.spring.MessageUtil;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;

/**
 * @author 佟盟 on 2017/1/9
 */
public final class RETURN {

    public static final RETURN SUCCESS = byMessage("agile.success.success");
    public static final RETURN FAIL = byMessage("agile.error.fail");

    public static final RETURN LOGOUT_SUCCESS = byMessage("agile.success.logoutSuccess");
    public static final RETURN EXPRESSION = byMessage("agile.exception.expression");
    public static final RETURN PARAMETER_ERROR = byMessage("agile.error.paramError");


    /**
     * 响应状态码
     */
    private final String code;

    /**
     * 响应信息
     */
    private final String msg;

    /**
     * 状态码
     */
    private final HttpStatus status;

    private RETURN(String code, String msg, HttpStatus status) {
        this.code = code.trim();
        this.msg = msg.trim();
        this.status = status == null ? HttpStatus.OK : status;
    }

    public static RETURN of(String code, String msg) {
        return new RETURN(code, msg, null);
    }

    public static RETURN of(String code, String msg, HttpStatus status) {
        return new RETURN(code, msg, status);
    }

    /**
     * 根据国际化文件当中的key值与占位参数，获取国际化文，返回RETURN
     *
     * @param key    国际化文档中的key值
     * @param params 占位参数
     * @return 返回RETURN结果
     */
    public static RETURN byMessage(HttpStatus status, String key, Object... params) {

        String message = MessageUtil.message(key, params);

        String code;

        if (message != null && message.contains(Constant.RegularAbout.COLON)) {
            int splitIndex = message.indexOf(Constant.RegularAbout.COLON);
            code = message.substring(0, splitIndex);
            message = message.substring(splitIndex + 1);
        } else if (status != null) {
            code = status.value() + "";
            message = status.getReasonPhrase();
        } else {
            throw new NoSuchMessageException(key);
        }

        return of(code, message, status);
    }

    public static RETURN byMessage(String key, Object... params) {
        return byMessage(null, key, params);
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
