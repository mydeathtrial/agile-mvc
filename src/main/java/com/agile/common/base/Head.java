package com.agile.common.base;

import com.agile.common.util.ServletUtil;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * @author 佟盟 on 2017/1/9
 */
public class Head implements Serializable {
    private static final long serialVersionUID = 97555324631150979L;
    private final String ip;
    private final String code;
    private final String msg;
    private final HttpStatus status;

    public Head(RETURN returnState) {
        this.code = returnState.getCode();
        this.msg = returnState.getMsg();
        this.ip = ServletUtil.getLocalIP();
        this.status = returnState.getStatus();
    }

    public String getIp() {
        return ip;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}