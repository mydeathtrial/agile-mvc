package cloud.agileframework.mvc.exception;

/**
 * @author 佟盟 on 2018/11/6
 */
public abstract class AbstractCustomException extends Exception {
    private final String[] params;

    protected AbstractCustomException(String... params) {
        this.params = params;
    }

    public String[] getParams() {
        return params.clone();
    }
}
