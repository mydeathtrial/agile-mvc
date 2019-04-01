package com.agile.common.util;

import com.agile.common.base.ResponseFile;
import com.agile.common.base.poi.ExcelFile;
import org.apache.commons.collections.MapUtils;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 佟盟 on 2017/12/22
 */
public class MapUtil extends MapUtils {
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
     * 将List中的Key转换为小写
     *
     * @param list 返回新对象
     * @return
     */
    public static List<Map<String, Object>> convertKeyList2LowerCase(List<Map<String, Object>> list) {
        if (null == list) {
            return null;
        }
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        Iterator<Map<String, Object>> iterator = list.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> result = convertKey2LowerCase(iterator.next());
            if (null != result) {
                resultList.add(result);
            }
        }
        return resultList;
    }

    /**
     * 转换单个map,将key转换为小写.
     *
     * @param map 返回新对象
     * @return
     */
    public static Map<String, Object> convertKey2LowerCase(Map<String, Object> map) {
        if (null == map) {
            return null;
        }
        Map<String, Object> result = new HashMap<>(map.size());
        Iterator iterator = getKeyIterator(map);
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            Object value = map.get(key);
            if (null == key) {
                continue;
            }
            String keyL = key.toLowerCase();
            result.put(keyL, value);
        }
        return result;
    }

    public static <Key, Value> Iterator getKeyIterator(Map<Key, Value> map) {
        Set<Key> keys = map.keySet();
        return keys.iterator();
    }

    public static <Key, Value> Iterator geValueIterator(Map<Key, Value> map) {
        Collection<Value> values = map.values();
        return values.iterator();
    }

    /**
     * 将List中Map的Key转换为小写.
     *
     * @param list 返回新对象
     * @return
     */
    public static List<Map<String, Object>> trimListKeyValue(List<Map<String, Object>> list) {
        if (null == list) {
            return null;
        }
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        Iterator<Map<String, Object>> iteratorL = list.iterator();
        while (iteratorL.hasNext()) {
            Map<String, Object> map = iteratorL.next();
            Map<String, Object> result = trimKeyValue(map);
            if (null != result) {
                resultList.add(result);
            }
        }
        return resultList;
    }

    /**
     * 转换单个map,将key转换为小写.
     *
     * @param map 返回新对象
     * @return
     */
    public static Map<String, Object> trimKeyValue(Map<String, Object> map) {
        if (null == map) {
            return null;
        }
        Map<String, Object> result = new HashMap<>(map.size());
        //
        Set<String> keys = map.keySet();
        //
        Iterator<String> iteratorK = keys.iterator();
        while (iteratorK.hasNext()) {
            String key = iteratorK.next();
            Object value = map.get(key);
            if (null == key) {
                continue;
            }
            //
            String keyT = key.trim();
            if (value instanceof String) {
                String valueT = String.valueOf(value).trim();
                result.put(keyT, valueT);
            } else {
                result.put(keyT, value);
            }
        }
        return result;
    }

    /**
     * Map中取String类型值
     *
     * @param map Map集
     * @param key 索引值
     * @return
     */
    public static String getString(Map<String, Object> map, String key) {
        if (map.get(key) != null) {
            return map.get(key).toString();
        }
        return null;
    }

    /**
     * 按照key值排序Map
     *
     * @param map
     * @return
     */
    public static Map<String, Object> sort(Map<String, Object> map) {
        return sortByValue(map, KeyOrValue.KEY);
    }

    /**
     * 按照value值排序Map
     *
     * @param map
     * @return
     */
    public static Map<String, Object> sortByValue(Map<String, Object> map) {
        return sortByValue(map, KeyOrValue.VALUE);
    }

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
     * Map排序
     *
     * @param map        目标map
     * @param keyOrValue 排序key值还是排序value值
     * @return 排序好的Map
     */
    private static Map<String, Object> sortByValue(Map<String, Object> map, KeyOrValue keyOrValue) {
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
        final int length = 16;
        Map<String, Object> target = new HashMap<>(length);
        target.putAll(source);
        for (Map.Entry<String, Object> entity : target.entrySet()) {
            Object value = entity.getValue();
            if (value == null) {
                continue;
            }
            if (value instanceof MultipartFile) {
                target.put(entity.getKey(), ((MultipartFile) value).getOriginalFilename());
            } else if (value instanceof ResponseFile) {
                target.put(entity.getKey(), ((ResponseFile) value).getFileName());
            } else if (value instanceof ExcelFile) {
                target.put(entity.getKey(), ((ExcelFile) value).getFileName());
            } else if (List.class.isAssignableFrom(value.getClass())) {
                List list = (List) value;
                if (list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        Object o = list.get(i);
                        if (o instanceof MultipartFile) {
                            list.set(i, ((MultipartFile) o).getOriginalFilename());
                        } else if (o instanceof ResponseFile) {
                            list.set(i, ((ResponseFile) o).getFileName());
                        } else if (o instanceof ExcelFile) {
                            list.set(i, ((ExcelFile) o).getFileName());
                        }
                    }
                }
            }
        }
        return source;
    }
}
