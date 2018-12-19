package com.agile.common.util;

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
 * Created by 佟盟 on 2017/1/9
 *
 * @author 佟盟
 */
public class ObjectUtil extends ObjectUtils {
    public enum ContainOrExclude {
        INCLUDE, EXCLUDE
    }

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

    /**
     * 复制对象中哪些属性
     *
     * @param source           原对象
     * @param target           新对象
     * @param arguments        属性列表
     * @param containOrExclude 包含或排除
     */
    public static void copyProperties(Object source, Object target, String[] arguments, ContainOrExclude containOrExclude) {
        if (ObjectUtil.isEmpty(source) || ObjectUtil.isEmpty(target)) {
            return;
        }

        Set<Field> sourceFields = ObjectUtil.getAllField(source.getClass());
        for (Field field : sourceFields) {
            field.setAccessible(true);
            String propertyName = field.getName();

            if (!ObjectUtil.isEmpty(arguments)) {
                switch (containOrExclude) {
                    case EXCLUDE:
                        if (ArrayUtil.contains(arguments, propertyName)) {
                            continue;
                        }
                        break;
                    case INCLUDE:
                        if (!ArrayUtil.contains(arguments, propertyName)) {
                            continue;
                        }
                        break;
                }
            }

            try {
                Object value = field.get(source);
                Field targetProperty;
                targetProperty = getField(target.getClass(), propertyName);
                if (targetProperty == null) {
                    continue;
                }

                targetProperty.setAccessible(true);
                targetProperty.set(target, value);
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
        copyProperties(source, target, null, null);
    }


    /**
     * 比较两个对象是否继承于同一个类
     *
     * @param source 源对象
     * @param target 目标对象
     * @return 是否相同
     */
    public static Boolean compareClass(Object source, Object target) {
        return isEmpty(source) ? isEmpty(target) : (!isEmpty(target) && source.getClass().equals(target.getClass()));
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
                List<Map<String, Object>> list = getDifferenceProperties(source, target, excludeProperty);
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
    public static List<Map<String, Object>> getDifferenceProperties(Object source, Object target, String... excludeProperty) throws IllegalAccessException {
        if ((!compareClass(source, target) || compare(source, target) || isEmpty(source)) != isEmpty(target)) {
            return null;
        }
        List<Map<String, Object>> result = new ArrayList<>();
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
            result.add(new HashMap<String, Object>() {
                private static final long serialVersionUID = -3959176970036247143L;

                {
                    put("propertyName", field.getName());
                    put("propertyType", field.getType());
                    put("oldValue", sourceValue);
                    put("newValue", targetValue);
                }
            });
        }
        return result;
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
                String key = map.containsKey(propertyName) ? propertyName : map.containsKey(camelToUnderlineKey) ? camelToUnderlineKey : null;
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
                        } else {
                            targetValue = cast(type, value);
                        }
                        if (targetValue != null) {
                            if (notNull) {
                                notNull = false;
                            }
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
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
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

    public static Set<Target> getAllColumnAnnotation(Class clazz) {
        Set<Target> fieldAnnotation = getAllFieldAnnotation(clazz, Column.class);
        Set<Target> methodAnnotation = getAllMethodAnnotation(clazz, Column.class);
        Iterator<Target> it = methodAnnotation.iterator();
        while (it.hasNext()) {
            Target target = it.next();
            String name = target.getMember().getName();
            if (name.startsWith("get")) {
                Field field = getField(clazz, StringUtil.toLowerName(name.substring(3)));
                fieldAnnotation.add(new Target(field, target.getAnnotation()));
            }
        }
        return fieldAnnotation;
    }

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
     * 判断对象非空属性是否存值（排除主键）
     * 判断对象非空属性是否存值（排除主键）
     */
    public static boolean isValidity(Object object) {
        boolean result = true;
        if (object == null) {
            return result;
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
            } catch (Exception e) {
                continue;
            }
        }
        return result;
    }

    public static boolean haveId(Field field, Object entity) {
        try {
            Object id = field.get(entity);
            if (id == null || "".equals(id.toString().trim())) {
                return false;
            }
        } catch (IllegalAccessException e) {
            return false;
        }

        return true;
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
        String valueStr = String.valueOf(value);
        if (clazz == String.class) {
            temp = valueStr;
        }
        if (clazz == java.util.Date.class) {
            String format = "yyyy-MM-dd";
            if (StringUtil.containMatchedString("[\\d]{4}-[\\d]{1,2}-[\\d]{1,2} [\\d]{1,2}:[\\d]{1,2}:[\\d]{1,2}", valueStr)) {
                format = "yyyy-MM-dd HH:mm:ss";
            } else if (StringUtil.containMatchedString("[\\d]{4}-[\\d]{1,2}-[\\d]{1,2}", valueStr)) {
                format = "yyyy-MM-dd";
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
}
