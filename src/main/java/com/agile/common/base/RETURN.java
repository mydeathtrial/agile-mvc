package com.agile.common.base;

import com.agile.common.util.PropertiesUtil;

/**
 * Created by 佟盟 on 2017/1/9
 */
public final class RETURN {
    //响应状态码
    private String code;

    //响应信息
    private String msg;

    //请求类响应状态
    public final static RETURN SUCCESS = new RETURN("000001","服务执行成功！");
    public final static RETURN NO_SERVICE = new RETURN("000002","请求服务不存在！");
    public final static RETURN NO_METHOD = new RETURN("000003","请求方法不存在！");
    public final static RETURN NO_COMPLETE = new RETURN("000004","请求路径不合法！");
    public final static RETURN UPLOAD_SUCCESS = new RETURN("000005","文件上传成功！");
    public final static RETURN UPLOAD_ERROR = new RETURN("000006","文件上传过程中发生错误，上传失败！");
    public final static RETURN MADE_DIR_FAIL = new RETURN("000007","文件夹创建失败！");
    public final static RETURN EMPTY_FILE = new RETURN("000008","空文件，上传失败！");
    public final static RETURN EMPTY_FILENAME = new RETURN("000009","空文件名，上传失败！");
    public final static RETURN ERROR_FORMAT = new RETURN("000010","被禁用的文件格式！");

    //登陆类响应状态
    public final static RETURN NO_SIGN_IN = new RETURN("100000", getSecurityErrorMsg("no_login","账号尚未登陆！"));
    public final static RETURN ILLEGAL_ACCOUNT = new RETURN("100002",getSecurityErrorMsg("illegal_account","无效用户名或密码，请重新登陆！"));
    public final static RETURN DISABLE_ACCOUNT = new RETURN("100003",getSecurityErrorMsg("disable_account","账号已被禁用！"));
    public final static RETURN ACCESS_DENIED = new RETURN("100004",getSecurityErrorMsg("access_denied","权限不足，无法访问！"));
    public final static RETURN EXPIRED_ACCOUNT = new RETURN("100005",getSecurityErrorMsg("expired_account","账号已过期，无法登陆！"));
    public final static RETURN TOKEN_ILLEGAL = new RETURN("100006",getSecurityErrorMsg("token_illegal","会话已失效，请重新登陆！"));
    public final static RETURN NO_COMPLETE_FORM = new RETURN("100007",getSecurityErrorMsg("no_complete_form","账号或密码不能为空，请重新登陆！"));
    public final static RETURN LOCKED_ACCOUNT = new RETURN("100008",getSecurityErrorMsg("locked_account","账号已被冻结，无法登陆！"));
    public final static RETURN CREDENTIALS_EXPIRED_ACCOUNT = new RETURN("100009",getSecurityErrorMsg("credentials_expired_account","凭证已过期！无法登陆"));
    public final static RETURN REPEAT_LOGIN = new RETURN("100010",getSecurityErrorMsg("repeat_login","账号无法重复登陆！"));
    public final static RETURN VERIFICATION_CODE = new RETURN("100011",getSecurityErrorMsg("verification_code_error","验证码验证失败！"));
    public final static RETURN AUTHENTICATION = new RETURN("100012",getSecurityErrorMsg("authentication","权限不足！"));
    public final static RETURN LOGOUT_SUCCESS = new RETURN("100013",getSecurityErrorMsg("logout_success","退出成功！"));
    public final static RETURN VERIFICATION_CODE_EXPIRE = new RETURN("100014",getSecurityErrorMsg("verification_code_expire","验证码超时！"));
    public final static RETURN VERIFICATION_CODE_NON = new RETURN("100015",getSecurityErrorMsg("verification_code_non","请输入验证码！"));

    //运行错误响应状态
    public final static RETURN EXPRESSION = new RETURN("200000","程序错误！");
    public final static RETURN SQL_EXPRESSION = new RETURN("200001","SQL错误！");
    public final static RETURN HIBERNATE_EXPRESSION = new RETURN("200002","HIBERNATE错误！");
    public final static RETURN DATETIME_EXPRESSION = new RETURN("200003","时间错误！");
    public final static RETURN NULL_POINTER_EXPRESSION = new RETURN("200004","空指针错误！");
    public final static RETURN PARSE_EXPRESSION = new RETURN("200005","转换错误！");
    public final static RETURN IO_EXPRESSION = new RETURN("200006","IO流错误！");
    public final static RETURN TIMEOUT_EXPRESSION = new RETURN("200007","IO流错误！");
    public final static RETURN PARAMETER_EXPRESSION = new RETURN("200008","参数错误！");
    public final static RETURN IIIEGAL_ACCESS_EXPRESSION = new RETURN("200009","非法访问错误！");
    public final static RETURN IIIEGAL_ARGUMENT_EXPRESSION = new RETURN("200010","非法参数错误！");
    public final static RETURN INVOCATION_TARGET_EXPRESSION = new RETURN("200011","调用目标错误！");
    public final static RETURN SECURITY_EXPRESSION = new RETURN("200013","安全错误！");
    public final static RETURN CLASS_CAST_EXPRESSION = new RETURN("200014","对象强制转换错误！");
    public final static RETURN BEAN_EXPRESSION = new RETURN("200015","BEAN错误！");
    public final static RETURN NUSUCH_BEAN_EXPRESSION = new RETURN("200016","找不到指定实体！");
    public final static RETURN MAX_UPLOAD_SIZE_EXPRESSION = new RETURN("200017","上传文件大小超出最大值！");
    public final static RETURN FILE_NOT_FOUND_EXPRESSION = new RETURN("200018","系统找不到指定路径！");
    public final static RETURN NO_SUCH_METHPD_EXPRESSION = new RETURN("200019","调用方法不存在！");
    public final static RETURN NOT_FOUND_CACHEPROXY_EXPRESSION = new RETURN("200020","未找到缓存代理类！");
    public final static RETURN PERSISTENT_OBJECT_EXPRESSION = new RETURN("200021","主键重复");

    //业务逻辑响应状态
    public final static RETURN PARAMETER_ERROR = new RETURN("300000","请求参数中，未包含必要参数！");

    public RETURN(String code, String msg) {
        this.code = code;
        this.msg = msg;
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

    private static String getSecurityErrorMsg(String key,String defaultMsg){
        return PropertiesUtil.getProperty(String.format("agile.security.%s",key),defaultMsg);
    }
}
