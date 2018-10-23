package com.agile.common.util;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.*;

/**
 * Created by mydeathtrial on 2017/3/11
 */
public class PropertiesUtil {

    public static Properties properties = new Properties();

    private PropertiesUtil(File file) {
        try {
            properties = new Properties();
            InputStream in = new BufferedInputStream(new FileInputStream(file));
            properties.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PropertiesUtil(InputStream in){
        try {
            properties = new Properties();
            properties.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        merge("agile.properties");
        merge("agile.yml");
        merge("application.properties");
        merge("application.yml");
        merge("bootstrap.properties");
        merge("bootstrap.yml");
        merge("agile-reset.properties");
        merge("agile-reset.yml");
    }

    public static void merge(String filename){
        try {
            if(filename.endsWith("properties")){
                if(ObjectUtil.isEmpty(filename))return;
                InputStream in = PropertiesUtil.class.getResourceAsStream("/com/agile/conf/"+filename);
                if(in==null){
                    in = PropertiesUtil.class.getResourceAsStream("/"+filename);
                }
                merge(in);
            }else if(filename.endsWith("yml")){
                YamlPropertiesFactoryBean yml = new YamlPropertiesFactoryBean();
                ClassPathResource resource = new ClassPathResource("/com/agile/conf/" + filename);
                if(!resource.exists()){
                    resource = new ClassPathResource("/" + filename);
                }
                if(!resource.exists())return;
                yml.setResources(resource);
                merge(yml.getObject());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void merge(InputStream in){
        try {
            if(ObjectUtil.isEmpty(in))return;
            properties.load(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void merge(Properties p){
        for (Map.Entry<Object, Object> entry: p.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            properties.setProperty(key.toString(), value.toString());
        }
    }

    public static void merge(File file){
        InputStream in;
        try {
            if(ObjectUtil.isEmpty(file))return;
            in = new BufferedInputStream(new FileInputStream(file));
            merge(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Properties getPropertys(){
        return properties;
    }
    /**
     * 获取工程配置信息
     * @param key 句柄
     * @return 值
     */
    public static String getProperty(String key){
        return properties.getProperty(key);
    }

    /**
     * 获取工程配置信息
     * @param key 句柄
     * @return 值
     */
    public static String getProperty(String key,String defaultValue){
        if(!properties.containsKey(key))return defaultValue;
        return getProperty(key);
    }

    public static Properties getPropertyByPrefix(String prefix){
        Properties r = new Properties();
        Set<String> propertyNames = properties.stringPropertyNames();
        for (String name :propertyNames){
            if(name.startsWith(prefix)){
                r.put(name,properties.get(name));
            }
        }
        return r;

    }

    public static <T> T getProperty(String var1, Class<T> var2){
        return ObjectUtil.cast(var2,getProperty(var1));
    }

    public static <T> T getProperty(String var1, Class<T> var2,String defaultValue){
        return ObjectUtil.cast(var2,getProperty(var1,defaultValue));
    }

}
