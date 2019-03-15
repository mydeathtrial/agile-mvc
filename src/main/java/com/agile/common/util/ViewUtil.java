package com.agile.common.util;

import com.agile.common.base.AbstractResponseFormat;
import com.agile.common.base.ApiInfo;
import com.agile.common.base.Constant;
import com.agile.common.base.Head;
import com.agile.common.base.RequestWrapper;
import com.agile.common.mybatis.Page;
import com.agile.common.view.ForwardView;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.util.UriUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author 佟盟 on 2018/8/22
 * @author 佟盟
 */
@Component
public class ViewUtil {
    private static ViewUtil viewUtil;
    @Nullable
    private List<ViewResolver> viewResolvers;
    @Nullable
    private Locale locale;
    @Nullable
    private RequestToViewNameTranslator viewNameTranslator;

    /**
     * 根据servlet请求、认证信息、目标服务名、目标方法名处理入参
     */
    public static Map<String, Object> handleInParam(HttpServletRequest currentRequest) {
        final int length = 16;
        Map<String, Object> inParam = new HashMap<>(length);

        Map<String, String[]> parameterMap = currentRequest.getParameterMap();
        if (parameterMap.size() > 0) {
            for (Map.Entry<String, String[]> map : parameterMap.entrySet()) {
                String[] v = map.getValue();
                if (v.length == 1) {
                    inParam.put(map.getKey(), v[0]);
                } else {
                    inParam.put(map.getKey(), v);
                }
            }
        }

        if (currentRequest instanceof RequestWrapper) {
            Map<String, String[]> forwardMap = ((RequestWrapper) currentRequest).getForwardParameterMap();
            for (Map.Entry<String, String[]> map : forwardMap.entrySet()) {
                inParam.put(map.getKey(), map.getValue());
            }
        }

        //判断是否存在文件上传
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(currentRequest.getSession().getServletContext());
        if (multipartResolver.isMultipart(currentRequest)) {
            Map<String, Object> formData = FileUtil.getFileFormRequest(currentRequest);
            for (String key : formData.keySet()) {
                if (inParam.containsKey(key)) {
                    continue;
                }
                inParam.put(key, formData.get(key));
            }
        } else {
            Map<String, Object> bodyParam = ServletUtil.getBody(currentRequest);
            if (bodyParam != null) {
                inParam.putAll(bodyParam);
            }
        }

        Enumeration<String> attributeNames = currentRequest.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String key = attributeNames.nextElement();
            String prefix = ForwardView.getPrefix();
            if (key.startsWith(prefix)) {
                inParam.put(key.replace(prefix, ""), currentRequest.getAttribute(key));
            }
        }

        //处理Mapping参数
        String uri = currentRequest.getRequestURI();
        String extension = UriUtils.extractFileExtension(uri);
        if ("json".equals(extension) || "xml".equals(extension) || "plain".equals(extension)) {
            uri = uri.replaceAll("." + extension, "");
        }
        ApiInfo info = ApiUtil.getApiCache(currentRequest);

        //处理路径入参
        if (info != null) {
            Set<RequestMappingInfo> requestMappingInfos = info.getRequestMappingInfos();
            if (requestMappingInfos != null) {
                Set<String> mappingCache = new HashSet<>();
                for (RequestMappingInfo requestMappingInfo : requestMappingInfos) {
                    mappingCache.addAll(requestMappingInfo.getPatternsCondition().getPatterns());
                }
                for (String mapping : mappingCache) {
                    String[] uris = StringUtil.split(uri, Constant.RegularAbout.SLASH);
                    String[] targetParams = StringUtil.split(mapping, Constant.RegularAbout.SLASH);
                    if (uris.length == targetParams.length) {
                        for (int i = 0; i < targetParams.length; i++) {
                            String targetParam = targetParams[i];
                            String value = uris[i];
                            Map<String, String> params = StringUtil.getParamFromMapping(value, targetParam);
                            if (params != null && params.size() > 0) {
                                inParam.putAll(params);
                            }
                        }
                    }
                }
            }
        }
        //将处理过的所有请求参数传入调用服务对象
        return inParam;
    }

    public static <T> T getInParam(Map<String, Object> map, String key, Class<T> clazz) {
        T result = null;

        if (map.containsKey(Constant.ResponseAbout.BODY)) {
            Object jsonNode = map.get(Constant.ResponseAbout.BODY);
            if (jsonNode != null) {
                JsonNode json = ((JsonNode) jsonNode);
                JsonNode value = json.get(key);
                if (value != null && !value.isNull()) {
                    if (clazz == String.class && value.isTextual()) {
                        result = (T) value.textValue();
                    } else if (clazz == Integer.class && value.isInt()) {
                        result = (T) Integer.valueOf(value.intValue());
                    } else if (clazz == Long.class && value.isLong()) {
                        result = (T) Long.valueOf(value.longValue());
                    } else if (clazz == BigInteger.class && value.isBigInteger()) {
                        result = (T) value.bigIntegerValue();
                    } else if (clazz == byte[].class && value.isBinary()) {
                        try {
                            result = (T) value.binaryValue();
                        } catch (IOException ignored) {
                        }
                    } else if (clazz == BigDecimal.class && value.isBigDecimal()) {
                        result = (T) BigDecimal.valueOf(value.asLong());
                    } else if (clazz == boolean.class && value.isBoolean()) {
                        result = (T) Boolean.valueOf(value.booleanValue());
                    } else if (clazz == double.class && value.isDouble()) {
                        result = (T) Double.valueOf(value.doubleValue());
                    } else if (clazz == float.class && value.isFloat()) {
                        result = (T) Float.valueOf(value.floatValue());
                    } else if (clazz == short.class && value.isShort()) {
                        result = (T) Short.valueOf(value.shortValue());
                    } else if (clazz == Number.class && value.isNumber()) {
                        result = (T) value.numberValue();
                    } else if (clazz == Date.class && value.isLong()) {
                        result = (T) new Date(value.longValue());
                    } else if (clazz == Date.class && value.isTextual()) {
                        result = ObjectUtil.cast(clazz, value.asText());
                    } else {
                        result = JSONUtil.toBean(clazz, value.toString());
                    }
                }
            }
        }
        if (result == null && map.containsKey(key)) {
            Object v = map.get(key);
            result = ObjectUtil.cast(clazz, v);
        }

        return result;
    }

    /**
     * 格式化响应报文
     *
     * @param head   头信息
     * @param result 体信息
     * @return 格式化后的ModelAndView
     */
    public static ModelAndView getResponseFormatData(Head head, Object result) {
        ModelAndView modelAndView = new ModelAndView();
        AbstractResponseFormat abstractResponseFormat = FactoryUtil.getBean(AbstractResponseFormat.class);
        if (abstractResponseFormat != null) {
            modelAndView = abstractResponseFormat.buildResponse(head, result);
        } else {
            if (head != null) {
                modelAndView.addObject(Constant.ResponseAbout.HEAD, head);
            }
            if (result != null && Map.class.isAssignableFrom(result.getClass())) {
                modelAndView.addAllObjects((Map<String, ?>) result);
            } else {
                modelAndView.addObject(Constant.ResponseAbout.RESULT, result);
            }
        }
        return modelAndView;
    }

    public static void render(Head head, Object result, HttpServletRequest request, HttpServletResponse response) throws Exception {
        render(getResponseFormatData(head, result), request, response);
    }

    public static void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request, response));

        ApplicationContext context = FactoryUtil.getApplicationContext();

        if (viewUtil.locale == null) {
            initLocaleResolver(context, request);
        }
        if (viewUtil.viewResolvers == null) {
            initViewResolvers(context);
        }
        if (viewUtil.viewNameTranslator == null) {
            initRequestToViewNameTranslator(context);
        }

        response.setLocale(viewUtil.locale);
        String viewName = mv.getViewName();
        if (viewName == null) {
            viewName = getDefaultViewName(request);
            mv.setViewName(viewName);
        }
        View view = resolveViewName(viewName, mv.getModel(), viewUtil.locale, request);

        try {
            if (mv.getStatus() != null) {
                response.setStatus(mv.getStatus().value());
            }

            view.render(mv.getModel(), request, response);
        } catch (Exception var8) {
            throw var8;
        }
    }

    @Nullable
    private static View resolveViewName(String viewName, @Nullable Map<String, Object> model, Locale locale, HttpServletRequest request) throws Exception {

        if (viewUtil.viewResolvers != null) {
            Iterator var5 = viewUtil.viewResolvers.iterator();

            while (var5.hasNext()) {
                ViewResolver viewResolver = (ViewResolver) var5.next();
                View view = viewResolver.resolveViewName(viewName, locale);
                if (view != null) {
                    return view;
                }
            }
        }

        return null;
    }

    private static void initLocaleResolver(ApplicationContext context, HttpServletRequest request) throws IOException {
        List<LocaleResolver> localeResolvers = getDefaultStrategies(context, LocaleResolver.class);
        viewUtil.locale = localeResolvers == null ? request.getLocale() : (localeResolvers.get(0)).resolveLocale(request);
    }

    private static void initViewResolvers(ApplicationContext context) throws IOException {
        viewUtil.viewResolvers = null;
        Map<String, ViewResolver> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, ViewResolver.class, true, false);
        if (!matchingBeans.isEmpty()) {
            viewUtil.viewResolvers = new LinkedList<>(matchingBeans.values());
            AnnotationAwareOrderComparator.sort(viewUtil.viewResolvers);
        }

        if (viewUtil.viewResolvers == null) {
            viewUtil.viewResolvers = getDefaultStrategies(context, ViewResolver.class);
        }
    }

    private static void initRequestToViewNameTranslator(ApplicationContext context) throws IOException {
        try {
            viewUtil.viewNameTranslator = context.getBean("viewNameTranslator", RequestToViewNameTranslator.class);
        } catch (NoSuchBeanDefinitionException var3) {
            viewUtil.viewNameTranslator = getDefaultStrategies(context, RequestToViewNameTranslator.class).get(0);
        }

    }

    private static <T> List<T> getDefaultStrategies(ApplicationContext context, Class<T> strategyInterface) throws IOException {
        String key = strategyInterface.getName();
        ClassPathResource resource = new ClassPathResource("DispatcherServlet.properties", DispatcherServlet.class);
        Properties defaultStrategies = PropertiesLoaderUtils.loadProperties(resource);
        String value = defaultStrategies.getProperty(key);
        if (value == null) {
            return new LinkedList<>();
        } else {

            String[] classNames = StringUtils.commaDelimitedListToStringArray(value);
            List<T> strategies = new ArrayList<>(classNames.length);
            String[] var7 = classNames;
            int var8 = classNames.length;

            for (int var9 = 0; var9 < var8; ++var9) {
                String className = var7[var9];

                try {
                    Class<?> clazz = ClassUtils.forName(className, DispatcherServlet.class.getClassLoader());
                    Object strategy = context.getAutowireCapableBeanFactory().createBean(clazz);
                    strategies.add((T) strategy);
                } catch (ClassNotFoundException var13) {
                    throw new BeanInitializationException("Could not find DispatcherServlet's default strategy class [" + className + "] for interface [" + key + "]", var13);
                } catch (LinkageError var14) {
                    throw new BeanInitializationException("Unresolvable class definition for DispatcherServlet's default strategy class [" + className + "] for interface [" + key + "]", var14);
                }
            }

            return strategies;

        }
    }

    @Nullable
    private static String getDefaultViewName(HttpServletRequest request) throws Exception {
        return viewUtil.viewNameTranslator != null ? viewUtil.viewNameTranslator.getViewName(request) : null;
    }

    public static Model modelProcessing(Map<String, Object> model) {
        Model m = new Model();
        m.setModel(model);
        for (Map.Entry<String, Object> entry : model.entrySet()) {
            Object value = entry.getValue();
            if (FileUtil.isFile(value)) {
                m.addFile(value);
            } else if (value instanceof Page) {
                m.addPage(entry.getKey());
                m.put(entry.getKey(), ((Page) value).getPage());
            } else if (value != null && Map.class.isAssignableFrom(value.getClass())) {
                Model inm = modelProcessing((Map<String, Object>) value);
                m.addFiles(inm.getFiles());
                m.addPages(inm.getPages());
                m.put(entry.getKey(), inm);
            } else {
                m.put(entry.getKey(), entry.getValue());
            }
        }
        return m;
    }

    @PostConstruct
    void init() {
        viewUtil = this;
    }

    /**
     * 返回数据经过加公后产生的模型辅助类
     */
    @Getter
    @Setter
    public static class Model extends LinkedHashMap<String, Object> {
        List<Object> files = new ArrayList<>();
        List<String> pages = new ArrayList<>();
        Map<String, Object> model;

        void addFile(Object file) {
            this.files.add(file);
        }

        void addFiles(List<Object> files) {
            this.files.addAll(files);
        }

        void addPage(String page) {
            this.pages.add(page);
        }

        void addPages(List<String> pages) {
            this.pages.addAll(pages);
        }
    }


}
