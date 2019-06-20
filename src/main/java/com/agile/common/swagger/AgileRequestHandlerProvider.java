package com.agile.common.swagger;

import com.agile.common.util.ApiUtil;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import springfox.documentation.RequestHandler;
import springfox.documentation.spi.service.RequestHandlerProvider;
import springfox.documentation.spring.web.WebMvcRequestHandler;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static springfox.documentation.builders.BuilderDefaults.nullToEmptyList;
import static springfox.documentation.spi.service.contexts.Orderings.byPatternsCondition;

/**
 * @author 佟盟
 * 日期 2019/5/22 18:54
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class AgileRequestHandlerProvider implements RequestHandlerProvider {
    private final List<RequestMappingInfoHandlerMapping> handlerMappings;
    private final HandlerMethodResolver methodResolver;

    public AgileRequestHandlerProvider(HandlerMethodResolver methodResolver) {
        List<RequestMappingInfoHandlerMapping> list = new ArrayList<>();
        list.add(ApiUtil.getMappingHandlerMapping());
        this.handlerMappings = list;
        this.methodResolver = methodResolver;
    }

    @Override
    public List<RequestHandler> requestHandlers() {
        return byPatternsCondition().sortedCopy(nullToEmptyList(handlerMappings).stream()
                .flatMap(handlerMapping -> toMappingEntries().apply(handlerMapping).stream().map(map -> toRequestHandler().apply(map)))
                .collect(Collectors.toList()));
    }

    private Function<? super RequestMappingInfoHandlerMapping, Set<Map.Entry<RequestMappingInfo, HandlerMethod>>> toMappingEntries() {
        return input -> input.getHandlerMethods().entrySet();
    }

    private Function<Map.Entry<RequestMappingInfo, HandlerMethod>, RequestHandler> toRequestHandler() {
        return map -> new WebMvcRequestHandler(
                methodResolver,
                map.getKey(),
                map.getValue());
    }
}
