package com.agile.common.generator;

import com.agile.common.annotation.Mapping;
import com.agile.common.base.ApiInfo;
import com.agile.common.generator.model.ShowDocModel;
import com.agile.common.properties.GeneratorProperties;
import com.agile.common.util.ApiUtil;
import com.agile.common.util.FactoryUtil;
import com.agile.common.util.FreemarkerUtil;
import freemarker.template.TemplateException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author mydeathtrial on 2017/5/10.
 */
public class AgileTestGenerator {
    private static GeneratorProperties generator;

    public static void main(String[] args) throws IOException, TemplateException, InstantiationException, IllegalAccessException {
        AgileGenerator.init();
        generator = FactoryUtil.getBean(GeneratorProperties.class);
        creatTest("./src/main/java/com/agile/mvc/service");
    }

    private static void creatTest(String path) throws IOException, TemplateException, IllegalAccessException, InstantiationException {
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
                        ApiUtil.registerApiMapping(serviceName, clazz.newInstance());

//                        Method[] methods = clazz.getDeclaredMethods();
//                        Method[] methods1 = clazz.getMethods();
//                        for (Method method : methods) {
//
//                            if (!ArrayUtil.contains(methods1, method)) {
//                                continue;
//                            }
//                            String methodName = method.getName();
//                            Map<String, String> map = new HashMap<>();
//                            map.put("methodName", methodName);
//                            map.put("url", "/api/" + serviceName + "/" + methodName);
//                            methodList.add(map);
//                        }
//
//
//                        if (serviceName.startsWith(generator.getServicePrefix())) {
//                            entityName = serviceName.replaceFirst(generator.getServicePrefix(), "");
//                        }
//                        if (serviceName.endsWith(generator.getServiceSuffix())) {
//                            assert entityName != null;
//                            entityName = entityName.replaceAll(generator.getServiceSuffix() + "$", "");
//                        }
//                        if (!StringUtil.isEmpty(generator.getEntityPrefix())) {
//                            entityName = generator.getEntityPrefix() + entityName;
//                        }
//                        if (!StringUtil.isEmpty(generator.getEntitySuffix())) {
//                            entityName = entityName + generator.getEntitySuffix();
//                        }
//                        try {
//                            Class<?> entity = Class.forName("com.agile.mvc.entity." + entityName);
//                            Method[] entityMethods = entity.getMethods();
//                            for (Method entityMethod : entityMethods) {
//                                String entityMethodName = entityMethod.getName();
//                                if (entityMethodName.startsWith("set")) {
//                                    propertyList.add(StringUtil.toLowerName(entityMethodName));
//                                }
//                            }
//                        } catch (Exception ignored) {
//                        }
                    } catch (ClassNotFoundException ignored) {
                    }
                    //数据装填
//                    data.put("className", serviceName);
//                    data.put("methodList", methodList);
//                    data.put("propertyList", propertyList);
//
//                    //生成器
//                    String testFileName = serviceName + "Test.java";
//                    FreemarkerUtil.generatorProxy("Test.ftl", "./test/main/java/com/agile/mvc/service/", testFileName, data, false);
                }
            }
            for (ApiInfo apiInfo : ApiUtil.getApiInfoCache()) {
                ShowDocModel node = parsingMethod(apiInfo);
                if (node == null) {
                    continue;
                }
                String testFileName = apiInfo.getBeanName() + "_" + apiInfo.getMethod().getName() + ".md";
                FreemarkerUtil.generatorProxy("Showdoc.ftl", "./showdoc/", testFileName, node, false);
            }
        }
        System.exit(0);
    }

    private static ShowDocModel parsingMethod(ApiInfo apiInfo) {
        Class<?> clazz = apiInfo.getBean().getClass();
        Method method = apiInfo.getMethod();

        ShowDocModel.ShowDocModelBuilder builder = ShowDocModel.builder();
        Api api = clazz.getAnnotation(Api.class);
        if (api != null) {
            builder.module(api.value());
        }

        Mapping classMapping = clazz.getDeclaredAnnotation(Mapping.class);
        Set<String> classMappingUrl = new HashSet<>();

        if (classMapping != null) {
            classMappingUrl.addAll(Arrays.asList(classMapping.value()));
        }

        ApiOperation apiOperation = method.getDeclaredAnnotation(ApiOperation.class);
        if (apiOperation == null) {
            return null;
        }
        builder.desc(apiOperation.value());
        builder.method(apiOperation.httpMethod());

        builder.url(apiInfo.getRequestMappingInfo().getPatternsCondition().getPatterns());

        ApiImplicitParams apiImplicitParams = method.getDeclaredAnnotation(ApiImplicitParams.class);
        if (apiImplicitParams != null && apiImplicitParams.value().length > 0) {
            Set<ShowDocModel.Param> paramSet = new HashSet<>();
            for (ApiImplicitParam apiImplicitParam : apiImplicitParams.value()) {
                ShowDocModel.Param param = new ShowDocModel.Param();
                param.setDesc(apiImplicitParam.value());
                param.setName(apiImplicitParam.name());
                param.setNullable(apiImplicitParam.required());
                param.setType(apiImplicitParam.dataType());
                paramSet.add(param);
            }
            builder.requestParams(paramSet);
        }
        return builder.build();
    }
}
