package cloud.agileframework.mvc.base;

import cloud.agileframework.common.constant.Constant;
import cloud.agileframework.spring.util.MessageUtil;
import cloud.agileframework.spring.util.ServletUtil;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;

/**
 * @author 佟盟 on 2017/1/9
 */
public class RETURN {

    public static final RETURN SUCCESS = byMessageWithDefault("000000:服务执行成功", "agile.success.success");
    public static final RETURN LOGOUT_SUCCESS = byMessageWithDefault("000001:退出成功", "agile.success.logout");

    public static final RETURN FAIL = byMessageWithDefault("100000:操作失败", "agile.error.fail");
    public static final RETURN NOT_FOUND = byMessage("100001:请求服务不存在", HttpStatus.NOT_FOUND, "agile.error.not-found");
    public static final RETURN PARAMETER_ERROR = byMessageWithDefault("100002:参数错误", "agile.error.paramError");

    public static final RETURN EXPRESSION = byMessageWithDefault("200001:程序异常", "agile.exception.expression");


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

    public RETURN(String code, String msg, HttpStatus status) {
        this.code = code.trim();
        this.msg = msg.trim();
        this.status = status == null ? HttpStatus.OK : status;
        HttpServletResponse response = ServletUtil.getCurrentResponse();
        if (response != null) {
            response.setStatus(this.status.value());
        }
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
    public static RETURN byMessage(String defaultValue, HttpStatus status, String key, Object... params) {
        String message = MessageUtil.message(key, params);
        if (defaultValue == null && message == null) {
            throw new RuntimeException(String.format("defaultValue and message cannot both be null,message %s not found", key));
        }
        if (message != null) {
            try {
                return byMessage(status, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return byMessage(status, defaultValue);
    }

    /**
     * 根据国际化文件当中的key值与占位参数，获取国际化文，返回RETURN
     *
     * @param status  响应状态
     * @param message 相应信息
     * @return 返回RETURN结果
     */
    public static RETURN byMessage(HttpStatus status, String message) {
        String code;

        if (message != null && message.contains(Constant.RegularAbout.COLON)) {
            int splitIndex = message.indexOf(Constant.RegularAbout.COLON);
            code = message.substring(0, splitIndex);
            message = message.substring(splitIndex + 1);
        } else if (status != null) {
            code = status.value() + "";
            message = status.getReasonPhrase();
        } else {
            throw new RuntimeException("status and message cannot both be null");
        }

        return of(code, message, status);
    }

    public static RETURN byMessageWithDefault(String defaultValue, String key) {
        return byMessage(defaultValue, null, key);
    }

    public static RETURN byMessage(String key, Object... params) {
        return byMessage(null, null, key, params);
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
