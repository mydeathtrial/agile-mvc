package com.agile.common.util;

import org.springframework.util.ClassUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author mydeathtrial on 2017/4/24
 */
public class ClassUtil extends ClassUtils {

    /**
     * 基本类型转换包装类
     *
     * @param name 类型名字
     * @return 包装类名字
     */
    public static String toWrapperNameFromName(String name) {
        String result;
        switch (name) {
            case "byte":
                result = "Byte";
                break;
            case "short":
                result = "Short";
                break;
            case "int":
                result = "Integer";
                break;
            case "long":
                result = "Long";
                break;
            case "float":
                result = "Float";
                break;
            case "double":
                result = "Double";
                break;
            case "boolean":
                result = "Boolean";
                break;
            case "char":
                result = "Character";
                break;
            default:
                result = name;
        }
        return result;
    }

    /**
     * 基本类型转换包装类
     *
     * @param clazz 类型
     * @return 包装类名字
     */
    public static String toSwaggerTypeFromName(Class clazz) {
        String result = "String";
        if (clazz == short.class || clazz == Short.class || clazz == int.class || clazz == Integer.class || clazz == long.class || clazz == Long.class) {
            result = "integer";
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            result = "boolean";
        } else if (clazz == double.class || clazz == Double.class || clazz == float.class || clazz == Float.class) {
            result = "number";
        }
        return result;
    }

    public static boolean isWrapOrPrimitive(Class clazz) {
        if (clazz.isPrimitive()) {
            return true;
        }
        try {
            if (((Class) clazz.getDeclaredField("TYPE").get(null)).isPrimitive()) {
                return true;
            }
        } catch (NoSuchFieldException e) {
            return false;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean canCastClass(Class clazz) {
        if (isWrapOrPrimitive(clazz)) {
            return true;
        }
        return String.class == clazz || BigDecimal.class == clazz || Date.class == clazz;
    }

    public static boolean isJavaClass(Class clazz) {
        return clazz.getPackage().getName().startsWith("java.");
    }
}
