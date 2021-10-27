package cloud.agileframework.mvc.exception;

/**
 * @author 佟盟 on 2017/9/23
 */
public class NoSuchRequestServiceException extends Exception {
    public NoSuchRequestServiceException() {
        this("100003:请求服务不存在");
    }

    private NoSuchRequestServiceException(String message) {
        super(message);
    }
}
