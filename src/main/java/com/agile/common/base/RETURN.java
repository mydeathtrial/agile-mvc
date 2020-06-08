package com.agile.common.base;

import com.agile.common.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;

/**
 * @author 佟盟 on 2017/1/9
 */
public final class RETURN {

    public static final RETURN SUCCESS = getMessage("agile.success.success");
    public static final RETURN LOGOUT_SUCCESS = getMessage("agile.success.logoutSuccess");
    public static final RETURN EXPRESSION = getMessage("agile.exception.expression");
    public static final RETURN PARAMETER_ERROR = getMessage("agile.error.paramError");
    public static final RETURN XML_SERIALIZER_ERROR = getMessage("agile.error.xmlSerializerError");
    public static final RETURN FAIL = getMessage("agile.error.Fail");

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
    private HttpStatus status;

    private RETURN(String code, String msg) {
        this.code = code.trim();
        this.msg = msg.trim();
        parseState();
    }

    public static RETURN of(String code, String msg) {
        return new RETURN(code, msg);
    }

    public static RETURN getReturn(String key, Object... params) {
        String message = PropertiesUtil.getMessage(key, params);
        if (message != null && message.contains(Constant.RegularAbout.COLON)) {
            int splitIndex = message.indexOf(Constant.RegularAbout.COLON);
            return new RETURN(message.substring(0, splitIndex), message.substring(splitIndex + 1));
        }
        return null;
    }

    /**
     * 根据国际化文件当中的key值与占位参数，获取国际化文，返回RETURN
     *
     * @param key    国际化文档中的key值
     * @param params 占位参数
     * @return 返回RETURN结果
     */
    public static RETURN getMessage(String key, Object... params) {
        RETURN r = getReturn(key, params);
        if (r == null) {
            throw new NoSuchMessageException(key);
        }
        return r;
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

    void parseState() {
        if (StringUtils.isEmpty(code)) {
            return;
        }
        String firstCode = code.substring(Constant.NumberAbout.ZERO, Constant.NumberAbout.ONE);
        if ("2".equals(firstCode)) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            status = HttpStatus.OK;
        }
        if ("100002".equals(code) || "100003".equals(code)) {
            status = HttpStatus.NOT_FOUND;
        }
    }
}
