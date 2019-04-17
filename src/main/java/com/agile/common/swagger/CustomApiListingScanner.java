package com.agile.common.swagger;

import com.agile.common.annotation.Models;
import com.agile.common.base.ApiInfo;
import com.agile.common.util.ApiUtil;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import springfox.documentation.PathProvider;
import springfox.documentation.builders.ApiListingBuilder;
import springfox.documentation.schema.Model;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ApiListing;
import springfox.documentation.service.PathAdjuster;
import springfox.documentation.service.ResourceGroup;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.service.contexts.ApiListingContext;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.RequestMappingContext;
import springfox.documentation.spring.web.WebMvcRequestHandler;
import springfox.documentation.spring.web.paths.PathMappingAdjuster;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;
import springfox.documentation.spring.web.scanners.ApiDescriptionReader;
import springfox.documentation.spring.web.scanners.ApiListingScanner;
import springfox.documentation.spring.web.scanners.ApiListingScanningContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static springfox.documentation.spi.service.contexts.Orderings.methodComparator;
import static springfox.documentation.spi.service.contexts.Orderings.resourceGroupComparator;

/**
 * @author 佟盟 on 2018/11/23
 */
public class CustomApiListingScanner extends ApiListingScanner {
    private static final int LENGTH = 16;
    private final ApiDescriptionReader apiDescriptionReader;
    private final CustomApiModelReader apiModelReader;
    private final DocumentationPluginsManager pluginsManager;
    private final TypeResolver typeResolver;

    @Autowired
    public CustomApiListingScanner(ApiDescriptionReader apiDescriptionReader, CustomApiModelReader customApiModelReader, DocumentationPluginsManager pluginsManager, TypeResolver typeResolver) {
        super(apiDescriptionReader, customApiModelReader, pluginsManager);
        this.apiDescriptionReader = apiDescriptionReader;
        this.apiModelReader = customApiModelReader;
        this.pluginsManager = pluginsManager;
        this.typeResolver = typeResolver;
    }

    static Iterable<ResourceGroup> collectResourceGroups(Collection<ApiDescription> apiDescriptions) {
        return apiDescriptions.stream().map(toResourceGroups()).collect(Collectors.toList());
    }

    private static Iterable<ResourceGroup> sortedByName(Set<ResourceGroup> resourceGroups) {
        return resourceGroups.stream().sorted(resourceGroupComparator()).collect(Collectors.toList());
    }

    private static Predicate<ApiDescription> belongsTo(final String groupName) {
        return input -> !input.getGroupName().isPresent()
                || groupName.equals(input.getGroupName().get());
    }

    private static Function<ApiDescription, ResourceGroup> toResourceGroups() {
        return input -> new ResourceGroup(
                input.getGroupName().or(Docket.DEFAULT_GROUP_NAME),
                null);
    }

    private static Optional<String> longestCommonPath(List<ApiDescription> apiDescriptions) {
        List<String> commons = newArrayList();
        if (null == apiDescriptions || apiDescriptions.isEmpty()) {
            return Optional.absent();
        }
        List<String> firstWords = urlParts(apiDescriptions.get(0));

        for (int position = 0; position < firstWords.size(); position++) {
            String word = firstWords.get(position);
            boolean allContain = true;
            for (int i = 1; i < apiDescriptions.size(); i++) {
                List<String> words = urlParts(apiDescriptions.get(i));
                if (words.size() < position + 1 || !words.get(position).equals(word)) {
                    allContain = false;
                    break;
                }
            }
            if (allContain) {
                commons.add(word);
            }
        }
        Joiner joiner = Joiner.on("/").skipNulls();
        return Optional.of("/" + joiner.join(commons));
    }

    static List<String> urlParts(ApiDescription apiDescription) {
        return Splitter.on('/')
                .omitEmptyStrings()
                .trimResults()
                .splitToList(apiDescription.getPath());
    }

    @Override
    public Multimap<String, ApiListing> scan(ApiListingScanningContext context) {
        final Multimap<String, ApiListing> apiListingMap = LinkedListMultimap.create();
        int position = 0;

        Collection<ApiDescription> additionalListings = pluginsManager.additionalListings(context);
        Collection<ApiInfo> list = ApiUtil.getApiInfoCache();

        Map<String, ResourceGroup> resourceGroupCache = new HashMap<>(LENGTH);
        Map<ResourceGroup, List<RequestMappingContext>> requestMappingContextListCache = new HashMap<>(LENGTH);
        for (ApiInfo apiInfo : list) {
            Set<RequestMappingInfo> apiInfoRequestMappingInfos = apiInfo.getRequestMappingInfos();
            if (apiInfoRequestMappingInfos == null || apiInfoRequestMappingInfos.size() == 0) {
                continue;
            }

            for (RequestMappingInfo requestMappingInfo : apiInfoRequestMappingInfos) {
                RequestMappingContext requestMappingContext = new RequestMappingContext(
                        context.getDocumentationContext(),
                        new WebMvcRequestHandler(
                                new HandlerMethodResolver(typeResolver),
                                requestMappingInfo,
                                new HandlerMethod(apiInfo.getBean(), apiInfo.getMethod())
                        )
                );
                String groupName = requestMappingContext.getGroupName();
                Class<?> bean = AopUtils.getTargetClass(apiInfo.getBean());

                ResourceGroup currentResourceGroup;
                if (resourceGroupCache.containsKey(groupName)) {
                    currentResourceGroup = resourceGroupCache.get(groupName);
                } else {
                    currentResourceGroup = new ResourceGroup(groupName, bean);
                    resourceGroupCache.put(groupName, currentResourceGroup);
                }

                List<RequestMappingContext> currentRequestMappings;
                if (requestMappingContextListCache.containsKey(currentResourceGroup)) {
                    currentRequestMappings = requestMappingContextListCache.get(currentResourceGroup);
                } else {
                    currentRequestMappings = new ArrayList<>();
                    requestMappingContextListCache.put(currentResourceGroup, currentRequestMappings);
                }
                currentRequestMappings.add(requestMappingContext);
            }
        }

//        allResourceGroups.addAll(requestMappingsByResourceGroup.keySet());
        List<SecurityReference> securityReferences = newArrayList();
        for (final ResourceGroup resourceGroup : sortedByName(requestMappingContextListCache.keySet())) {

            DocumentationContext documentationContext = context.getDocumentationContext();
            Set<String> produces = new LinkedHashSet<>(documentationContext.getProduces());
            Set<String> consumes = new LinkedHashSet<>(documentationContext.getConsumes());
            String host = documentationContext.getHost();
            Set<String> protocols = new LinkedHashSet<>(documentationContext.getProtocols());
            Set<ApiDescription> apiDescriptions = newHashSet();

            Map<String, Model> models = new LinkedHashMap<>();
            for (RequestMappingContext each : sortedByMethods(requestMappingContextListCache.get(resourceGroup))) {
                models.putAll(apiModelReader.read(each.withKnownModels(models)));
                apiDescriptions.addAll(apiDescriptionReader.read(each));

                Optional<Models> modelsOptional = each.findAnnotation(Models.class);
                if (modelsOptional.isPresent()) {
                    Class[] classes = modelsOptional.get().value();
                    for (Class c : classes) {
                        apiModelReader.read(documentationContext, c, models);
                    }
                }
            }

            List<ApiDescription> additional = additionalListings.stream().filter(belongsTo(resourceGroup.getGroupName()).and(onlySelectedApis(documentationContext))).collect(Collectors.toList());
            apiDescriptions.addAll(additional);

            List<ApiDescription> sortedApis = apiDescriptions.stream().sorted(documentationContext.getApiDescriptionOrdering()).collect(Collectors.toList());

            String resourcePath = new ResourcePathProvider(resourceGroup)
                    .resourcePath()
                    .or(longestCommonPath(sortedApis))
                    .orNull();

            PathProvider pathProvider = documentationContext.getPathProvider();
            String basePath = pathProvider.getApplicationBasePath();
            PathAdjuster adjuster = new PathMappingAdjuster(documentationContext);
            ApiListingBuilder apiListingBuilder = new ApiListingBuilder(context.apiDescriptionOrdering())
                    .apiVersion(documentationContext.getApiInfo().getVersion())
                    .basePath(adjuster.adjustedPath(basePath)).resourcePath(resourcePath).produces(produces)
                    .consumes(consumes).host(host).protocols(protocols).securityReferences(securityReferences)
                    .apis(sortedApis).models(models).position(position++).availableTags(documentationContext.getTags());

            ApiListingContext apiListingContext = new ApiListingContext(
                    context.getDocumentationType(),
                    resourceGroup,
                    apiListingBuilder);
            apiListingMap.put(resourceGroup.getGroupName(), pluginsManager.apiListing(apiListingContext));
        }
        return apiListingMap;
    }

    private Iterable<RequestMappingContext> sortedByMethods(List<RequestMappingContext> contexts) {
        return contexts.stream().sorted(methodComparator()).collect(Collectors.toList());
    }

    private Predicate<ApiDescription> onlySelectedApis(final DocumentationContext context) {
        return input -> context.getApiSelector().getPathSelector().apply(input.getPath());
    }

}
