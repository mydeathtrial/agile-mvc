package cloud.agileframework.mvc.exception;

import cloud.agileframework.common.constant.Constant;
import cloud.agileframework.validate.ValidateMsg;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 佟盟
 * 日期 2020/8/00031 17:47
 * 描述 参数验证失败
 * @version 1.0
 * @since 1.0
 */
public class AgileArgumentException extends Exception {
    public AgileArgumentException() {
        super("100013:请求参数有误");
    }

    public AgileArgumentException(String message) {
        super("100013:" + message);
    }

    public AgileArgumentException(List<ValidateMsg> validateMsg) {
        this();
        transmit(validateMsg.toArray(new ValidateMsg[]{}));
    }

    public AgileArgumentException(String message, List<ValidateMsg> validateMsg) {
        this(message);
        transmit(validateMsg.toArray(new ValidateMsg[]{}));
    }

    public AgileArgumentException(ValidateMsg... validateMsg) {
        this();
        transmit(validateMsg);
    }

    public AgileArgumentException(String message, ValidateMsg... validateMsg) {
        this(message);
        transmit(validateMsg);
    }

    private static void transmit(ValidateMsg... validateMsg) {
        if (validateMsg.length == 0) {
            return;
        }
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            request.setAttribute(Constant.RequestAttributeAbout.ATTRIBUTE_ERROR, validateMsg);
        }
    }
}
