package cloud.agileframework.mvc.base;

import cloud.agileframework.spring.util.ServletUtil;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * @author 佟盟 on 2017/1/9
 */

public class Head extends RETURN implements Serializable {
    private static final long serialVersionUID = 97555324631150979L;
    private final String ip = ServletUtil.getLocalIP();;

    public Head(String code, String msg, HttpStatus status) {
        super(code, msg, status);
    }

    public Head(RETURN r) {
        super(r.getCode(),r.getMsg(),r.getStatus());
    }

    public Head(){
        this(RETURN.SUCCESS);
    }

    public String getIp() {
        return ip;
    }
}