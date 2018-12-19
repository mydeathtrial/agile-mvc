package com.agile.common.util;

import com.agile.common.mybatis.Page;
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
import org.springframework.web.servlet.*;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Created by 佟盟 on 2018/8/22
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
    
    @PostConstruct
    void init(){
        viewUtil = this;
    }

    public static void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request, response));

        ApplicationContext context = FactoryUtil.getApplicationContext();

        if(viewUtil.locale == null){
            initLocaleResolver(context,request);
        }
        if(viewUtil.viewResolvers == null){
            initViewResolvers(context);
        }
        if(viewUtil.viewNameTranslator == null){
            initRequestToViewNameTranslator(context);
        }

        response.setLocale(viewUtil.locale);
        String viewName = mv.getViewName();
        if(viewName==null){
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

            while(var5.hasNext()) {
                ViewResolver viewResolver = (ViewResolver)var5.next();
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
        viewUtil.locale = localeResolvers == null? request.getLocale():(localeResolvers.get(0)).resolveLocale(request);
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
            return new LinkedList();
        }else {

            String[] classNames = StringUtils.commaDelimitedListToStringArray(value);
            List<T> strategies = new ArrayList(classNames.length);
            String[] var7 = classNames;
            int var8 = classNames.length;

            for(int var9 = 0; var9 < var8; ++var9) {
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

    public static Model modelProcessing(Map<String,Object> model){
        Model m = new Model();
        m.setModel(model);
        for (Map.Entry<String,Object> entry:model.entrySet()) {
            Object value = entry.getValue();
            if(value == null) {
                continue;
            }
            if(FileUtil.isFile(value)){
                m.addFile(value);
            }else if(value instanceof Page){
                m.addPage(entry.getKey());
                m.put(entry.getKey(),((Page) value).getPage());
            }else if(Map.class.isAssignableFrom(value.getClass())){
                Model inm = modelProcessing((Map<String, Object>) value);
                m.addFiles(inm.getFiles());
                m.addPages(inm.getPages());
                m.put(entry.getKey(),inm);
            }else{
                m.put(entry.getKey(),entry.getValue());
            }
        }
        return m;
    }

    public static class Model extends LinkedHashMap<String,Object>{
        List<Object> files = new ArrayList<>();
        List<String> pages = new ArrayList<>();
        Map<String,Object> model;

        public List<Object> getFiles() {
            return files;
        }

        public void addFile(Object file) {
            this.files.add(file);
        }

        public void addFiles(List<Object> files) {
            this.files.addAll(files);
        }

        public List<String> getPages() {
            return pages;
        }

        public void addPage(String page) {
            this.pages.add(page);
        }

        public void addPages(List<String> pages) {
            this.pages.addAll(pages);
        }

        public Map<String, Object> getModel() {
            return model;
        }

        public void setModel(Map<String, Object> model) {
            this.model = model;
        }
    }
}