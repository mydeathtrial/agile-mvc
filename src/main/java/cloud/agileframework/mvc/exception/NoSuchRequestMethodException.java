package cloud.agileframework.mvc.exception;

/**
 * @author 佟盟 on 2017/9/24
 */
public class NoSuchRequestMethodException extends Exception {
    public NoSuchRequestMethodException() {
        this("100003:请求服务不存在");
    }

    private NoSuchRequestMethodException(String message) {
        super(message);
    }
}
