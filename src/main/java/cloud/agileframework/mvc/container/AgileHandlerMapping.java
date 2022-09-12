package cloud.agileframework.mvc.container;

import cloud.agileframework.mvc.annotation.Mapping;
import cloud.agileframework.spring.util.BeanUtil;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author 佟盟 on 2018/11/4
 */
public class AgileHandlerMapping extends RequestMappingHandlerMapping {
    private final Map<String, RequestMappingInfo> cache = new HashMap<>();

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
        return builder.build();
    }

    @Nullable
    private RequestMappingInfo createMappingInfo(AnnotatedElement element) {
        Mapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, Mapping.class);
        RequestCondition<?> condition = element instanceof Class ? this.getCustomTypeCondition((Class<?>) element) : this.getCustomMethodCondition((Method) element);
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

    @Override
    public void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
        final PatternsRequestCondition patternsCondition = mapping.getPatternsCondition();
        if (patternsCondition == null) {
            return;
        }
        for (String path : patternsCondition.getPatterns()) {
            RequestMappingInfo cacheMapping = cache.get(path);

            if (!ObjectUtils.isEmpty(cacheMapping)) {
                Set<RequestMethod> methods = mapping.getMethodsCondition().getMethods();
                for (RequestMethod requestMethod : cacheMapping.getMethodsCondition().getMethods()) {
                    if (methods.contains(requestMethod)) {
                        final String message = String.format("Mapping映射重复，重复类:%s,重复方法:%s", BeanUtil.getBeanClass(handler).getName(), method.getName());
                        if (logger.isErrorEnabled()) {
                            logger.error(message);
                        }
                        throw new IllegalStateException(message);
                    }
                }
            }

            cache.put(path, mapping);
        }
        super.registerHandlerMethod(handler, method, mapping);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("[class:%s][method:%s][url:%s]",
                    BeanUtil.getBeanClass(handler).getCanonicalName(),
                    method.getName(),
                    String.join(",", patternsCondition.getPatterns())));
        }
    }
}
