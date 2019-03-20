package com.agile.common.util;

import com.agile.common.base.Constant;
import com.agile.mvc.entity.DictionaryDataEntity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 佟盟
 * @version 1.0
 * 日期 2019/3/6 15:48
 * 描述 字典工具
 * @since 1.0
 */
public class DictionaryUtil {
    private static final String NAME_FORMAT = "%s%s";
    private static final String CODE_FORMAT = "%s.%s";

    /**
     * 集合类型转换字典码工具，转换为List/Map类型
     *
     * @param list           要进行转换的集合
     * @param dictionaryCode 要使用的字典码
     * @param column         转换字段集
     * @param <T>            泛型
     * @return 返回List/Map类型，增加_text字段
     * @throws NoSuchFieldException   没有这个字段
     * @throws IllegalAccessException 非法访问
     */
    public static <T> List<Map<String, Object>> coverMapDictionary(List<T> list, String dictionaryCode, String suffix, String column) throws NoSuchFieldException, IllegalAccessException {
        return coverMapDictionary(list, new String[]{dictionaryCode}, suffix, new String[]{column});
    }

    public static <T> List<Map<String, Object>> coverMapDictionary(List<T> list, String[] dictionaryCodes, String suffix, String[] columns) throws NoSuchFieldException, IllegalAccessException {
        if (dictionaryCodes == null || columns == null || dictionaryCodes.length != columns.length) {
            throw new IllegalArgumentException();
        }
        List<Map<String, Object>> result = new ArrayList<>(list.size());
        for (T o : list) {
            result.add(coverMapDictionary(o, dictionaryCodes, suffix, columns));
        }
        return result;
    }

    public static <T> Map<String, Object> coverMapDictionary(T o, String[] dictionaryCodes, String suffix, String[] columns) throws NoSuchFieldException, IllegalAccessException {
        Map<String, Field> cache;
        Class clazz = o.getClass();
        if (Map.class.isAssignableFrom(clazz)) {
            Map map = ((Map) o);
            for (int i = 0; i < columns.length; i++) {
                String column = columns[i];
                map.put(String.format(NAME_FORMAT, column, suffix), coverDicName(String.format(CODE_FORMAT, dictionaryCodes[i], map.get(column))));
            }
            return map;
        } else {
            cache = initField(clazz, columns);
            Map<String, Object> map = new HashMap<>(clazz.getDeclaredFields().length + columns.length);
            MapUtil.coverMap(map, o);
            for (int i = 0; i < columns.length; i++) {
                String column = columns[i];
                Field field = cache.get(column);
                Object value = field.get(o);
                map.put(String.format(NAME_FORMAT, column, suffix), coverDicName(String.format(CODE_FORMAT, dictionaryCodes[i], value)));
            }
            return map;
        }

    }

    public static <T> List<T> coverBeanDictionary(List<T> list, String dictionaryCode, String column) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        return coverBeanDictionary(list, new String[]{dictionaryCode}, new String[]{column});
    }

    public static <T> T coverBeanDictionary(T o, String[] dictionaryCodes, String[] columns) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        Map<String, Field> cache;
        Class clazz = o.getClass();
        if (Map.class.isAssignableFrom(clazz)) {
            Map map = ((Map) o);
            for (int i = 0; i < columns.length; i++) {
                String column = columns[i];
                map.put(column, coverDicName(String.format(CODE_FORMAT, dictionaryCodes[i], map.get(column))));
            }
            return (T) map;
        } else {
            cache = initField(clazz, columns);

            T n = (T) clazz.newInstance();
            ObjectUtil.copyProperties(o, n);
            for (int i = 0; i < columns.length; i++) {
                String column = columns[i];
                Field field = cache.get(column);
                Object value = field.get(o);
                field.set(n, coverDicName(String.format(CODE_FORMAT, dictionaryCodes[i], value)));
            }
            return n;
        }
    }

    /**
     * 集合类型转换字典码工具，转换为List/T类型
     *
     * @param list            要进行转换的集合
     * @param dictionaryCodes 要使用的字典码
     * @param columns         转换字段集
     * @param <T>             泛型
     * @return 返回List/Map类型，字典码字段自动被转换为字典值
     */
    public static <T> List<T> coverBeanDictionary(List<T> list, String[] dictionaryCodes, String[] columns) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        List<T> result = new ArrayList<>(list.size());
        for (T o : list) {
            result.add(coverBeanDictionary(o, dictionaryCodes, columns));
        }
        return result;
    }

    /**
     * 根据父级树形字典码与name获取字典
     *
     * @param code 字典
     * @return bean
     */
    public static DictionaryDataEntity coverDicBean(String code, String name) {
        List<DictionaryDataEntity> list = coverDicList(code);
        if (list == null) {
            return null;
        }
        for (DictionaryDataEntity entity : list) {
            if (name.equals(entity.getName())) {
                return entity;
            }
        }
        return null;
    }

    /**
     * 根据父级树形字典码与name获取code
     *
     * @param code 字典码
     * @return bean
     */
    public static String coverDicCode(String code, String name) {
        DictionaryDataEntity dic = coverDicBean(code, name);
        if (dic == null) {
            return null;
        }
        return dic.getCode();
    }

    /**
     * 获取缓存根字典对象
     *
     * @param code 字典码
     * @return bean
     */
    public static DictionaryDataEntity coverRootDicBean(String code) {
        return CacheUtil.getDicCache().get(code, DictionaryDataEntity.class);
    }

    /**
     * 转换字典对象
     *
     * @param code 字典码
     * @return bean
     */
    public static DictionaryDataEntity coverDicBean(String code) {
        DictionaryDataEntity rootEntity;
        DictionaryDataEntity targetEntity;
        String parentCode = code;

        if (code.contains(Constant.RegularAbout.SPOT)) {
            String[] codes = code.split("[.]");
            parentCode = codes[Constant.NumberAbout.ZERO].trim();
            rootEntity = coverRootDicBean(parentCode);
            targetEntity = rootEntity.getCodeCache(code.replaceFirst(parentCode + Constant.RegularAbout.SPOT, Constant.RegularAbout.BLANK));
        } else {
            targetEntity = coverRootDicBean(parentCode);
        }
        if (targetEntity == null) {
            return null;
        }
        return targetEntity;
    }

    /**
     * 编码转字典名
     *
     * @param code 字典码
     * @return 字典名
     */
    public static String coverDicName(String code) {
        DictionaryDataEntity targetEntity = coverDicBean(code);
        if (targetEntity == null) {
            return null;
        }
        return targetEntity.getName();
    }

    /**
     * 编码转字典名
     *
     * @param code 字典码
     * @return 字典名
     */
    public static String coverDicName(String code, String defaultName) {
        String name = coverDicName(code);
        return name == null ? defaultName : name;
    }

    /**
     * 编码转子字典列
     *
     * @param code 字典码
     * @return 字典
     */
    public static Map<String, DictionaryDataEntity> coverDicMap(String code) {
        DictionaryDataEntity targetEntity = coverDicBean(code);
        if (targetEntity == null) {
            return null;
        }
        return targetEntity.getCodeCache();
    }

    /**
     * 编码转子字典列
     *
     * @param code 字典码
     * @return 字典
     */
    public static List<DictionaryDataEntity> coverDicList(String code) {
        DictionaryDataEntity targetEntity = coverDicBean(code);
        if (targetEntity == null) {
            return null;
        }
        return targetEntity.getChildren();
    }

    /**
     * 初始化转换字段集合
     *
     * @param clazz   类型
     * @param columns 字段名集合
     */
    private static Map<String, Field> initField(Class clazz, String... columns) throws NoSuchFieldException {
        Map<String, Field> cache = new HashMap<>(columns.length);
        for (String column : columns) {
            if (!cache.containsKey(column)) {
                Field field = clazz.getDeclaredField(column);
                field.setAccessible(true);
                cache.put(column, field);
            }
        }
        return cache;
    }
}
