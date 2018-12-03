package com.agile.common.container;

import com.agile.common.annotation.Mapping;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

/**
 * Created by 佟盟 on 2018/11/4
 */
public class MappingHandlerMapping extends RequestMappingHandlerMapping {
    private boolean useSuffixPatternMatch = true;
    private boolean useRegisteredSuffixPatternMatch = false;
    private boolean useTrailingSlashMatch = true;

    private RequestMappingInfo.BuilderConfiguration config = new RequestMappingInfo.BuilderConfiguration();
    private RequestMappingInfo createMappingInfo(Mapping mapping, RequestCondition<?> condition){
        RequestMappingInfo.Builder builder = RequestMappingInfo.paths(this.resolveEmbeddedValuesInPatterns(mapping.path())).methods(mapping.method()).params(mapping.params()).headers(mapping.headers()).consumes(mapping.consumes()).produces(mapping.produces()).mappingName(mapping.name());
        if (condition != null) {
            builder.customCondition(condition);
        }
        return builder.options(this.config).build();
    }

    private RequestMappingInfo createMappingInfo(String beanName,String methodName){
        RequestMappingInfo.Builder builder = RequestMappingInfo.paths(this.resolveEmbeddedValuesInPatterns(new String[]{String.format("/api/%s/%s",beanName,methodName)})).methods(RequestMethod.GET,RequestMethod.POST).params().headers().consumes().produces().mappingName("");
        return builder.options(this.config).build();
    }

    @Nullable
    private RequestMappingInfo createMappingInfo(AnnotatedElement element) {
        Mapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, Mapping.class);
        RequestCondition<?> condition = element instanceof Class ? this.getCustomTypeCondition((Class)element) : this.getCustomMethodCondition((Method)element);
        return requestMapping != null ? this.createMappingInfo(requestMapping, condition) : null;
    }

    public RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = this.createMappingInfo(method);
        if (info != null) {
            RequestMappingInfo typeInfo = this.createMappingInfo(handlerType);
            if (typeInfo != null) {
                info = typeInfo.combine(info);
            }
        }

        if(info == null && method.getParameters().length==0){
            info = createMappingInfo(handlerType.getSimpleName(),method.getName());
        }
        return info;
    }

    public void afterPropertiesSet() {
        this.config = new RequestMappingInfo.BuilderConfiguration();
        this.config.setUrlPathHelper(this.getUrlPathHelper());
        this.config.setPathMatcher(this.getPathMatcher());
        this.config.setSuffixPatternMatch(this.useSuffixPatternMatch);
        this.config.setTrailingSlashMatch(this.useTrailingSlashMatch);
        this.config.setRegisteredSuffixPatternMatch(this.useRegisteredSuffixPatternMatch);
        this.config.setContentNegotiationManager(this.getContentNegotiationManager());
    }

    public void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping){
        super.registerHandlerMethod( handler,  method,  mapping);
    }
}
