package com.agile.common.util;

import com.agile.common.base.Constant;
import org.springframework.util.ObjectUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 佟盟 on 2017/1/9
 */
public class ObjectUtil extends ObjectUtils {
    public static void copyPropertiesOfNotNull(Object source, Object target) {
        if (ObjectUtil.isEmpty(source) || ObjectUtil.isEmpty(target)) {
            return;
        }
        Set<Field> sourceFields = getAllField(source.getClass());
        List<String> arguments = new ArrayList<>();
        for (Field field : sourceFields) {
            field.setAccessible(true);
            try {
                if (field.get(source) != null) {
                    arguments.add(field.getName());
                }
            } catch (Exception ignored) {
            }
        }

        copyProperties(source, target, arguments.toArray(new String[]{}), ContainOrExclude.INCLUDE);
    }

    public static void copyProperties(Object source, Object target, String[] arguments, ContainOrExclude containOrExclude) {
        copyProperties(source, target, Constant.RegularAbout.BLANK, Constant.RegularAbout.BLANK, arguments, containOrExclude);
    }

    public static void copyProperties(Object source, Object target, String prfix, String sufix) {
        copyProperties(source, target, prfix, sufix, new String[]{}, ContainOrExclude.INCLUDE);
    }

    /**
     * 复制对象中哪些属性
     *
     * @param source           原对象
     * @param target           新对象
     * @param arguments        属性列表
     * @param containOrExclude 包含或排除
     */
    public static void copyProperties(Object source, Object target, String prefix, String sufix, String[] arguments, ContainOrExclude containOrExclude) {
        if (ObjectUtil.isEmpty(source) || ObjectUtil.isEmpty(target)) {
            return;
        }

        Set<Field> targetFields = ObjectUtil.getAllField(target.getClass());
        for (Field field : targetFields) {
            field.setAccessible(true);
            String propertyName = field.getName();

            propertyName = StringUtil.isBlank(prefix) ? propertyName : prefix + StringUtil.toUpperName(propertyName);
            propertyName = StringUtil.isBlank(sufix) ? propertyName : propertyName + StringUtil.toUpperName(sufix);

            Field sourceProperty = getField(source.getClass(), propertyName);
            if (sourceProperty == null) {
                continue;
            }
            if (!ObjectUtil.isEmpty(arguments)) {
                switch (containOrExclude) {
                    case EXCLUDE:
                        if (ArrayUtil.contains(arguments, sourceProperty.getName())) {
                            continue;
                        }
                        break;
                    case INCLUDE:
                        if (!ArrayUtil.contains(arguments, sourceProperty.getName())) {
                            continue;
                        }
                        break;
                    default:
                }
            }

            try {
                sourceProperty.setAccessible(true);
                Object value = sourceProperty.get(source);

                Class<?> type = field.getType();
                if (!type.isAssignableFrom(value.getClass())) {
                    value = cast(type, value);
                }

                field.setAccessible(true);
                field.set(target, value);

            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 复制对象属性
     *
     * @param source 原对象
     * @param target 新对象
     */
    public static void copyProperties(Object source, Object target) {
        copyProperties(source, target, null, null, null, null);
    }

    /**
     * 比较两个对象是否继承于同一个类
     *
     * @param source 源对象
     * @param target 目标对象
     * @return 是否相同
     */
    public static Boolean compareClass(Object source, Object target) {
        return isEmpty(source) ? isEmpty(target) : (!isEmpty(target) && source.getClass() == (target.getClass()));
    }

    /**
     * 比较两个对象属性是否相同
     *
     * @param source 源对象
     * @param target 目标对象
     * @return 是否相同
     */
    public static boolean compare(Object source, Object target) {
        return isEmpty(source) && isEmpty(target) || source.equals(target);
    }

    /**
     * 比较两个对象属性是否相同
     *
     * @param source 源对象
     * @param target 目标对象
     * @return 是否相同
     */
    public static boolean compareValue(Object source, Object target, String... excludeProperty) {
        if (isEmpty(source)) {
            return isEmpty(target);
        } else {
            if (isEmpty(target)) {
                return false;
            }
            try {
                List<Different> list = getDifferenceProperties(source, target, excludeProperty);
                if (list != null && list.size() > 0) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 获取两个对象的不同属性列表
     *
     * @param source 源对象
     * @param target 目标对象
     * @return 值不相同的属性列表
     * @throws IllegalAccessException 调用过程异常
     */
    public static List<Different> getDifferenceProperties(Object source, Object target, String... excludeProperty) throws IllegalAccessException {
        if ((!compareClass(source, target) || compare(source, target) || isEmpty(source)) != isEmpty(target)) {
            return null;
        }
        List<Different> result = new ArrayList<>();
        Object sourceObject = isEmpty(source) ? target : source;
        Object targetObject = isEmpty(source) ? source : target;
        Class sourceClass = sourceObject.getClass();
        Set<Field> fields = getAllField(sourceClass);
        for (Field field : fields) {
            field.setAccessible(true);
            if (excludeProperty != null && ArrayUtil.contains(excludeProperty, field.getName())) {
                continue;
            }
            Object sourceValue = field.get(sourceObject);
            Object targetValue = field.get(targetObject);
            if (compare(sourceValue, targetValue)) {
                continue;
            }

            result.add(new Different(field.getName(), field.getType().getTypeName(), String.valueOf(targetValue), String.valueOf(sourceValue)));
        }
        return result;
    }

    /**
     * 区别信息
     */
    public static class Different {
        private String propertyName;
        private String propertyType;
        private String newValue;
        private String oldValue;

        public Different(String propertyName, String propertyType, String newValue, String oldValue) {
            this.propertyName = propertyName;
            this.propertyType = propertyType;
            this.newValue = newValue;
            this.oldValue = oldValue;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public String getPropertyType() {
            return propertyType;
        }

        public String getNewValue() {
            return newValue;
        }

        public String getOldValue() {
            return oldValue;
        }
    }


    /**
     * 从Map对象中获取指定类型对象
     *
     * @param clazz 想要获取的对象类型
     * @param map   属性集合
     * @return 返回指定对象类型对象
     */
    public static <T> T getObjectFromMap(Class<T> clazz, Map<String, Object> map) {
        return getObjectFromMap(clazz, map, "", "");
    }

    /**
     * 从Map对象中获取指定类型对象
     *
     * @param clazz 想要获取的对象类型
     * @param map   属性集合
     * @return 返回指定对象类型对象
     */
    public static <T> T getObjectFromMap(Class<T> clazz, Map<String, Object> map, String prefix) {
        return getObjectFromMap(clazz, map, prefix, "");
    }

    /**
     * 对象转属性下划线式key值Map结构
     *
     * @param o 对象
     * @return Map
     */
    public static Map<String, Object> getUnderlineMapFromObject(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        Map<String, Object> result = new HashMap<>(fields.length);
        if (fields.length > 0) {
            for (Field field : fields) {
                field.setAccessible(true);
                String key = StringUtil.camelToUnderline(field.getName());
                try {
                    result.put(key, field.get(o));
                } catch (IllegalAccessException ignored) {
                }
            }
        }
        return result;
    }

    /**
     * 对象集转Map集，转属性下划线式key值
     *
     * @param list 对象集合
     * @return map集合
     */
    public static List<Map<String, Object>> getUnderlineMapFromListObject(Iterable<Object> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (list != null) {
            for (Object o : list) {
                result.add(getUnderlineMapFromObject(o));
            }
        }

        return result;
    }

    /**
     * 从Map对象中获取指定类型对象
     *
     * @param clazz  想要获取的对象类型
     * @param map    属性集合
     * @param prefix 属性前缀
     * @return 返回指定对象类型对象
     */
    public static <T> T getObjectFromMap(Class<T> clazz, Map<String, Object> map, String prefix, String suffix) {

        try {
            if (ObjectUtil.isEmpty(map)) {
                return null;
            }
            T object = clazz.newInstance();
            boolean notNull = true;
            Set<Field> fields = getAllField(clazz);
            for (Field field : fields) {
                String propertyName = prefix + field.getName() + suffix;
                String camelToUnderlineKey = StringUtil.camelToUnderline(propertyName);
                String camelToUnderlineKeyUpper = camelToUnderlineKey.toUpperCase();
                String camelToUnderlineKeyLower = camelToUnderlineKey.toLowerCase();
                String key = null;
                if (map.containsKey(propertyName)) {
                    key = propertyName;
                } else if (map.containsKey(camelToUnderlineKey)) {
                    key = camelToUnderlineKey;
                } else if (map.containsKey(camelToUnderlineKeyUpper)) {
                    key = camelToUnderlineKeyUpper;
                } else if (map.containsKey(camelToUnderlineKeyLower)) {
                    key = camelToUnderlineKeyLower;
                }
                if (key != null) {
                    try {
                        field.setAccessible(true);
                        Class<?> type = field.getType();
                        Object value = map.get(key);
                        Object targetValue;
                        if (!type.isArray() && value.getClass().isArray()) {
                            targetValue = cast(field.getType(), ((String[]) value)[0]);
                        } else if (type.isArray() && value.getClass().isArray()) {
                            targetValue = value;
                        } else if (List.class.isAssignableFrom(type) && List.class.isAssignableFrom(value.getClass())) {
                            targetValue = value;
                        } else {
                            targetValue = cast(type, value);
                        }
                        if (targetValue == null) {
                            targetValue = value;
                        }
                        if (notNull) {
                            notNull = false;
                        }
                        String fieldName = field.getName();
                        Method setMethod;
                        String setMethodName = "set" + StringUtil.toUpperName(fieldName);
                        try {
                            setMethod = ClassUtil.getMethod(clazz, setMethodName, type);
                        } catch (Exception e) {
                            if (targetValue == null || StringUtil.isBlank(targetValue.toString())) {
                                continue;
                            }
                            setMethod = clazz.getDeclaredMethod(setMethodName, String.class);
                        }
                        try {
                            setMethod.invoke(object, targetValue);
                        } catch (Exception e) {
                            field.set(object, targetValue);
                        }

                    } catch (Exception ignored) {
                    }
                }
            }
            if (notNull) {
                return null;
            }
            return object;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Method getMethod(Class clazz, String fieldName) {
        Set<Method> methods = getAllMethod(clazz);
        for (Method method : methods) {
            if (method.getName().equals(fieldName)) {
                return method;
            }
        }
        return null;
    }

    public static Field getField(Class clazz, String fieldName) {
        Set<Field> fields = getAllField(clazz);
        Map<String, Field> targetFields = new HashMap<>(Constant.NumberAbout.TWO);
        String targetFieldName = StringUtil.camelToUrlRegex(fieldName);
        for (Field field : fields) {
            if (StringUtil.containMatchedString(targetFieldName, field.getName())) {
                targetFields.put(field.getName(), field);
            }
        }
        if (targetFields.size() > 1) {
            return targetFields.get(fieldName);
        } else if (targetFields.size() == 1) {
            return targetFields.values().iterator().next();
        }
        return null;
    }

    /**
     * 获取所有类型属性集
     *
     * @param clazz
     * @return
     */
    public static Set<Field> getAllField(Class clazz) {
        Set<Field> fields = new HashSet<>();
        try {
            Field[] selfFields = clazz.getDeclaredFields();
            Field[] extendFields = clazz.getFields();
            Class superClass = clazz.getSuperclass();
            if (superClass != null) {
                fields.addAll(getAllField(superClass));
            }
            fields.addAll(Arrays.asList(selfFields));
            fields.addAll(Arrays.asList(extendFields));
        } catch (Exception ignored) {
        }
        return fields;
    }

    public static Set<Method> getAllMethod(Class clazz) {
        Set<Method> fields = new HashSet<>();
        try {
            Method[] selfFields = clazz.getDeclaredMethods();
            Method[] extendFields = clazz.getMethods();
            Class superClass = clazz.getSuperclass();
            if (superClass != null) {
                fields.addAll(getAllMethod(superClass));
            }
            fields.addAll(Arrays.asList(selfFields));
            fields.addAll(Arrays.asList(extendFields));
        } catch (Exception ignored) {
        }
        return fields;
    }

    public static Set<Target> getAllFieldAnnotation(Class clazz, Class annotationClass) {
        Set<Field> fields = getAllField(clazz);
        Set<Target> set = new HashSet<>();
        Iterator<Field> it = fields.iterator();
        while (it.hasNext()) {
            Field field = it.next();
            Annotation annotation = field.getAnnotation(annotationClass);
            if (annotation != null) {
                set.add(new Target(field, annotation));
            }
        }
        return set;
    }

    public static Set<Target> getAllMethodAnnotation(Class clazz, Class annotationClass) {
        Set<Method> fields = getAllMethod(clazz);
        Set<Target> set = new HashSet<>();
        Iterator<Method> it = fields.iterator();
        while (it.hasNext()) {
            Method method = it.next();
            Annotation annotation = method.getAnnotation(annotationClass);
            if (annotation != null) {
                set.add(new Target(method, annotation));
            }
        }
        return set;
    }

    /**
     * 获取所有字段注解
     *
     * @param clazz 类
     * @return 注解结果集
     */
    public static Set<Target> getAllEntityAnnotation(Class clazz, Class annotation) {
        Set<Target> fieldAnnotation = getAllFieldAnnotation(clazz, annotation);
        Set<Target> methodAnnotation = getAllMethodAnnotation(clazz, annotation);
        Iterator<Target> it = methodAnnotation.iterator();
        while (it.hasNext()) {
            Target target = it.next();
            String name = target.getMember().getName();
            if (name.startsWith("get")) {
                final int length = 3;
                Field targetField = getField(clazz, StringUtil.toLowerName(name.substring(length)));
                fieldAnnotation.add(new Target(targetField, target.getAnnotation()));
            }
        }
        return fieldAnnotation;
    }

    /**
     * 获取所有字段注解
     *
     * @param clazz 类
     * @return 注解结果集
     */
    public static <T extends Annotation> T getAllEntityPropertyAnnotation(Class clazz, Field field, Class<T> annotation) throws NoSuchMethodException {
        T result = null;
        T fieldDeclaredAnnotations = field.getDeclaredAnnotation(annotation);
        if (fieldDeclaredAnnotations != null) {
            result = fieldDeclaredAnnotations;
        }

        T fieldAnnotations = field.getAnnotation(annotation);
        if (fieldAnnotations != null) {
            result = fieldAnnotations;
        }

        String getMethodName = String.format("get%s", StringUtil.toUpperName(field.getName()));
        Method declaredMethod = clazz.getDeclaredMethod(getMethodName);
        T methodDeclaredAnnotations = declaredMethod.getDeclaredAnnotation(annotation);
        if (methodDeclaredAnnotations != null) {
            result = methodDeclaredAnnotations;
        }

        Method method = clazz.getMethod(getMethodName);
        T methodAnnotations = method.getAnnotation(annotation);
        if (methodAnnotations != null) {
            result = methodAnnotations;
        }

        return result;
    }

    private static void addAll(Annotation[] methodAnnotations, List<Annotation> list) {
        if (methodAnnotations != null) {
            list.addAll(ArrayUtil.asList(methodAnnotations));
        }
    }

    /**
     * 判断对象非空属性是否存值（排除主键）
     * 判断对象非空属性是否存值（排除主键）
     */
    public static boolean isValidity(Object object) {
        boolean result = true;
        if (object == null) {
            return false;
        }
        Class<?> clazz = object.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (!method.getName().startsWith("get")) {
                continue;
            }
            try {
                if (isEmpty(method.getAnnotation(Id.class))) {
                    Column columInfo = method.getAnnotation(Column.class);
                    if (!isEmpty(columInfo) && !columInfo.nullable() && isEmpty(method.invoke(object))) {
                        result = false;
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return result;
    }

    /**
     * 判断对象属性是否全空
     */
    public static boolean isAllNullValidity(Object object) {
        boolean result = true;
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            try {
                if (!isEmpty(field.get(object))) {
                    result = false;
                }
            } catch (Exception e) {
                continue;
            }
        }
        return result;
    }

    /**
     * 对象类型转换
     *
     * @param clazz 类型
     * @param value 值
     * @return 转换后的值
     */
    public static <T> T cast(Class<T> clazz, Object value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }

        Object temp = null;

        boolean isSame = Map.class.isAssignableFrom(clazz) && Map.class.isAssignableFrom(value.getClass()) || (clazz == value.getClass());
        if (isSame) {
            return (T) value;
        }
        if (Map.class.isAssignableFrom(value.getClass())) {
            temp = getObjectFromMap(clazz, (Map<String, Object>) value);
        } else {
            String valueStr = String.valueOf(value);
            if (clazz == String.class) {
                temp = valueStr;
            }
            if (clazz == java.util.Date.class) {
                String format = "yyyy-MM-dd";
                if (StringUtil.containMatchedString("[\\d]{4}-[\\d]{1,2}-[\\d]{1,2} [\\d]{1,2}:[\\d]{1,2}:[\\d]{1,2}", valueStr)) {
                    format = "yyyy-MM-dd HH:mm:ss";
                } else if (StringUtil.containMatchedString("[\\d]{4}/[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}:[\\d]{1,2}:[\\d]{1,2}", valueStr)) {
                    format = "yyyy/MM/dd HH:mm:ss";
                } else if (StringUtil.containMatchedString(Constant.RegularAbout.DATE_YYYYIMMIDD, valueStr)) {
                    format = "yyyy/MM/dd";
                } else if (StringUtil.containMatchedString(Constant.RegularAbout.DATE_YYYY_MM_DD, valueStr)) {
                    format = "yyyy-MM-dd";
                } else if (StringUtil.containMatchedString(Constant.RegularAbout.DATE_YYYYMMDD, valueStr)) {
                    format = "yyyyMMdd";
                } else if (StringUtil.containMatchedString("[\\d]+", valueStr)) {
                    temp = new java.util.Date(Long.parseLong(valueStr));
                }
                if (temp == null) {
                    try {
                        temp = DateUtil.toDateByFormat(valueStr, format);
                    } catch (ParseException e) {
                        return null;
                    }
                }
            }
            if (clazz == Date.class) {
                temp = Date.valueOf(valueStr);
            }
            if (clazz == Long.class || clazz == long.class) {
                temp = Long.parseLong(valueStr);
            }
            if (clazz == Integer.class || clazz == int.class) {
                temp = Integer.parseInt(valueStr);
            }
            if (clazz == BigDecimal.class) {
                temp = new BigDecimal(valueStr);
            }
            if (clazz == Double.class || clazz == double.class) {
                temp = Double.parseDouble(valueStr);
            }
            if (clazz == Float.class || clazz == float.class) {
                temp = Float.parseFloat(valueStr);
            }
            if (clazz == Boolean.class || clazz == boolean.class) {
                temp = Boolean.parseBoolean(valueStr);
            }
            if (clazz == Byte.class || clazz == byte.class) {
                temp = Byte.parseByte(valueStr);
            }
            if (clazz == Short.class || clazz == short.class) {
                temp = Short.parseShort(valueStr);
            }
            if (clazz == Character.class || clazz == char.class) {
                char[] array = valueStr.toCharArray();
                temp = array.length > 0 ? array[0] : null;
            }
        }
        return (T) (temp);
    }

    public static boolean compareOfNotNull(Object source, Object target) {
        Field[] fields = source.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            if ("serialVersionUID".equals(field.getName())) {
                continue;
            }
            try {
                Object sourceValue = field.get(source);
                if (sourceValue == null) {
                    continue;
                }
                Object targetValue = field.get(target);
                if (!sourceValue.equals(targetValue)) {
                    return false;
                }
            } catch (Exception e) {
                continue;
            }
        }
        return true;
    }

    /**
     * 包含或者排除
     */
    public enum ContainOrExclude {
        /**
         * 包含
         */
        INCLUDE,
        EXCLUDE
    }

    /**
     * 目标
     */
    public static class Target {
        private Member member;
        private Annotation annotation;

        public Target(Member member, Annotation annotation) {
            this.member = member;
            this.annotation = annotation;
        }

        public Member getMember() {
            return member;
        }

        public void setMember(Member member) {
            this.member = member;
        }

        public Annotation getAnnotation() {
            return annotation;
        }

        public void setAnnotation(Annotation annotation) {
            this.annotation = annotation;
        }
    }

    /**
     * 对象转字符串
     *
     * @param o       对象
     * @param exclude 排除转换的字段名字
     * @return 字符串
     */
    public static String objectToString(Object o, String... exclude) {
        Class clazz = o.getClass();
        StringBuilder target = new StringBuilder(clazz.getSimpleName()).append("{");
        Set<Field> fields = ObjectUtil.getAllField(clazz);
        int i = 0;
        for (Field field : fields) {
            try {
                if (ArrayUtil.contains(exclude, field.getName())) {
                    i++;
                    continue;
                }
                field.setAccessible(true);
                if (i != 0) {
                    target.append(", ");
                }
                target.append(field.getName()).append("='").append(field.get(o));
                if (i == fields.size() - 1) {
                    target.append('}');
                } else {
                    target.append('\'');
                }
            } catch (IllegalAccessException e) {
                continue;
            }
            i++;
        }
        return target.toString();
    }
}
