package com.agile.common.container;

import com.agile.common.annotation.Mapping;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.util.StringUtil;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.util.ProxyUtils;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 佟盟 on 2018/11/4
 */
public class MappingHandlerMapping extends RequestMappingHandlerMapping {
    private boolean useSuffixPatternMatch = true;
    private boolean useRegisteredSuffixPatternMatch = false;
    private boolean useTrailingSlashMatch = true;
    private Map<String, RequestMappingInfo> cache = new HashMap<>();

    private RequestMappingInfo.BuilderConfiguration config = new RequestMappingInfo.BuilderConfiguration();

    private RequestMappingInfo createMappingInfo(Mapping mapping, RequestCondition<?> condition) {

        RequestMappingInfo.Builder builder = RequestMappingInfo
                .paths(this.resolveEmbeddedValuesInPatterns(mapping.path()))
                .methods(mapping.method())
                .params(mapping.params())
                .headers(mapping.headers())
                .consumes(mapping.consumes())
                .produces(mapping.produces())
                .mappingName(mapping.name());
        if (condition != null) {
            builder.customCondition(condition);
        }
        return builder.options(this.config).build();
    }

    @Nullable
    private RequestMappingInfo createMappingInfo(AnnotatedElement element) {
        Mapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, Mapping.class);
        RequestCondition<?> condition = element instanceof Class ? this.getCustomTypeCondition((Class) element) : this.getCustomMethodCondition((Method) element);
        return requestMapping != null ? this.createMappingInfo(requestMapping, condition) : null;
    }

    @Override
    public RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = this.createMappingInfo(method);
        if (info != null) {
            RequestMappingInfo typeInfo = this.createMappingInfo(handlerType);
            if (typeInfo != null) {
                info = typeInfo.combine(info);
            }
        }
        return info;
    }

    private String createDefaultMappingPath(AnnotatedElement element) {
        StringBuilder path = new StringBuilder();
        if (element instanceof Class) {
            path.append(String.format("/api/{service:%s}", StringUtil.camelToUrlRegex(((Class) element).getSimpleName())));
        } else if (element instanceof Method) {
            path.append(String.format("/{method:%s}", StringUtil.camelToUrlRegex(((Method) element).getName())));
        }
        return path.toString();
    }

    private RequestMappingInfo createDefaultMappingInfo(AnnotatedElement element, RequestCondition<?> condition) {
        RequestMappingInfo.Builder builder = RequestMappingInfo.paths(this.resolveEmbeddedValuesInPatterns(new String[]{createDefaultMappingPath(element)})).methods().params().headers().consumes().produces().mappingName("");
        if (condition != null) {
            builder.customCondition(condition);
        }
        return builder.options(this.config).build();
    }

    private RequestMappingInfo createDefaultMappingInfo(AnnotatedElement element) {
        RequestCondition<?> condition = element instanceof Class ? this.getCustomTypeCondition((Class) element) : this.getCustomMethodCondition((Method) element);
        return this.createDefaultMappingInfo(element, condition);
    }

    public RequestMappingInfo getDefaultFroMethod(Method method, Class<?> handlerType) {

        RequestMappingInfo defaultMappingInfo = this.createDefaultMappingInfo(method);
        if (defaultMappingInfo != null) {
            RequestMappingInfo defaultTypeInfo = this.createDefaultMappingInfo(handlerType);
            if (defaultTypeInfo != null) {
                defaultMappingInfo = defaultTypeInfo.combine(defaultMappingInfo);
            }
        }
        return defaultMappingInfo;
    }

    @Override
    public void afterPropertiesSet() {
        this.config = new RequestMappingInfo.BuilderConfiguration();
        this.config.setUrlPathHelper(this.getUrlPathHelper());
        this.config.setPathMatcher(this.getPathMatcher());
        this.config.setSuffixPatternMatch(this.useSuffixPatternMatch);
        this.config.setTrailingSlashMatch(this.useTrailingSlashMatch);
        this.config.setRegisteredSuffixPatternMatch(this.useRegisteredSuffixPatternMatch);
        this.config.setContentNegotiationManager(this.getContentNegotiationManager());
    }

    @Override
    public void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
        if (mapping == null) {
            return;
        }
        if (mapping.getPatternsCondition() != null) {
            for (String path : mapping.getPatternsCondition().getPatterns()) {
                if (cache.containsKey(path)) {
                    LoggerFactory.getCommonLog().error(String.format("Mapping映射重复，重复类:%s,重复方法:%s", ProxyUtils.getUserClass(handler).getName(), method.getName()));
                    throw new IllegalStateException();
                } else {
                    cache.put(path, mapping);
                }
            }
        }
        super.registerHandlerMethod(handler, method, mapping);
    }
}
