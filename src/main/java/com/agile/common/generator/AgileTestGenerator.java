package com.agile.common.generator;

import com.agile.common.util.ArrayUtil;
import com.agile.common.util.FreemarkerUtil;
import com.agile.common.util.PropertiesUtil;
import com.agile.common.util.StringUtil;
import freemarker.template.TemplateException;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mydeathtrial on 2017/5/10.
 */
public class AgileTestGenerator {
    public static void main(String[] args) throws IOException, TemplateException {
        creatTest("./src/main/java/com/agile/mvc/service");
    }

    private static void creatTest(String path) throws IOException, TemplateException {
        File directory = new File(path);
        String packageName = AgileGenerator.getPackPath(path);
        if (directory.isDirectory()) {
            String[] files = directory.list();
            if (files == null) {
                return;
            }
            for (String fileName : files) {
                if (!"java".equals(StringUtils.getFilenameExtension(fileName))) {
                    creatTest(path + "/" + fileName);
                } else {
                    //分割获取类名
                    String serviceName = fileName.replaceAll(".java$", "");
                    String entityName = null;
                    Map<String, Object> data = new HashMap<>();
                    List<Map<String, String>> methodList = new ArrayList<>();
                    List<String> propertyList = new ArrayList<>();
                    //循环处理各个类
                    try {
                        Class<?> clazz = Class.forName(packageName + "." + serviceName);
                        Method[] methods = clazz.getDeclaredMethods();
                        Method[] methods1 = clazz.getMethods();
                        for (Method method : methods) {
                            if (!ArrayUtil.contains(methods1, method)) {
                                continue;
                            }
                            String methodName = method.getName();
                            Map<String, String> map = new HashMap<>();
                            map.put("methodName", methodName);
                            map.put("url", "/api/" + serviceName + "/" + methodName);
                            methodList.add(map);
                        }

                        String servicePrefix = PropertiesUtil.getProperty("agile.generator.service_prefix");
                        String serviceSuffix = PropertiesUtil.getProperty("agile.generator.service_suffix");
                        String entityPrefix = PropertiesUtil.getProperty("agile.generator.entity_prefix");
                        String entitySuffix = PropertiesUtil.getProperty("agile.generator.entity_suffix");
                        if (serviceName.startsWith(servicePrefix)) {
                            entityName = serviceName.replaceFirst(servicePrefix, "");
                        }
                        if (serviceName.endsWith(serviceSuffix)) {
                            entityName = entityName.replaceAll(serviceSuffix + "$", "");
                        }
                        if (!StringUtil.isEmpty(entityPrefix)) {
                            entityName = entityPrefix + entityName;
                        }
                        if (!StringUtil.isEmpty(entitySuffix)) {
                            entityName = entityName + entitySuffix;
                        }
                        try {
                            Class<?> entity = Class.forName("com.agile.mvc.entity." + entityName);
                            Method[] entityMethods = entity.getMethods();
                            for (Method entityMethod : entityMethods) {
                                String entityMethodName = entityMethod.getName();
                                if (entityMethodName.startsWith("set")) {
                                    propertyList.add(StringUtil.toLowerName(entityMethodName));
                                }
                            }
                        } catch (Exception ignored) {
                        }
                    } catch (ClassNotFoundException ignored) {
                    }
                    //数据装填
                    data.put("className", serviceName);
                    data.put("methodList", methodList);
                    data.put("propertyList", propertyList);

                    //生成器
                    String testFileName = serviceName + "Test.java";
                    FreemarkerUtil.generatorProxy("Test.ftl", "./test/main/java/com/agile/mvc/service/", testFileName, data, false);
                }
            }
        }
        System.exit(0);
    }
}
