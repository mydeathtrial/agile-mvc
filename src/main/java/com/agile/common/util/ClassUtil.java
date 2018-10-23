package com.agile.common.util;

import org.springframework.util.ClassUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by mydeathtrial on 2017/4/24
 */
public class ClassUtil extends ClassUtils {

    /**
     * 基本类型转换包装类
     * @param name 类型名字
     * @return 包装类名字
     */
    public static String toWrapperNameFromName(String name){
        switch (name){
            case "byte":return "Byte";
            case "short":return "Short";
            case "int":return "Integer";
            case "long":return "Long";
            case "float":return "Float";
            case "double":return "Double";
            case "boolean":return "Boolean";
            case "char":return "Character";
            default: return name;
        }
    }

    /**
     * 基本类型转换包装类
     * @param name 类型名字
     * @return 包装类名字
     */
    public static String toSwaggerTypeFromName(String name){
        switch (name){
            case "byte":return "string";
            case "Byte":return "string";
            case "short":return "integer";
            case "Short":return "integer";
            case "int":return "integer";
            case "Integer":return "integer";
            case "long":return "integer";
            case "Long":return "integer";
            case "char":return "string";
            case "Character":return "string";
            case "Timestamp":return "string";
            case "Date":return "string";
            case "String":return "string";
            case "Boolean":return "boolean";
            case "Double":return "number";
            case "Float":return "number";
            default: return name;
        }
    }

    public static boolean isWrapOrPrimitive(Class clazz){
        if(clazz.isPrimitive())return true;
        try {
            if(((Class)clazz.getDeclaredField("TYPE").get(null)).isPrimitive())return true;
        } catch (NoSuchFieldException e) {
            return false;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isCustomClass(Class clazz){
        if(isWrapOrPrimitive(clazz))return true;
        return String.class == clazz || BigDecimal.class == clazz || Date.class == clazz;
    }

}
