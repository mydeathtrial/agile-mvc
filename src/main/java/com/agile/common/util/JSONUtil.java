package com.agile.common.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author mydeathtrial on 2017/5/9
 */
public class JSONUtil extends JSON {

    private static ObjectMapper objectMapper = new ObjectMapper();

    //    static {
//        objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
//            @Override
//            public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
//                jsonGenerator.writeString(Constant.RegularAbout.BLANK);
//            }
//        });
//
//        // 对象字段全部列入
//        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
//
//        // 取消默认转换timestamps形式
//        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//
//        // 忽略空bean转json的错误
//        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//
//        // 统一日期格式yyyy-MM-dd HH:mm:ss
//        objectMapper.setDateFormat(new SimpleDateFormat(DateUtil.YYMMDDHHMMSS_SLASH));
//
//        // 忽略在json字符串中存在,但是在java对象中不存在对应属性的情况
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
//
//        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
//    }
    private static final String JSON_ERROR = "特殊参数无法进行json化处理";

    /**
     * Object转json字符串并格式化美化
     *
     * @param obj 准备序列化的java对象
     * @param <T> tab空格数
     * @return 序列化后的json
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
                s.append(" ");
                i++;
            }
            String result = obj instanceof String ? (String) obj : toJSONString(obj, true).replaceAll(r, r + s.toString());
            if (blank > 0) {
                result = s + result;
            }
            return result;
        } catch (Exception e) {
            return JSON_ERROR;
        }
    }

    /**
     * JSONObject转java对象
     */
    public static <T> T toBean(Class<T> clazz, JSONObject json) {
        return toJavaObject(json, clazz);
    }

    public static <T> T toBean(Class<T> clazz, String str) {
        return parseObject(str, clazz);
    }

    public static JSON toJSON(Object javaObject) {
        if (javaObject == null) {
            return null;
        }
        return (JSON) parse(javaObject.toString());
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

        Set<String> keySet = json.keySet();
        for (String key : keySet) {
            Object o = json.get(key);
            if (o != null && JSONObject.class.isAssignableFrom(o.getClass())) {
                result.put(key, jsonObjectCoverMap((JSONObject) o));
            } else if (o != null && JSONArray.class.isAssignableFrom(o.getClass())) {
                result.put(key, jsonArrayCoverArray((JSONArray) o));
            } else {
                result.put(key, o);
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
            if (o != null && JSON.class.isAssignableFrom(o.getClass())) {
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

    /**
     * 取出类中包含的集合类型属性信息，用于jsonObject转javaBean使用
     *
     * @param clazz 指定对象类型
     * @return Map 属性名，属性类型
     */
    public static Map<String, Class<?>> getClassMap(Class clazz) {
        final int length = 16;
        Map<String, Class<?>> map = new HashMap<>(length);
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Iterable.class.isAssignableFrom(field.getType())) {
                Type genericType = field.getGenericType();
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                if (ArrayUtil.isEmpty(typeArguments) || typeArguments.length > 1) {
                    continue;
                }
                map.put(field.getName(), (Class) typeArguments[0]);
                if (!ClassUtil.canCastClass((Class) typeArguments[0])) {
                    map.putAll(getClassMap((Class) typeArguments[0]));
                }
            } else if (!ClassUtil.canCastClass(field.getType())) {
                map.putAll(getClassMap(field.getType()));
            }
        }
        return map;
    }

//    /**
//     * asdasd
//     */
//    @Data
//    public static class Demo {
//        private String a;
//        private int b;
//        private double c;
//        private int[] d;
//        private List<SysModulesEntity> list;
//        private Map<String, SysModulesEntity> map;
//    }

//    public static void main(String[] args) {
//        String s = "{\"name\":\"BeJson\",\"url\":\"http://www.bejson.com\",\"page\":88,\"isNonProfit\":true,\"address\":" +
//                "{\"street\":\"科技园路.\",\"city\":\"江苏苏州\",\"country\":\"中国\"},\"links\":[{\"name\":\"Google\",\"url\"" +
//                ":\"http://www.google.com\"},{\"name\":\"Baidu\",\"url\":\"http://www.baidu.com\"},{\"name\":\"SoSo\",\"url\":\"http://www.SoSo.com\"}]}";
//        String s2 = "[{\"name\":\"Google\",\"url\":\"http://www.google.com\",\"a\":[{\"name1\":\"1\",\"url1\":\"2\"}," +
//                "{\"name1\":\"1\",\"url1\":\"2\"}]},{\"name\":\"Baidu\",\"url\":\"http://www.baidu.com\",\"a\":[{\"name1\":" +
//                "\"1\",\"url1\":\"2\"},{\"name1\":\"1\",\"url1\":\"2\"}]},{\"name\":\"SoSo\",\"url\":\"http://www.SoSo.com\"" +
//                ",\"a\":[{\"name1\":\"1\",\"url1\":\"2\"},{\"name1\":\"1\",\"url1\":\"2\"}]}]";
//
//        SysModulesEntity entity = new SysModulesEntity();
//        entity.setDesc(s);
//        entity.setEnable(true);
//        entity.setOrder(100);
//        entity.setChildren(new ArrayList<SysModulesEntity>() {{
//            add(SysModulesEntity.builder().name("tu").build());
//        }});
//
//        Demo demo = new Demo();
//        demo.setA(s);
//        demo.setB(2);
//        demo.setC(3.01);
//        demo.setD(new int[]{1, 2, 3});
//        demo.setList(new ArrayList<SysModulesEntity>() {{
//            add(entity);
//        }});
//        demo.setMap(new HashMap<String, SysModulesEntity>() {{
//            put("tudou", entity);
//        }});
//        Object s3 = com.alibaba.fastjson.JSON.toJSON(demo);
//        HashMap<Object, Object> map = new HashMap<>();
//        map.put("body", jsonCover(toJSON(s2)));
//        ObjectUtil.pathGet("body.name.a.all.url1", map);
//    }

}
