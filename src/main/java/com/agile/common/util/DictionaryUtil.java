package com.agile.common.util;

import com.agile.common.annotation.Dictionary;
import com.agile.common.base.Constant;
import com.agile.common.cache.AgileCache;
import com.agile.common.util.clazz.ClassUtil;
import com.agile.common.util.object.ObjectUtil;
import com.agile.common.util.pattern.PatternUtil;
import com.agile.common.util.string.StringUtil;
import com.agile.mvc.entity.DictionaryDataEntity;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 佟盟
 * @version 1.0SPLIT_CHAR
 * 日期 2019/3/6 15:48
 * 描述 字典工具
 * @since 1.0
 */
public final class DictionaryUtil {
    private static final String DEFAULT_CACHE_NAME = "dictionary-cache";
    private static final String NAME_FORMAT = "%s%s";
    private static final String CODE_FORMAT = "%s.%s";
    private static final String SPLIT_CHAR = "[./\\\\]";
    private static ThreadLocal<Map<String, DictionaryDataEntity>> threadLocal = ThreadLocal.withInitial(HashMap::new);

    private DictionaryUtil() {
    }

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
        Class<?> clazz = o.getClass();
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

    public static <T> List<T> coverBeanDictionary(List<T> list, String dictionaryCode, String column, String textColumn) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        return coverBeanDictionary(list, new String[]{dictionaryCode}, new String[]{column}, new String[]{textColumn}, null);
    }

    public static <T> List<T> coverBeanDictionary(List<T> list, String dictionaryCode, String column) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        return coverBeanDictionary(list, new String[]{dictionaryCode}, new String[]{column}, new String[]{column}, null);
    }

    public static <T> T coverBeanDictionary(T o, String[] dictionaryCodes, String[] columns) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        return coverBeanDictionary(o, dictionaryCodes, columns, columns, null);
    }

    public static <T> T coverBeanDictionary(T o, String[] dictionaryCodes, String[] columns, String[] textColumns) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        return coverBeanDictionary(o, dictionaryCodes, columns, textColumns, null);
    }

    public static <T> T coverBeanDictionary(T o, String[] dictionaryCodes, String[] columns, String[] textColumns, String[] defaultValues) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        Map<String, Field> cache;
        Class<?> clazz = o.getClass();
        if (Map.class.isAssignableFrom(clazz)) {
            Map map = ((Map) o);
            for (int i = 0; i < columns.length; i++) {
                String column = columns[i];
                String textColumn = textColumns[i];
                if (defaultValues == null || defaultValues.length <= i) {
                    map.put(textColumn, coverDicName(dictionaryCodes[i], String.valueOf(map.get(column))));
                } else {
                    String defaultValue = defaultValues[i];
                    map.put(textColumn, coverDicName(dictionaryCodes[i], String.valueOf(map.get(column)), defaultValue));
                }

            }
            return (T) map;
        } else {
            cache = initField(clazz, columns);
            cache.putAll(initField(clazz, textColumns));

            for (int i = 0; i < columns.length; i++) {
                String column = columns[i];
                String textColumn = textColumns[i];
                Field field = cache.get(column);
                Field textField = cache.get(textColumn);
                Object value = field.get(o);
                if (defaultValues == null || defaultValues.length <= i) {
                    textField.set(o, coverDicName(dictionaryCodes[i], String.valueOf(value)));
                } else {
                    String defaultValue = defaultValues[i];
                    textField.set(o, coverDicName(dictionaryCodes[i], String.valueOf(value), defaultValue));
                }

            }
            return o;
        }
    }

    /**
     * 根据父级字典与子字典(多，逗号分隔)，转换字典值
     *
     * @param parentCode   父级字典码
     * @param codes        子字典码
     * @param defaultValue 默认值
     * @return 逗号分隔字典值
     */
    public static String coverDicName(String parentCode, String codes, String defaultValue) {
        if (StringUtils.isBlank(parentCode) || StringUtils.isBlank(codes)) {
            return defaultValue;
        }
        StringBuilder nameCache = new StringBuilder();
        for (String code : codes.split(Constant.RegularAbout.COMMA)) {
            nameCache.append(coverDicName(String.format(CODE_FORMAT, parentCode, code))).append(Constant.RegularAbout.COMMA);
        }
        if (nameCache.length() > 0) {
            return nameCache.substring(0, nameCache.length() - 1);
        }
        return defaultValue;
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
    public static <T> List<T> coverBeanDictionary(List<T> list, String[] dictionaryCodes, String[] columns, String[] textColumns, String[] defaultValues) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        List<T> result = new ArrayList<>(list.size());
        for (T o : list) {
            result.add(coverBeanDictionary(o, dictionaryCodes, columns, textColumns, defaultValues));
        }
        return result;
    }

    /**
     * 根据父级树形字典码与子树形name获取字典
     *
     * @param code 字典
     * @return bean
     */
    public static DictionaryDataEntity coverDicBean(String code, String name) {
        List<DictionaryDataEntity> list = coverDicList(code);
        if (list == null || name == null) {
            return null;
        }

        String parentName;
        if (PatternUtil.find(SPLIT_CHAR, name)) {
            parentName = StringUtil.getSplitAtomic(name, SPLIT_CHAR, Constant.NumberAbout.ZERO);
            if (parentName != null) {
                DictionaryDataEntity parentEntity = coverDicBeanByChildName(code, parentName);
                if (parentEntity == null) {
                    return null;
                }
                return coverDicBean(code + Constant.RegularAbout.SPOT + parentEntity.getCode(), name.replaceFirst(parentName + Constant.RegularAbout.SPOT, Constant.RegularAbout.BLANK));
            }
            return null;
        } else {
            return coverDicBeanByChildName(code, name);
        }
    }

    /**
     * 根据子名字与树形父级字典码，获取该子对象
     *
     * @param code 树形父级字典码
     * @param name 子名字
     * @return 子
     */
    public static DictionaryDataEntity coverDicBeanByChildName(String code, String name) {
        List<DictionaryDataEntity> list = coverDicList(code);
        if (list == null || name == null) {
            return null;
        }
        return list.stream().filter(entity -> name.equals(entity.getName())).findFirst().orElse(null);
    }

    public static String coverDicFullCode(String rootCode, String fullName) {
        return coverDicFullCode(rootCode, null, fullName, Constant.RegularAbout.SPOT);
    }

    public static String coverDicFullCode(String rootCode, String fullName, String splitStr) {
        return coverDicFullCode(rootCode, null, fullName, splitStr);
    }

    public static String coverDicFullCode(String rootCode, StringBuilder builder, String fullName, String splitStr) {
        if (builder == null) {
            builder = new StringBuilder();
        }
        if (PatternUtil.find(SPLIT_CHAR, fullName)) {

            if (builder.length() != 0) {
                builder.insert(0, splitStr);
            }
            builder.insert(0, coverDicCode(rootCode, fullName));

            String parentCode = fullName.substring(0, PatternUtil.lastIndexOf(SPLIT_CHAR, fullName));
            return coverDicFullCode(rootCode, builder, parentCode, splitStr);
        } else {

            String parentCode = coverDicCode(rootCode, fullName);
            if (rootCode.equals(parentCode)) {
                return builder.toString();
            }
            if (builder.length() != 0) {
                builder.insert(0, splitStr);
            }
            return builder.insert(0, parentCode).insert(0, splitStr).insert(0, rootCode).toString();
        }
    }

    /**
     * 根据字典码转换名字
     *
     * @param fullCode 树形字典码
     * @return 树形字典值
     */
    public static String coverDicFullName(String fullCode) {
        return coverDicFullName(null, fullCode, Constant.RegularAbout.SPOT);
    }

    /**
     * 根据字典码转换名字
     *
     * @param code     树形字典码
     * @param splitStr 分隔符
     * @return 树形字典值
     */
    public static String coverDicFullName(String code, String splitStr) {
        return coverDicFullName(null, code, splitStr);
    }

    /**
     * 尾递归
     *
     * @param builder  结果容器
     * @param code     转换的编码
     * @param splitStr 分隔符
     * @return 结果
     */
    public static String coverDicFullName(StringBuilder builder, String code, String splitStr) {
        if (builder == null) {
            builder = new StringBuilder();
        }
        if (PatternUtil.find(SPLIT_CHAR, code)) {

            if (builder.length() != 0) {
                builder.insert(0, splitStr);
            }
            builder.insert(0, coverDicName(code));

            String parentCode = code.substring(0, PatternUtil.lastIndexOf(SPLIT_CHAR, code));
            return coverDicFullName(builder, parentCode, splitStr);
        } else {
            if (builder.length() != 0) {
                builder.insert(0, splitStr);
            }
            return builder.insert(0, coverDicName(code)).toString();
        }
    }


    /**
     * 递归获取字典码对应实体下n层中，code = targetCode的首个实例
     *
     * @param rootCode   上级字典码
     * @param targetCode 目标字典码
     * @return 字典实例
     */
    public static DictionaryDataEntity coverDicBeanByRecursive(String rootCode, String targetCode) {
        DictionaryDataEntity dic = coverDicBean(rootCode);
        return coverDicBeanByRecursive(dic, targetCode);
    }

    /**
     * 递归获取实体下n层中，code = targetCode的首个实例
     *
     * @param entity     实体
     * @param targetCode 目标字典码
     * @return 字典实例
     */
    public static DictionaryDataEntity coverDicBeanByRecursive(DictionaryDataEntity entity, String targetCode) {
        DictionaryDataEntity result = null;
        if (entity != null) {
            if (entity.containsKey(targetCode)) {
                result = entity.getCodeCache(targetCode);
            } else if (!entity.getChildren().isEmpty()) {
                for (DictionaryDataEntity dic : entity.getChildren()) {
                    DictionaryDataEntity c = coverDicBeanByRecursive(dic, targetCode);
                    if (c != null) {
                        return c;
                    }
                }
            }
        }
        return result;
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
            return StringUtil.getSplitLastAtomic(code, SPLIT_CHAR);
        }
        return dic.getCode();
    }

    /**
     * 根据父级树形字典码与name获取code
     *
     * @param defaultValue 默认值
     * @param name         子字典明文
     * @param code         父级字典码
     * @return bean
     */
    public static String coverDicCode(String code, String name, String defaultValue) {
        DictionaryDataEntity dic = coverDicBean(code, name);
        if (dic == null) {
            return defaultValue;
        }
        return dic.getCode();
    }

    /**
     * 转换字典对象
     *
     * @param code 字典码
     * @return bean
     */
    public static DictionaryDataEntity coverDicBean(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        if (threadLocal.get().containsKey(code)) {
            return threadLocal.get().get(code);
        } else {
            code = code.replaceAll(SPLIT_CHAR, Constant.RegularAbout.SPOT);
            DictionaryDataEntity entity = getCache().getFromMap("codeMap", code, DictionaryDataEntity.class);
            threadLocal.get().put(entity.getFullCode(), entity);
            return entity;
        }
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
            return StringUtil.getSplitLastAtomic(code, SPLIT_CHAR);
        }
        return targetEntity.getName();
    }

    /**
     * 编码转字典名
     *
     * @return 字典名
     */
    public static String coverDicName(String parentCode, String codes) {
        return coverDicName(parentCode, codes, null);
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
            return Lists.newArrayList();
        }
        return targetEntity.getChildren();
    }

    /**
     * 初始化转换字段集合
     *
     * @param clazz   类型
     * @param columns 字段名集合
     */
    private static Map<String, Field> initField(Class<?> clazz, String... columns) throws NoSuchFieldException {
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

    public static AgileCache getCache() {
        return CacheUtil.getCache(DEFAULT_CACHE_NAME);
    }

    /**
     * 字典自动转换，针对Dictionary注解进行解析
     *
     * @param o   目标数据
     * @param <T> 泛型
     */
    public static <T> void cover(T o) {
        if (ObjectUtils.isEmpty(o)) {
            return;
        }
        cover(Lists.newArrayList(o));
    }

    /**
     * 字典自动转换，针对Dictionary注解进行解析
     *
     * @param list 目标数据集
     * @param <T>  泛型
     */
    public static <T> void cover(List<T> list) {
        if (ObjectUtils.isEmpty(list)) {
            return;
        }
        Class<?> clazz = list.get(0).getClass();
        Set<ClassUtil.Target<Dictionary>> targets = ClassUtil.getAllEntityAnnotation(clazz, Dictionary.class);

        targets.forEach(target -> {
            Dictionary dictionary = target.getAnnotation();
            Member member = target.getMember();
            String parentDicCode = dictionary.dicCode();
            String linkColumn = dictionary.fieldName();
            Field field;
            if (member instanceof Method) {
                String fieldName = StringUtil.toLowerName(member.getName().substring(Constant.NumberAbout.THREE));
                field = ClassUtil.getField(clazz, fieldName);
                if (ObjectUtils.isEmpty(field)) {
                    return;
                }
            } else {
                field = (Field) member;
            }
            list.parallelStream().forEach(node -> {
                Object code = ObjectUtil.getFieldValue(node, linkColumn);
                String codeStr;
                if (code instanceof String) {
                    codeStr = (String) code;
                } else if (code instanceof Boolean) {
                    codeStr = (Boolean) code ? "1" : "0";
                } else {
                    codeStr = code.toString();
                }
                if (ObjectUtils.isEmpty(parentDicCode)) {
                    ObjectUtil.setValue(node, field, coverDicName(codeStr));
                } else {
                    ObjectUtil.setValue(node, field, coverDicName(parentDicCode, codeStr));
                }
            });
        });
        threadLocal.remove();
    }
}
