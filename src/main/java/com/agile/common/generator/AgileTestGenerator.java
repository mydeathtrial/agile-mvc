package com.agile.common.generator;

import com.agile.common.util.ArrayUtil;
import com.agile.common.util.FreemarkerUtil;
import com.agile.common.util.PropertiesUtil;
import com.agile.common.util.StringUtil;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by mydeathtrial on 2017/5/10.
 */
public class AgileTestGenerator {
    public static void main(String[] args) throws IOException, TemplateException {
        File directory = new File("./src/main/java/com/agile/mvc/service");
        if (directory.isDirectory()){
            String[] files = directory.list();
            for (int i = 0 ; i < files.length ; i++){
                //分割获取类名
                String serviceName = files[i].replaceAll(".java$","");
                String entityName = null;
                Map<String, Object> data = new HashMap<>();
                List<Map<String, String>> methodList = new ArrayList<>();
                List<String> propertyList = new ArrayList<>();
                //循环处理各个类
                try {
                    Class<?> clazz = Class.forName("com.agile.mvc.service." + serviceName);
                    Method[] methods = clazz.getDeclaredMethods();
                    Method[] methods1 = clazz.getMethods();
                    for (int j = 0 ; j < methods.length;j++){
                        Method method = methods[j];
                        if(!ArrayUtil.contains(methods1,method))continue;
                        String methodName = method.getName();
                        Map<String,String> map = new HashMap<>();
                        map.put("methodName",methodName);
                        map.put("url","/api/"+serviceName+"/"+methodName);
                        methodList.add(map);
                    }

                    String servicePrefix = PropertiesUtil.getProperty("agile.generator.service_prefix");
                    String serviceSuffix = PropertiesUtil.getProperty("agile.generator.service_suffix");
                    String entityPrefix = PropertiesUtil.getProperty("agile.generator.entity_prefix");
                    String entitySuffix = PropertiesUtil.getProperty("agile.generator.entity_suffix");
                    if (serviceName.startsWith(servicePrefix)){
                        entityName = serviceName.replaceFirst(servicePrefix,"");
                    }
                    if (serviceName.endsWith(serviceSuffix)){
                        entityName = entityName.replaceAll(serviceSuffix+"$","");
                    }
                    if(!StringUtil.isEmpty(entityPrefix)){
                        entityName = entityPrefix + entityName;
                    }
                    if(!StringUtil.isEmpty(entitySuffix)){
                        entityName = entityName + entitySuffix;
                    }
                    Class<?> entity = Class.forName("com.agile.mvc.entity." + entityName);
                    Method[] entityMethods = entity.getMethods();
                    for(int j = 0 ; j < entityMethods.length ; j++){
                        String entityMethodName = entityMethods[j].getName();
                        if (entityMethodName.startsWith("set")){
                            propertyList.add(StringUtil.toLowerName(entityMethodName));
                        }
                    }
                } catch (ClassNotFoundException e) {
                    System.out.println("未找到文件：com.agile.mvc.service."+serviceName);
                    continue;
                }
                //数据装填
                data.put("className",serviceName);
                data.put("methodList",methodList);
                data.put("propertyList",propertyList);

                //生成器
                String testFileName = serviceName + "Test.java";
                FreemarkerUtil.generatorProxy("Test.ftl","./test/main/java/com/agile/mvc/service/",testFileName,data,false);
            }
        }
        System.exit(0);
    }
}
