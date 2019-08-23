package com.agile.common.util;

import com.agile.common.base.ResponseFile;
import com.agile.common.base.poi.ExcelFile;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author 佟盟 on 2017/12/22
 */
public class MapUtil extends MapUtils {

    /**
     * 辅助枚举
     */
    private enum KeyOrValue {
        /**
         * key
         */
        KEY,
        VALUE
    }

    /**
     * 对象属性转Map结构
     *
     * @param map    转换后装填的map集
     * @param object 需要被转换的对象
     */
    public static void coverMap(Map<String, Object> map, Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                Field field = fields[i];
                field.setAccessible(true);
                Object value = field.get(object);
                map.put(field.getName(), value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 索引转驼峰
     *
     * @param map 需要转换的Map
     * @return
     */
    public static Map<String, Object> coverKey2Camel(Map<String, Object> map) {
        Map<String, Object> result = new HashMap<>(map.size());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            result.put(StringUtil.toLowerName(entry.getKey()), entry.getValue());
        }
        return result;
    }

    /**
     * 判断是否是Map类型
     *
     * @param o 需要判断的对象
     * @return 是否
     */
    public static boolean isMap(Object o) {
        return o instanceof Map;
    }

    /**
     * Map中取String类型值
     *
     * @param map Map集
     * @param key 索引值
     * @return
     */
    public static <T> T getValue(Map<String, Object> map, String key, Class<T> clazz) {
        if (map.get(key) != null) {
            return ObjectUtil.cast(clazz, map.get(key));
        }
        return null;
    }

    /**
     * 按照key值排序Map
     *
     * @param map
     * @return
     */
    public static Map<String, Object> sortByKey(Map<String, Object> map) {
        return sort(map, KeyOrValue.KEY);
    }

    /**
     * 按照value值排序Map
     *
     * @param map
     * @return
     */
    public static Map<String, Object> sortByValue(Map<String, Object> map) {
        return sort(map, KeyOrValue.VALUE);
    }

    /**
     * Map排序
     *
     * @param map        目标map
     * @param keyOrValue 排序key值还是排序value值
     * @return 排序好的Map
     */
    private static Map<String, Object> sort(Map<String, Object> map, KeyOrValue keyOrValue) {
        List<Map.Entry<String, Object>> list = new LinkedList<>(map.entrySet());
        switch (keyOrValue) {
            case KEY:
                list.sort(Comparator.comparing(Map.Entry::getKey));
                break;
            case VALUE:
                list.sort(Comparator.comparing(o -> String.valueOf(o.getValue())));
                break;
            default:
        }
        Map<String, Object> linkedHashMap = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : list) {
            linkedHashMap.put(entry.getKey(), entry.getValue());
        }
        return linkedHashMap;
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

}
