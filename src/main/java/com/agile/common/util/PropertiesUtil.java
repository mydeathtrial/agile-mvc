package com.agile.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

/**
 * @author mydeathtrial on 2017/3/11
 */
public final class PropertiesUtil extends PropertiesLoaderUtils {

    private static final String CLASS_PATH = "/com/agile/conf/";
    private static final String JSON_FILE_ERROR = "未成功解析的json文件";
    private static Properties properties = new Properties();

    public static Properties getProperties() {
        return properties;
    }

    static {
        mergeEnv();
        readDir("/");
        readDir(CLASS_PATH);
        mergeOrder("application-agile");
        mergeOrder("application-datasource");
        mergeOrder("application-jpa");
        mergeOrder("application-mvc");
        mergeOrder("application-kafka ");
        mergeOrder("application");
        mergeOrder("bootstrap");
        mergeOrder("agile-reset");
        coverEL();
    }

    private static void readDir(String classPathResource) {
        try {
            File dir = new File(PropertiesUtil.class.getResource(classPathResource).toURI().getPath());
            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles();
                if (files == null) {
                    return;
                }
                for (File file : files) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        if (fileName.endsWith("properties")) {
                            mergeProperties(file);
                        } else if (fileName.endsWith("yml")) {
                            mergeYml(classPathResource + file.getName());
                        } else if (fileName.endsWith("json")) {
                            mergeJson(file);
                        } else if (fileName.endsWith("py") || fileName.endsWith("jrxml") || fileName.endsWith("datx")) {
                            mergeFilePath(file);
                        }
                    } else if (file.isDirectory()) {
                        readDir(classPathResource + file.getName() + "/");
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static void coverEL() {
        if (properties == null) {
            return;
        }
        for (Map.Entry<Object, Object> v : properties.entrySet()) {
            if (StringUtil.isString(v.getValue())) {
                properties.setProperty(String.valueOf(v.getKey()), StringUtil.parsingPlaceholder("${env.", "}", String.valueOf(v.getValue()), PropertiesUtil.properties));
                properties.setProperty(String.valueOf(v.getKey()), StringUtil.parsingPlaceholder("${", "}", String.valueOf(v.getValue()), PropertiesUtil.properties));
            }
        }
    }

    private static void mergeEnv() {
        merge(System.getProperties());
        merge(System.getenv());
    }

    private static void mergeOrder(String fileName) {
        merge(CLASS_PATH + fileName + ".properties");
        merge(CLASS_PATH + fileName + ".yml");
        merge(fileName + ".properties");
        merge(fileName + ".yml");
    }

    public static void merge(String fileName) {
        try {

            if (ObjectUtil.isEmpty(fileName)) {
                return;
            }
            if (fileName.endsWith("properties")) {
                merge(PropertiesUtil.class.getResourceAsStream(fileName));
            } else if (fileName.endsWith("yml")) {
                mergeYml(fileName);
            } else if (fileName.endsWith("json")) {
                mergeJson(fileName, PropertiesUtil.class.getResourceAsStream(fileName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void merge(InputStream in) {
        try {
            if (ObjectUtil.isEmpty(in)) {
                return;
            }
            properties.load(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void merge(Properties p) {
        for (Map.Entry<Object, Object> entry : p.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            properties.setProperty(key.toString(), value.toString());
        }
    }

    public static void merge(Map<String, String> p) {
        for (Map.Entry<String, String> entry : p.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            properties.setProperty(key.toString(), value.toString());
        }
    }

    public static void mergeProperties(File file) {
        try {
            if (ObjectUtil.isEmpty(file) || !file.getName().endsWith(".properties")) {
                return;
            }
            merge(new BufferedInputStream(new FileInputStream(file)));
        } catch (Exception ignored) {
        }
    }

    public static void mergeYml(String classPathResource) {
        try {
            if (classPathResource == null || !classPathResource.endsWith(".yml")) {
                return;
            }
            YamlPropertiesFactoryBean yml = new YamlPropertiesFactoryBean();
            ClassPathResource resource = new ClassPathResource(classPathResource);
            if (!resource.exists()) {
                return;
            }
            yml.setResources(resource);
            merge(Objects.requireNonNull(yml.getObject()));
        } catch (Exception ignored) {
        }
    }

    public static void mergeJson(File file) {
        try {
            if (ObjectUtil.isEmpty(file) || !file.getName().endsWith(".json")) {
                return;
            }
            String data = FileUtils.readFileToString(file, "UTF-8");
            JSON json = JSONUtil.toJSON(data);
            properties.put(file.getName(), json == null ? JSON_FILE_ERROR : json);
        } catch (Exception ignored) {
        }
    }

    private static void mergeJson(String fileName, InputStream in) {
        try {
            if (ObjectUtil.isEmpty(fileName) || !fileName.endsWith(".json")) {
                return;
            }
            String data = IOUtils.toString(in, "UTF-8");
            properties.put(fileName, JSONUtil.toJSON(data));
        } catch (Exception ignored) {
        }
    }


    private static void mergeFilePath(File file) {
        try {
            properties.put(file.getName(), file.getPath());
        } catch (Exception ignored) {
        }
    }

    /**
     * 获取工程配置信息
     *
     * @param key 句柄
     * @return 值
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
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
                message = MessageFormat.format(properties.getProperty(key), params);
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
        String key = String.format("%s.json", fileName);
        Object value = properties.get(key);
        if (JSON.class.isAssignableFrom(value.getClass())) {
            return (JSON) properties.get(key);
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
        Enumeration<Object> keys = properties.keys();
        while (keys.hasMoreElements()) {
            String key = String.valueOf(keys.nextElement());
            if (key.endsWith(fileNameSuffix)) {
                JSONObject node = properties.get(key) instanceof JSONObject ? (JSONObject) properties.get(key) : null;
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
     * 根据文件名取classpath目录下的json数据
     *
     * @param fileName 不带后缀文件名
     * @return JSONObject数据
     */
    public static String getFilePath(String fileName) {
        return getProperty(fileName);
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
        Enumeration<Object> it = properties.keys();
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

    /**
     * 获取工程配置信息
     *
     * @param key 句柄
     * @return 值
     */
    public static String getProperty(String key, String defaultValue) {
        Object value = getProperty(key);
        if (!ObjectUtil.isEmpty(value)) {
            return value.toString();
        }
        return defaultValue;
    }

    public static Properties getPropertyByPrefix(String prefix) {
        Properties r = new Properties();
        Set<String> propertyNames = properties.stringPropertyNames();
        for (String name : propertyNames) {
            if (name.startsWith(prefix)) {
                r.put(name, properties.get(name));
            }
        }
        return r;

    }

    public static <T> T getProperty(String var1, Class<T> var2) {
        return ObjectUtil.cast(var2, getProperty(var1));
    }

    public static <T> T getProperty(String var1, Class<T> var2, String defaultValue) {
        return ObjectUtil.cast(var2, getProperty(var1, defaultValue));
    }
}
