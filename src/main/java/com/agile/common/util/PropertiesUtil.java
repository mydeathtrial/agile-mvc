package com.agile.common.util;

import com.agile.common.factory.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author mydeathtrial on 2017/3/11
 */
public final class PropertiesUtil extends com.agile.common.util.properties.PropertiesUtil {
    private static Environment environment;

    public static void setEnvironment(Environment environment) {
        PropertiesUtil.environment = environment;
    }

    public static String getProperty(String key) {
        String v = environment.getProperty(key);
        if (v == null) {
            return getProperties().getProperty(key);
        }
        return v;
    }

    /**
     * 获取工程格式化响应文
     *
     * @param key    句柄
     * @param params 占位参数
     * @return 值
     */
    public static String getMessage(String key, Object... params) {
        Locale locale = null;
        try {
            locale = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest().getLocale();
        } catch (Exception ignored) {
        }

        if (locale == null) {
            locale = Locale.CHINA;
        }
        String message = null;
        try {
            MessageSource messageSource = FactoryUtil.getBean(MessageSource.class);
            if (messageSource != null) {
                message = messageSource.getMessage(key, params, locale);
            }
        } catch (Exception ignored) {
        }

        if (message == null) {
            try {
                message = MessageFormat.format(getProperty(key), params);
            } catch (Exception ignored) {
            }
        }

        return message;
    }


    /**
     * 根据文件名取classpath目录下的json数据
     *
     * @param fileName 不带后缀文件名
     * @return JSONObject数据
     */
    public static JSON getJson(String fileName) {
        try {
            if (!fileName.endsWith(".json")) {
                fileName = String.format("%s.json", fileName);
            }
            String path = getFileClassPath(fileName);
            if (LoggerFactory.COMMON_LOG.isDebugEnabled()) {
                LoggerFactory.COMMON_LOG.debug("获取json文件路径:" + path);
            }
            InputStream stream = PropertiesUtil.class.getResourceAsStream(path);
            return streamToJson(stream);
        } catch (Exception e) {
            return null;
        }
    }

    private static JSON streamToJson(InputStream stream) {
        return (JSON) com.agile.common.util.json.JSONUtil.parse(inputStreamToString(stream));
    }

    /**
     * 输入流转字符串
     *
     * @param inputStream 输入流
     * @return 字符串
     */
    private static String inputStreamToString(InputStream inputStream) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder stringBuffer = new StringBuilder();
            String oneLine;
            while ((oneLine = bufferedReader.readLine()) != null) {
                stringBuffer.append(oneLine).append("\n");
            }
            return stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据文件名取classpath目录下的json数据
     *
     * @param fileName 文件绝对路径
     * @return JSONObject数据
     */
    public static JSON getJsonByAbsolutePath(String fileName) {
        try {
            return streamToJson(new FileInputStream(new File(fileName)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据文件名取classpath目录下的json数据
     *
     * @param fileNameSuffix 文件
     * @return JSONObject数据
     */
    public static List<JSONObject> getJsonBySuffix(String fileNameSuffix) {
        List<JSONObject> list = new ArrayList<>();
        fileNameSuffix = String.format("%s.json", fileNameSuffix);
        Enumeration<Object> keys = getProperties().keys();
        while (keys.hasMoreElements()) {
            String key = String.valueOf(keys.nextElement());
            if (key.endsWith(fileNameSuffix)) {
                JSONObject node = getProperties().get(key) instanceof JSONObject ? (JSONObject) getProperties().get(key) : null;
                if (node != null) {
                    list.add(node);
                }
            }
        }

        if (list.size() > 0) {
            return list;
        }
        return null;
    }

    /**
     * 取配置文件内容
     *
     * @param fileName 文件名字
     * @return 文件内容
     */
    public static String getFileContent(String fileName) {
        InputStream inputStream = getFileStream(fileName);
        return inputStreamToString(inputStream);
    }

    /**
     * 取配置文件流
     *
     * @param fileName 文件名字
     * @return 文件流
     */
    public static InputStream getFileStream(String fileName) {
        String path = getFileClassPath(fileName);
        if (path == null) {
            return null;
        }
        return PropertiesUtil.class.getResourceAsStream(path);
    }

    /**
     * 取配置文件编译路径
     *
     * @param fileName 文件名字
     * @return 编译路径
     */
    public static String getFileClassPath(String fileName) {
        final String regex = "[\\\\/]";
        Set<String> set = getFilePaths("/" + fileName);
        return set.stream().min(Comparator.comparingInt(a -> a.split(regex).length)).orElse(null);
    }

    /**
     * 根据文件名取classpath目录下的json数据
     *
     * @param fileName 不带后缀文件名
     * @return JSONObject数据
     */
    public static String getFilePath(String fileName) {
        String path = getFileClassPath(fileName);
        if (path == null) {
            return null;
        }
        URL absolutePath = PropertiesUtil.class.getResource(path);
        if (absolutePath == null) {
            return path;
        }
        try {
            return URLDecoder.decode(absolutePath.getPath(), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return path;
    }

    public static Set<String> getFilePaths(String fileName) {
        return getFileNames().stream().filter(name -> name.endsWith(fileName)).collect(Collectors.toSet());
    }

    /**
     * 从classpath目录下的所有json文件中，取出指定clazz类型对象数据集
     *
     * @param clazz 指定对象类型
     * @param <T>   泛型
     * @return List泛型集合
     */
    public static <T> List<T> getObjectFromJson(Class<T> clazz) {
        return getObjectFromJson(clazz, "");
    }

    /**
     * 从json数据中，取出指定clazz类型对象数据集
     *
     * @param clazz 指定对象类型
     * @param <T>   泛型
     * @return List泛型集合
     */
    public static <T> T getObjectFromJson(Class<T> clazz, JSONObject json) {
        try {
            return JSONUtil.toBean(clazz, json);
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 从classpath目录下，取指定文件后缀名位jsonFileName的json文件数据，获取其中的clazz对象集
     *
     * @param clazz          指定对象类型
     * @param fileSuffixName json文件后缀名
     * @param <T>            泛型
     * @return List泛型集合
     */
    public static <T> List<T> getObjectFromJson(Class<T> clazz, String fileSuffixName) {
        List<T> list = new ArrayList<>();
        Enumeration<Object> it = getProperties().keys();
        while (it.hasMoreElements()) {
            String key = String.valueOf(it.nextElement());
            if (key.endsWith(String.format("%s.json", fileSuffixName))) {
                final int length = 5;
                JSON json = getJson(key.substring(0, key.length() - length));
                try {
                    if (json instanceof JSONObject) {
                        T node = JSONUtil.toBean(clazz, (JSONObject) json);
                        if (node != null) {
                            list.add(node);
                        }
                    } else if (json instanceof JSONArray) {
                        list.addAll(jsonArrayToObjectList((JSONArray) json, clazz));
                    }

                } catch (Exception ignored) {
                }
            }
        }
        return list;
    }

    /**
     * jsonArray转List集合
     *
     * @param jsonArray 转换前
     * @param clazz     转换集合类型
     * @param <T>       泛型
     * @return clazz集合
     */
    public static <T> List<T> jsonArrayToObjectList(JSONArray jsonArray, Class<T> clazz) {
        List<T> list = new LinkedList<>();
        for (Object o : jsonArray) {
            if (o instanceof JSONObject) {
                T node = JSONUtil.toBean(clazz, (JSONObject) o);
                list.add(node);
            } else if (o instanceof JSONArray) {
                list.addAll(jsonArrayToObjectList((JSONArray) o, clazz));
            }
        }
        return list;
    }

}
