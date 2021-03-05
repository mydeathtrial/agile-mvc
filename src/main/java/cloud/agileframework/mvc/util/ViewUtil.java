package cloud.agileframework.mvc.util;

import cloud.agileframework.common.constant.Constant;
import cloud.agileframework.common.util.file.FileUtil;
import cloud.agileframework.mvc.base.AbstractResponseFormat;
import cloud.agileframework.mvc.base.Head;
import cloud.agileframework.spring.util.BeanUtil;
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
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

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


    public static void render(Head head, Object result, HttpServletRequest request, HttpServletResponse response) throws Exception {
        render(getResponseFormatData(head, result), request, response);
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
        AbstractResponseFormat abstractResponseFormat = BeanUtil.getBean(AbstractResponseFormat.class);
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

    public static void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request, response));

        ApplicationContext context = BeanUtil.getApplicationContext();

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

        if (mv.getStatus() != null) {
            response.setStatus(mv.getStatus().value());
        }

        view.render(mv.getModel(), request, response);
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
        viewUtil.locale = localeResolvers.size() > 0 ? (localeResolvers.get(0)).resolveLocale(request) : request.getLocale();
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
            int var8 = classNames.length;

            for (int var9 = 0; var9 < var8; ++var9) {
                String className = classNames[var9];

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

    /**
     * 提取model对象中的文件数据
     *
     * @param model 容器
     * @return 容器中包含的所有文件
     */
    @SuppressWarnings("unchecked")
    public static List<Object> extractFiles(Object model) {
        List<Object> result = new ArrayList<>();

        if (FileUtil.isFile(model)) {
            result.add(model);
        } else if (model != null && Map.class.isAssignableFrom(model.getClass())) {
            ((Map<String, Object>) model).values().forEach(v -> result.addAll(extractFiles(v)));
        } else if (model != null && Collection.class.isAssignableFrom(model.getClass())) {
            ((Collection<?>) model).forEach(v -> result.addAll(extractFiles(v)));
        }

        return result;
    }

    @PostConstruct
    public static void init() {
        viewUtil = new ViewUtil();
    }

}
