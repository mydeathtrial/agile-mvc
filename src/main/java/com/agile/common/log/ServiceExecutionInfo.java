package com.agile.common.log;

import com.agile.common.base.Constant;
import com.agile.common.util.AopUtil;
import com.agile.common.util.JSONUtil;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

import static io.swagger.models.properties.PropertyBuilder.PropertyId.MAX_LENGTH;

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
    private static final int LOG_TAB = 10;
    private static final String JSON_ERROR = "特殊参数无法进行json化处理";
    private static final int MAX_LENGTH = 5000;

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
    private Throwable e;

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

    public Map<String, Object> getInParam() {
        inParam.remove(Constant.ResponseAbout.BODY);
        return inParam;
    }

    public String getInParamToJson() {
        try {
            return JSONUtil.toStringPretty(getInParam(), LOG_TAB);
        } catch (Exception e) {
            return JSON_ERROR;
        }
    }

    public String getOutParamToJson() {
        try {
            String outStr = JSONUtil.toStringPretty(getOutParam(), LOG_TAB);
            return (outStr != null && outStr.length() > MAX_LENGTH) ? outStr.substring(0, MAX_LENGTH) + "...}" : outStr;
        } catch (Exception e) {
            return JSON_ERROR;
        }
    }
}
