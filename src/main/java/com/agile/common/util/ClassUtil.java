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
     * @param name 类型名字
     * @return 包装类名字
     */
    public static String toSwaggerTypeFromName(String name) {
        String result;
        switch (name) {
            case "byte":
                result = "string";
                break;
            case "Byte":
                result = "string";
                break;
            case "short":
                result = "integer";
                break;
            case "Short":
                result = "integer";
                break;
            case "int":
                result = "integer";
                break;
            case "Integer":
                result = "integer";
                break;
            case "long":
                result = "integer";
                break;
            case "Long":
                result = "integer";
                break;
            case "char":
                result = "string";
                break;
            case "Character":
                result = "string";
                break;
            case "Timestamp":
                result = "string";
                break;
            case "Date":
                result = "string";
                break;
            case "String":
                result = "string";
                break;
            case "Boolean":
                result = "boolean";
                break;
            case "Double":
                result = "number";
                break;
            case "Float":
                result = "number";
                break;
            default:
                result = name;
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
