package com.agile.common.log;

import com.agile.common.util.AopUtil;
import com.agile.common.util.JSONUtil;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

/**
 * @author 佟盟
 * 日期 2019/5/7 14:50
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@Builder
@Data
public class ServiceExecutionInfo {
    private BusinessLog businessLog;
    private String ip;
    private String url;
    private long timeConsuming;
    private boolean status;
    private UserDetails userDetails;
    private Date executionDate;
    private Object bean;
    private Method method;
    private Map<String, Object> inParam;
    private Map<String, Object> outParam;

    public void setBean(Object bean) {
        if (bean == null) {
            throw new IllegalArgumentException("bean cannot be set to null");
        }
        this.bean = bean;
    }

    public void setMethod(Method method) {
        if (bean == null) {
            throw new IllegalArgumentException("method cannot be set to null");
        }
        this.method = method;
    }

    public String getBeanName() {
        return AopUtil.getTargetClass(bean).getSimpleName();
    }

    public String getMethodName() {
        return method.getName();
    }

    public String getInParamToJson() {
        return JSONUtil.toJSONString(inParam);
    }

    public String getOutParamToJson() {
        return JSONUtil.toJSONString(outParam);
    }
}
