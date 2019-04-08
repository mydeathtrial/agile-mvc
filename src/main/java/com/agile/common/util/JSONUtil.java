package com.agile.common.util;


import com.agile.common.base.Constant;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.JSONUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author mydeathtrial on 2017/5/9
 */
public class JSONUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(Constant.RegularAbout.BLANK);
            }
        });

        // 对象字段全部列入
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);

        // 取消默认转换timestamps形式
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // 忽略空bean转json的错误
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        // 统一日期格式yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(DateUtil.YYMMDDHHMMSS_SLASH));

        // 忽略在json字符串中存在,但是在java对象中不存在对应属性的情况
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
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
            JsonConfig jsonConfig = new JsonConfig();
            jsonConfig.setJsonPropertyFilter((obj, key, value) -> value == null || value instanceof JSONNull);
            json = JSONObject.fromObject(object, jsonConfig);
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
        T o = (T) JSONObject.toBean(json, clazz, PropertiesUtil.getClassMap(clazz));
        return o;
    }

    public static <T> T toBean(Class<T> clazz, String str) {
        try {
            return objectMapper.readValue(str, clazz);
        } catch (IOException e) {
            return null;
        }
    }

    public static <T> T toBean(Class<T> clazz, JsonNode json) {
        try {
            return objectMapper.readValue(json.toString(), clazz);
        } catch (IOException e) {
            return null;
        }
    }

    public static JsonNode toJsonNode(String jsonStr) {
        try {
            return objectMapper.readTree(jsonStr);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * jackson2转List Map结构
     *
     * @param json json数据
     * @return List Map结构
     */
    public static Object jsonNodeCover(JsonNode json) {
        if (json == null) {
            return null;
        }
        if (ObjectNode.class.isAssignableFrom(json.getClass())) {
            return jsonNodeCoverMap((ObjectNode) json);
        } else if (ArrayNode.class.isAssignableFrom(json.getClass())) {
            return jsonNodeCoverArray((ArrayNode) json);
        } else {
            return json.asText();
        }
    }

    /**
     * jackson2转List Map结构
     *
     * @param json ObjectNode数据
     * @return List Map结构
     */
    public static Map<String, Object> jsonNodeCoverMap(ObjectNode json) {
        if (json == null) {
            return null;
        }
        Map<String, Object> result = new HashMap<>(json.size());
        Iterator it = json.fieldNames();
        while (it.hasNext()) {
            String selfKey = it.next().toString();
            JsonNode o = json.get(selfKey);
            if (ObjectNode.class.isAssignableFrom(o.getClass())) {
                result.put(selfKey, jsonNodeCoverMap((ObjectNode) o));
            } else if (ArrayNode.class.isAssignableFrom(o.getClass())) {
                result.put(selfKey, jsonNodeCoverArray((ArrayNode) o));
            } else {
                result.put(selfKey, o.asText());
            }
        }
        return result;
    }

    /**
     * jackson2转List Map结构
     *
     * @param json ArrayNode数据
     * @return List Map结构
     */
    public static List<Object> jsonNodeCoverArray(ArrayNode json) {
        if (json == null) {
            return null;
        }
        List<Object> result = new ArrayList<>();
        for (JsonNode node : json) {
            if (ObjectNode.class.isAssignableFrom(node.getClass())) {
                result.add(jsonNodeCoverMap((ObjectNode) node));
            } else if (ArrayNode.class.isAssignableFrom(node.getClass())) {
                result.add(jsonNodeCoverArray((ArrayNode) node));
            } else {
                result.add(node.asText());
            }
        }
        return result;
    }

    /**
     * json-lib转List Map结构
     *
     * @param json json数据
     * @return List Map结构
     */
    public static Object jsonCover(JSON json) {
        if (json == null) {
            return null;
        }
        if (JSONObject.class.isAssignableFrom(json.getClass())) {
            return jsonObjectCoverMap((JSONObject) json);
        } else if (JSONArray.class.isAssignableFrom(json.getClass())) {
            return jsonArrayCoverArray((JSONArray) json);
        } else {
            return json.toString();
        }
    }

    /**
     * json-lib转List Map结构
     *
     * @param json JSONObject数据
     * @return List Map结构
     */
    public static Map<String, Object> jsonObjectCoverMap(JSONObject json) {

        if (json == null) {
            return null;
        }
        Map<String, Object> result = new HashMap<>(json.size());

        Iterator it = json.keys();
        while (it.hasNext()) {
            String selfKey = it.next().toString();
            Object o = json.get(selfKey);
            if (JSONObject.class.isAssignableFrom(o.getClass())) {
                result.put(selfKey, jsonObjectCoverMap((JSONObject) o));
            } else if (JSONArray.class.isAssignableFrom(o.getClass())) {
                result.put(selfKey, jsonArrayCoverArray((JSONArray) o));
            } else {
                result.put(selfKey, o);
            }
        }
        return result;
    }

    /**
     * json-lib转List Map结构
     *
     * @param jsonArray JSONArray数据
     * @return List Map结构
     */
    public static List<Object> jsonArrayCoverArray(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        List<Object> result = new ArrayList<>();
        for (Object o : jsonArray) {
            if (JSON.class.isAssignableFrom(o.getClass())) {
                if (JSONObject.class.isAssignableFrom(o.getClass())) {
                    result.add(jsonObjectCoverMap((JSONObject) o));
                } else if (JSONArray.class.isAssignableFrom(o.getClass())) {
                    result.add(jsonArrayCoverArray((JSONArray) o));
                }
            } else {
                result.add(o);
            }
        }
        return result;
    }

//    public static void main(String[] args) {
//        String s = "{\"name\":\"BeJson\",\"url\":\"http://www.bejson.com\",\"page\":88,\"isNonProfit\":true,\"address\":" +
//                "{\"street\":\"科技园路.\",\"city\":\"江苏苏州\",\"country\":\"中国\"},\"links\":[{\"name\":\"Google\",\"url\"" +
//                ":\"http://www.google.com\"},{\"name\":\"Baidu\",\"url\":\"http://www.baidu.com\"},{\"name\":\"SoSo\",\"url\":\"http://www.SoSo.com\"}]}";
//        String s2 = "[{\"name\":\"Google\",\"url\":\"http://www.google.com\",\"a\":[{\"name1\":\"1\",\"url1\":\"2\"}," +
//                "{\"name1\":\"1\",\"url1\":\"2\"}]},{\"name\":\"Baidu\",\"url\":\"http://www.baidu.com\",\"a\":[{\"name1\":" +
//                "\"1\",\"url1\":\"2\"},{\"name1\":\"1\",\"url1\":\"2\"}]},{\"name\":\"SoSo\",\"url\":\"http://www.SoSo.com\"" +
//                ",\"a\":[{\"name1\":\"1\",\"url1\":\"2\"},{\"name1\":\"1\",\"url1\":\"2\"}]}]";
//        JsonNode jsonNode = toJsonNode(s2);
//
//        HashMap<Object, Object> map = new HashMap<>();
//        map.put("body",jsonNodeCover(jsonNode));
//        MapUtil.pathGet("body.all.a.all.url1",map);
//    }
}
