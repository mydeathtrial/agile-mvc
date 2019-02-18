package com.agile.common.util;

import com.agile.common.base.Constant;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 佟盟 on 2017/1/9
 */
public final class StringUtil extends StringUtils {
    /**
     * 驼峰式转下划线
     *
     * @param text 任意字符串
     * @return 返回驼峰字符串
     */
    public static String camelToUnderline(String text) {
        String regex = Constant.RegularAbout.UPER;
        if (!haveMatchedString(regex, text)) {
            return text;
        }

        StringBuilder cacheStr = new StringBuilder(text);
        Matcher matcher = Pattern.compile(regex).matcher(text);
        int i = 0;
        while (matcher.find()) {
            int position = matcher.start() + i;
            if (position >= 1 && !"_".equals(cacheStr.substring(position - 1, position))) {
                cacheStr.replace(position, position + 1, "_" + cacheStr.substring(position, position + 1).toLowerCase());
                i++;
            }
        }
        return cacheStr.toString();
    }

    /**
     * 驼峰式转下路径匹配
     *
     * @param text 任意字符串
     * @return 返回路径匹配正则
     */
    public static String camelToUrlRegex(String text) {
        StringBuilder s = new StringBuilder();
        String[] setps = camelToUnderline(text).split("_");
        for (int i = 0; i < setps.length; i++) {
            String step = setps[i];
            String first = step.substring(0, 1);

            s.append(String.format("[%s]", first.toLowerCase() + first.toUpperCase()) + step.substring(1));
            if (i == setps.length - 1) {
                continue;
            }
            s.append(Constant.RegularAbout.URL_REGEX);
        }
        return s.toString();
    }

    /**
     * 特殊符号转驼峰式
     *
     * @param text 任意字符串
     * @return 返回驼峰字符串
     */
    public static String signToCamel(String text) {
        String regex = Constant.RegularAbout.HUMP;
        if (!haveMatchedString(regex, text)) {
            return text;
        }

        StringBuilder cacheStr = new StringBuilder(text);
        Matcher matcher = Pattern.compile(regex).matcher(text);
        int i = 0;
        while (matcher.find()) {
            int position = matcher.end() - (i++);
            if (position + 1 <= cacheStr.length()) {
                cacheStr.replace(position - 1, position + 1, cacheStr.substring(position, position + 1).toUpperCase());
            } else {
                break;
            }
        }
        return cacheStr.toString();
    }

    /**
     * 字符串转首字母大写驼峰名
     *
     * @param text 任意字符串
     * @return 返回首字母大写的驼峰字符串
     */
    public static String toUpperName(String text) {
        if (isEmpty(text)) {
            return "";
        }
        String camelString = signToCamel(text);
        return camelString.substring(0, 1).toUpperCase() + camelString.substring(1);
    }

    /**
     * 字符串转首字母小写驼峰名
     *
     * @param text 任意字符串
     * @return 返回首字母小写的驼峰字符串
     */
    public static String toLowerName(String text) {
        if (isEmpty(text)) {
            return "";
        }
        String camelString = signToCamel(text);
        return camelString.substring(0, 1).toLowerCase() + camelString.substring(1);
    }

    /**
     * map格式转url参数路径
     *
     * @param map 参数集合
     * @return url参数
     */
    public static String fromMapToUrl(Map<String, Object> map) {
        StringBuilder mapOfString = new StringBuilder(Constant.RegularAbout.NULL);
        for (Map.Entry<String, Object> entity : map.entrySet()) {
            Object value = entity.getValue();
            if (value.getClass().isArray()) {
                for (Object v : (Object[]) value) {
                    mapOfString.append(Constant.RegularAbout.AND).append(entity.getKey());
                    mapOfString.append(Constant.RegularAbout.EQUAL).append(v);
                }
            } else if (!(value instanceof Page) && !(value instanceof BeanPropertyBindingResult)) {
                mapOfString.append(Constant.RegularAbout.AND).append(entity.getKey());
                mapOfString.append(Constant.RegularAbout.EQUAL).append(entity.getValue());
            }
        }
        String urlParam = mapOfString.toString();
        return urlParam.startsWith(Constant.RegularAbout.AND) ? urlParam.substring(1) : urlParam;
    }

    /**
     * 字符串比较
     *
     * @param resource 比较方
     * @param target   参照方
     * @return 是否相同
     */
    public static boolean compare(String resource, String target) {
        return ObjectUtil.isEmpty(resource) ? ObjectUtil.isEmpty(target) : resource.equals(target);
    }

    /**
     * 全部匹配
     *
     * @param regex 正则表达式
     * @param text  正文
     * @return 匹配的字符串
     */
    public static boolean containMatchedString(String regex, String text) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    /**
     * 部分匹配
     *
     * @param regex 正则表达式
     * @param text  正文
     * @return 匹配的字符串
     */
    public static boolean findMatchedString(String regex, String text) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    /**
     * 获取字符串中匹配正则表达式的部分
     *
     * @param regex 正则表达式
     * @param text  正文
     * @return 匹配的字符串
     */
    public static String[] getMatchedString(String regex, String text) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            sb.append(matcher.group()).append(",");
        }
        if (isEmpty(sb.toString())) {
            return null;
        }
        return sb.toString().split(",");
    }

    public static String getGroupString(String regex, String text, int index) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        int count = matcher.groupCount();
        if (count > 0) {
            if (matcher.find()) {
                return matcher.group(index);
            }
        }
        return null;
    }

    /**
     * 匹配出正则表达式中所有的括号内容
     *
     * @param regex 正则
     * @param text  目标串
     * @return 按顺序匹配出来的括号内容
     */
    public static LinkedList<String> getGroupString(String regex, String text) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        int count = matcher.groupCount();

        if (count > 0) {
            LinkedList<String> temp = new LinkedList<>();
            if (matcher.find()) {
                for (int i = 1; i < count + 1; i++) {
                    temp.add(matcher.group(i));
                }
                return temp;
            }
        }
        return null;
    }

    /**
     * 根据正则查找字符串中包含的group形式参数
     *
     * @param regex 正则
     * @param text  目标串
     * @return map形式参数集合
     */
    public static Map<String, String> getParamByRegex(String regex, String text) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        int count = matcher.groupCount();

        String[] groupNames = getMatchedString("(?<=<)[\\w]+(?=>)", regex);
        if (count > 0 && groupNames != null && groupNames.length > 0) {
            Map<String, String> result = new HashMap<>(groupNames.length);
            if (matcher.find()) {
                for (String key : groupNames) {
                    result.put(key, matcher.group(key));
                }
                return result;
            }
        }
        return null;
    }

    /**
     * 从el表达式中获取key、value，如{key:value}
     *
     * @param el        需要处理的字符串
     * @param startChar 开始字符串如{
     * @param endChar   结束字符串如}
     * @param equalChar 中间字符串如：
     * @return 返回处理后的map集合
     */
    public static Map<String, String> getGroupByStartEnd(String el, String startChar, String endChar, String equalChar) {
        Map<String, String> map = new LinkedHashMap<>();
        int index = el.indexOf(startChar);
        if (index == -1) {
            return map;
        }

        String last = el;
        while (index > -1 && index < el.length()) {
            int end;
            int first = last.indexOf(startChar);
            last = last.substring(first + startChar.length());
            index += (first + startChar.length());
            end = last.indexOf(equalChar);
            if (end == -1) {
                return map;
            }
            String key = last.substring(0, end);
            index += end;
            last = last.substring(end + equalChar.length());
            index += equalChar.length();
            end = last.indexOf(endChar);
            if (end == -1) {
                return map;
            }
            String value = last.substring(0, end);
            index += end;
            last = last.substring(end + endChar.length());
            map.put(key, value);
        }
        return map;
    }

    public static Map<String, String> getParamFromMapping(String url, String mapUrl) {
        final String left = "{";
        final String right = "}";
        final String equal = ":";
        final int minLength = 2;
        if (StringUtil.isEmpty(mapUrl) || mapUrl.length() <= minLength) {
            return null;
        }
        if (!mapUrl.contains(left) || !mapUrl.contains(right)) {
            return null;
        }
        Map<String, String> result = new LinkedHashMap<>();
        Map<String, String> map;
        if (mapUrl.contains(equal)) {
            map = getGroupByStartEnd(mapUrl, left, right, equal);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String value = getMatchedString(entry.getValue(), url, 0);
                if (isBlank(value)) {
                    return null;
                }
                result.put(entry.getKey(), value);
                int start = url.indexOf(value);
                url = url.substring(start + value.length());
            }
        } else {
            result.put(mapUrl.substring(1, mapUrl.length() - 1), url);
        }


        return result;
    }

//    public static void main(String[] args) {
//        getGroupByStartEnd("{service:[dD]ictionary[\\W_]{0,1}[dD]ata[\\W_]{0,1}[sS]ervice}", "{", "}", ":");
//    }

    /**
     * 获取字符串中匹配正则表达式的部分
     *
     * @param regex 正则表达式
     * @param text  正文
     * @param index 第几组
     * @return 匹配的字符串
     */
    public static String getMatchedString(String regex, String text, int index) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        int i = 0;
        while (matcher.find()) {
            if (i == index) {
                return matcher.group();
            }
            i++;
        }
        return null;
    }

    /**
     * 获取字符串中匹配正则表达式的部分
     *
     * @param regex 正则表达式
     * @param text  正文
     * @return 匹配的字符串
     */
    public static boolean haveMatchedString(String regex, String text) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为字符串
     */
    public static boolean isString(Object object) {
        return object instanceof String;
    }

    /**
     * 比较长短
     */
    public static boolean compareTo(String resource, String target) {
        return resource.length() > target.length();
    }


    /**
     * 字符数组转16进制字符串
     */
    public static String coverToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        if (ArrayUtil.isEmpty(bytes)) {
            return null;
        }

        final int length = 0xFF;
        final int two = 2;
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & length;
            String hv = Integer.toHexString(v);
            if (hv.length() < two) {
                result.append(0);
            }
            result.append(hv);
        }
        return result.toString().toUpperCase();
    }

    /**
     * 将字符串text中由openToken和closeToken组成的占位符依次替换为args数组中的值
     *
     * @param openToken  开始符号
     * @param closeToken 结束符号
     * @param text       转换原文
     * @param args       替换内容集合
     * @return
     */
    public static String parse(String openToken, String closeToken, String text, Map args) {
        if (args == null || args.size() <= 0) {
            return text;
        }

        if (text == null || text.isEmpty()) {
            return "";
        }
        char[] src = text.toCharArray();
        int offset = 0;
        // search open token
        int start = text.indexOf(openToken, offset);
        if (start == -1) {
            return text;
        }
        final StringBuilder builder = new StringBuilder();
        StringBuilder expression = null;
        while (start > -1) {
            if (start > 0 && src[start - 1] == '\\') {
                // this open token is escaped. remove the backslash and continue.
                builder.append(src, offset, start - offset - 1).append(openToken);
                offset = start + openToken.length();
            } else {
                // found open token. let's search close token.
                if (expression == null) {
                    expression = new StringBuilder();
                } else {
                    expression.setLength(0);
                }
                builder.append(src, offset, start - offset);
                offset = start + openToken.length();
                int end = text.indexOf(closeToken, offset);
                while (end > -1) {
                    if (end > offset && src[end - 1] == '\\') {
                        // this close token is escaped. remove the backslash and continue.
                        expression.append(src, offset, end - offset - 1).append(closeToken);
                        offset = end + closeToken.length();
                        end = text.indexOf(closeToken, offset);
                    } else {
                        expression.append(src, offset, end - offset);
                        offset = end + closeToken.length();
                        break;
                    }
                }
                if (end == -1) {
                    // close token was not found.
                    builder.append(src, start, src.length - start);
                    offset = src.length;
                } else {
                    String key = expression.toString();
                    String[] keyObj = key.split(":-");
                    Object o;
                    String value;
                    //判断是否有配置了默认值(:-)  by nhApis 2018.12.27
                    if (keyObj.length > 0) {
                        //配置了默认值,使用key获取当前环境变量中是否已经配置  by nhApis 2018.12.27
                        o = args.get(keyObj[0]);
                    } else {
                        o = args.get(key);
                    }

                    if (o == null) {
                        if (key.indexOf(":-") != -1) {
                            //获取不到使用默认值   by nhApis 2018.12.24
                            value = keyObj[1].trim();
                        } else {
                            //获取不到环境变量时,返回原表达式 by nhApis 2018.12.24
                            value = openToken + key + closeToken;
                        }
                    } else {
                        value = String.valueOf(o);
                    }
                    builder.append(value);
                    offset = end + closeToken.length();
                }
            }
            start = text.indexOf(openToken, offset);
        }
        if (offset < src.length) {
            builder.append(src, offset, src.length - offset);
        }
        return builder.toString();
    }

    /**
     * 字符串拼接
     *
     * @param values 字符串数组
     * @return 字符串
     */
    public static String concat(String... values) {
        StringBuilder target = new StringBuilder();
        for (String value : values) {
            target.append(value);
        }
        return target.toString();
    }

    public static String removeExtension(String str) {
        if (!isEmpty(str) && str.contains(Constant.RegularAbout.SPOT)) {
            String[] s = str.split("[.]");
            if (s.length > Constant.NumberAbout.ONE) {
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < s.length - 1; i++) {
                    result.append(s[i]);
                    if (i != s.length - Constant.NumberAbout.TWO) {
                        result.append(Constant.RegularAbout.SPOT);
                    }
                }
                return result.toString();
            }
        }
        return str;
    }

}
