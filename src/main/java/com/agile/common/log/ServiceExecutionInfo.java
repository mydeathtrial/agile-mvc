package com.agile.common.log;

import com.agile.common.base.Constant;
import com.agile.common.base.ResponseFile;
import com.agile.common.base.poi.ExcelFile;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.util.ProxyUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
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
    private static final int LOG_TAB = 10;
    private static final String JSON_ERROR = "特殊参数无法进行json化处理";
    private static final int MAX_LENGTH = 5000;

    private String ip;
    private String url;
    private long startTime;
    private UserDetails userDetails;
    private Date executionDate;
    private Object bean;
    private Method method;
    private Map<String, Object> inParam;
    private Map<String, Object> outParam;
    private Throwable e;

    public long getTimeConsuming() {
        return System.currentTimeMillis() - this.startTime;
    }

    public String getBeanName() {
        return ProxyUtils.getUserClass(bean).getSimpleName();
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
            Map<String, Object> map = coverCanSerializer(getInParam());
            return JSON.toJSONString(map, true);
        } catch (Exception e) {
            return JSON_ERROR;
        }
    }

    public String getOutParamToJson() {
        try {
            Map<String, Object> map = coverCanSerializer(getOutParam());
            String outStr = JSON.toJSONString(map, true);
            return outStr.length() > MAX_LENGTH ? outStr.substring(0, MAX_LENGTH) + "...}" : outStr;
        } catch (Exception e) {
            return JSON_ERROR;
        }
    }

    public static Map<String, Object> coverCanSerializer(Map<String, Object> source) {
        HashMap<String, Object> map = Maps.newHashMapWithExpectedSize(source.size());
        source.entrySet().stream().filter(entry -> !(entry.getValue() instanceof BindingResult)).peek(entry -> {
            Object value = entry.getValue();
            if (value instanceof MultipartFile) {
                map.put(entry.getKey(), ((MultipartFile) value).getOriginalFilename());
            } else if (value instanceof ResponseFile) {
                map.put(entry.getKey(), ((ResponseFile) value).getFileName());
            } else if (value instanceof ExcelFile) {
                map.put(entry.getKey(), ((ExcelFile) value).getFileName());
            }
        }).forEach(entry -> {
            map.put(entry.getKey(), entry.getValue());
        });
        return map;
    }

    public String getUsername() {
        UserDetails user = getUserDetails();
        if (user != null) {
            return user.getUsername();
        }
        return "anonymous";
    }
}
