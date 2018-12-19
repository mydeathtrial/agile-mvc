package com.agile.common.base;

import com.agile.common.util.PropertiesUtil;

/**
 * Created by 佟盟 on 2017/1/9
 */
public final class RETURN {

    public static final RETURN SUCCESS = getMessage("agile.response.success");
    public static final RETURN LOGOUT_SUCCESS = getMessage("agile.response.logout_success");
    public static final RETURN EXPRESSION = getMessage("agile.response.expression");
    public static final RETURN UNKNOWN_STATE = getMessage("agile.response.unknown_state");
    public static final RETURN PARAMETER_ERROR = getMessage("agile.response.param_error");
    public static final RETURN XML_SERIALIZER_ERROR = getMessage("agile.response.xml_serializer_error");

    //响应状态码
    private String code;

    //响应信息
    private String msg;

    public RETURN(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 根据国际化文件当中的key值与占位参数，获取国际化文，返回RETURN
     *
     * @param key    国际化文档中的key值
     * @param params 占位参数
     * @return 返回RETURN结果
     */
    public static RETURN getMessage(String key, Object... params) {
        try {
            String message = PropertiesUtil.getMessage(key, params);
            if (message != null && message.contains(Constant.RegularAbout.COLON)) {
                int splitIndex = message.indexOf(Constant.RegularAbout.COLON);
                return new RETURN(message.substring(0, splitIndex), message.substring(splitIndex + 1));
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public RETURN setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public RETURN setMsg(String msg) {
        this.msg = msg;
        return this;
    }
}
