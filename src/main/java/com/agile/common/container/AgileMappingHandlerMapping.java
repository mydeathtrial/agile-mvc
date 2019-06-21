package com.agile.common.container;

import com.agile.common.annotation.ApiMethod;
import com.agile.common.annotation.Mapping;
import com.agile.common.base.Constant;
import com.agile.common.util.StringUtil;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringValueResolver;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author 佟盟
 * 日期 2019/5/28 15:38
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
//@Component
public class AgileMappingHandlerMapping extends RequestMappingHandlerMapping {

    @Nullable
    private StringValueResolver embeddedValueResolver;
    private RequestMappingInfo.BuilderConfiguration config = new RequestMappingInfo.BuilderConfiguration();

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    @Override
    protected boolean isHandler(Class<?> beanType) {
        return (AnnotatedElementUtils.hasAnnotation(beanType, Service.class) ||
                AnnotatedElementUtils.hasAnnotation(beanType, Mapping.class));
    }

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {

        RequestMappingInfo info = createRequestMappingInfo(method);
        if (info != null) {
            RequestMappingInfo typeInfo = createRequestMappingInfo(handlerType);
            if (typeInfo != null) {
                info = typeInfo.combine(info);
            }
            String prefix = getPathPrefix(handlerType);
            if (prefix != null) {
                info = RequestMappingInfo.paths(prefix).build().combine(info);
            }
        }

        int parameterCount = method.getParameterCount();
        if (parameterCount == 0 && info != null) {
            RequestMappingInfo defaultMapping = createDefaultMappingInfo(method, handlerType);
            List<String> list = new ArrayList<>(defaultMapping.getPatternsCondition().getPatterns());
            PatternsRequestCondition patternsCondition = info.getPatternsCondition();
            if (patternsCondition != null) {
                list.addAll(patternsCondition.getPatterns());
            }
            PatternsRequestCondition patternsRequestCondition = new PatternsRequestCondition(list.toArray(new String[]{}),
                    this.config.getUrlPathHelper(), this.config.getPathMatcher(),
                    this.config.useSuffixPatternMatch(), this.config.useTrailingSlashMatch(),
                    this.config.getFileExtensions());
            return new RequestMappingInfo(patternsRequestCondition,
                    info.getMethodsCondition(),
                    info.getParamsCondition(),
                    info.getHeadersCondition(),
                    info.getConsumesCondition(),
                    info.getProducesCondition(),
                    info.getCustomCondition());
        } else if (parameterCount == 0) {
            return createDefaultMappingInfo(method, handlerType);
        } else {
            return info;
        }

    }

    @Nullable
    private String getPathPrefix(Class<?> handlerType) {
        for (Map.Entry<String, Predicate<Class<?>>> entry : super.getPathPrefixes().entrySet()) {
            if (entry.getValue().test(handlerType)) {
                String prefix = entry.getKey();
                if (this.embeddedValueResolver != null) {
                    prefix = this.embeddedValueResolver.resolveStringValue(prefix);
                }
                return prefix;
            }
        }
        return null;
    }

    private RequestMappingInfo createDefaultMappingInfo(Method method, Class<?> handlerType) {
        RequestMethod[] apiMethod = createApiMethodContain(method);
        if (apiMethod == null) {
            apiMethod = createApiMethodContain(handlerType);
        }

        RequestMappingInfo.Builder builder = RequestMappingInfo
                .paths(String.format("/api/%s/%s", StringUtil.camelToSpilt(handlerType.getSimpleName(), Constant.RegularAbout.MINUS).toLowerCase(), StringUtil.camelToSpilt(method.getName(), Constant.RegularAbout.MINUS).toLowerCase()));
        if (apiMethod != null) {
            builder.methods(apiMethod);
        }
        return builder.options(this.config).build();
    }

    private RequestMethod[] createApiMethodContain(AnnotatedElement element) {
        ApiMethod apiMethod = AnnotatedElementUtils.findMergedAnnotation(element, ApiMethod.class);
        return apiMethod != null ? apiMethod.value() : null;
    }

    @Nullable
    private RequestMappingInfo createRequestMappingInfo(AnnotatedElement element) {
        Mapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, Mapping.class);
        RequestCondition<?> condition = (element instanceof Class ?
                getCustomTypeCondition((Class<?>) element) : getCustomMethodCondition((Method) element));
        return (requestMapping != null ? createRequestMappingInfo(requestMapping, condition) : null);
    }

    private RequestMappingInfo createRequestMappingInfo(
            Mapping requestMapping, @Nullable RequestCondition<?> customCondition) {

        RequestMappingInfo.Builder builder = RequestMappingInfo
                .paths(resolveEmbeddedValuesInPatterns(requestMapping.path()))
                .methods(requestMapping.method())
                .params(requestMapping.params())
                .headers(requestMapping.headers())
                .consumes(requestMapping.consumes())
                .produces(requestMapping.produces())
                .mappingName(requestMapping.name());
        if (customCondition != null) {
            builder.customCondition(customCondition);
        }
        return builder.options(this.config).build();
    }
}
