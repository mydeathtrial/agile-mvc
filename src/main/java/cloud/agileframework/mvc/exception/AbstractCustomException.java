package cloud.agileframework.mvc.exception;

/**
 * @author 佟盟 on 2018/11/6
 */
public class AbstractCustomException extends Exception {
    private final Object[] params;

    public AbstractCustomException(Object... params) {
        this.params = params;
    }

    public Object[] getParams() {
        return params.clone();
    }
}
