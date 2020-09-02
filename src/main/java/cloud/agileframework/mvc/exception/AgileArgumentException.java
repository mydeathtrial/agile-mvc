package cloud.agileframework.mvc.exception;

/**
 * @author 佟盟
 * 日期 2020/8/00031 17:47
 * 描述 参数验证失败
 * @version 1.0
 * @since 1.0
 */
public class AgileArgumentException extends Exception {
    public AgileArgumentException() {
        super("参数验证失败");
    }
}
