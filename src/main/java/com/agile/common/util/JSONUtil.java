package com.agile.common.util;


import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.text.SimpleDateFormat;

/**
 * Created by mydeathtrial on 2017/5/9
 */
public class JSONUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 对象字段全部列入
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);

        // 取消默认转换timestamps形式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);

        // 忽略空bean转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);

        // 统一日期格式yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(DateUtil.YYMMDDHHMMSS_SLASH));

        // 忽略在json字符串中存在,但是在java对象中不存在对应属性的情况
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Object转json字符串并格式化美化
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String toStringPretty(T obj, int blank) {
        if (obj == null) {
            return null;
        }
        try {
            String r = "\r\n";
            StringBuilder s = new StringBuilder();
            int i = 0;
            while (i < blank) {
                s = s.append(" ");
                i++;
            }
            String result = obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj).replaceAll(r, r + s.toString());
            if (blank > 0) {
                result = s + result;
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> String toStringPretty(T obj) {
        return toStringPretty(obj, 0);
    }

    /**
     * 转换json字符串
     *
     * @param object 转换源
     * @return json串
     */
    public static String toJSONString(Object object) {
        if (StringUtil.isString(object)) {
            return object.toString();
        }
        if (JSONUtils.isArray(object)) {
            return JSONArray.fromObject(object).toString();
        } else {
            try {
                return JSONObject.fromObject(object).toString();
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * 转换json对象
     *
     * @param object 转换源
     * @return json对象
     */
    public static JSON toJSON(Object object) {
        JSON json = null;
        try {
            json = JSONObject.fromObject(object);
        } catch (Exception ignored) {
        }
        try {
            json = JSONArray.fromObject(object);
        } catch (Exception ignored) {
        }
        return json;
    }

    /**
     * JSONObject转java对象
     */
    public static <T> T toBean(Class<T> clazz, JSONObject json) {
        T o = toBeanByNetSf(clazz, json);
        if (o == null) {
            o = toBeanByAli(clazz, json);
        }
        return o;
    }

    public static <T> T toBeanByNetSf(Class<T> clazz, JSONObject json) {
        try {
            return (T) JSONObject.toBean(json, clazz, PropertiesUtil.getClassMap(clazz));
        } catch (Exception ignored) {
        }
        return null;
    }

    public static <T> T toBeanByAli(Class<T> clazz, JSONObject json) {
        try {
            return com.alibaba.fastjson.JSONObject.toJavaObject(com.alibaba.fastjson.JSONObject.parseObject(json.toString()), clazz);
        } catch (Exception ignored) {
        }
        return null;
    }
}
