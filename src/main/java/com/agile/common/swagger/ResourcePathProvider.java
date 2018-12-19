package com.agile.common.swagger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.service.ResourceGroup;

import java.util.Arrays;

/**
 * Created by 佟盟 on 2018/11/23
 */
public class ResourcePathProvider {
    private final ResourceGroup resourceGroup;

    ResourcePathProvider(ResourceGroup resourceGroup) {
        this.resourceGroup = resourceGroup;
    }

    public Optional<String> resourcePath() {
        return Optional.fromNullable(
                Strings.emptyToNull(controllerClass()
                        .transform(resourcePathExtractor())
                        .or("")));
    }

    private Function<Class<?>, String> resourcePathExtractor() {
        return new Function<Class<?>, String>() {
            @Override
            public String apply(Class<?> input) {
                String path = Iterables.getFirst(Arrays.asList(paths(input)), "");
                if (Strings.isNullOrEmpty(path)) {
                    return "";
                }
                if (path.startsWith("/")) {
                    return path;
                }
                return "/" + path;
            }
        };
    }

    @VisibleForTesting
    String[] paths(Class<?> controller) {
        RequestMapping annotation
                = AnnotationUtils.findAnnotation(controller, RequestMapping.class);
        if (annotation != null) {
            return annotation.path();
        }
        return new String[]{};
    }

    private Optional<? extends Class<?>> controllerClass() {
        return resourceGroup.getControllerClass();
    }
}
