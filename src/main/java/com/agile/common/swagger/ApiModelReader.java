package com.agile.common.swagger;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import springfox.documentation.builders.ModelBuilder;
import springfox.documentation.schema.Model;
import springfox.documentation.schema.ModelProperty;
import springfox.documentation.schema.ModelProvider;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

/**
 * Model类读取
 */
public class ApiModelReader extends springfox.documentation.spring.web.scanners.ApiModelReader {
    private final ModelProvider modelProvider;
    private final TypeResolver typeResolver;

    public ApiModelReader(ModelProvider modelProvider, TypeResolver typeResolver, DocumentationPluginsManager pluginsManager) {
        super(modelProvider, typeResolver, pluginsManager);
        this.modelProvider = modelProvider;
        this.typeResolver = typeResolver;
    }

    void read(DocumentationContext documentationContext, Type type, Map<String, Model> models) {
        Set<Class> ignorableTypes = newHashSet(documentationContext.getIgnorableParameterTypes());

        ModelContext inputParam = ModelContext.inputParam(
                documentationContext.getGroupName(),
                type,
                documentationContext.getDocumentationType(),
                documentationContext.getAlternateTypeProvider(),
                documentationContext.getGenericsNamingStrategy(),
                documentationContext.getIgnorableParameterTypes());

        markIgnorablesAsHasSeen(typeResolver, ignorableTypes, inputParam);
        Optional<Model> pModel = modelProvider.modelFor(inputParam);
        if (pModel.isPresent()) {
            mergeModelMap(models, pModel.get());
        }
        populateDependencies(inputParam, models);
    }

    private void markIgnorablesAsHasSeen(
            TypeResolver typeResolver,
            Set<Class> ignorableParameterTypes,
            ModelContext modelContext) {

        for (Class ignorableParameterType : ignorableParameterTypes) {
            modelContext.seen(typeResolver.resolve(ignorableParameterType));
        }
    }

    private void populateDependencies(ModelContext modelContext, Map<String, Model> modelMap) {
        Map<String, Model> dependencies = modelProvider.dependencies(modelContext);
        for (Model each : dependencies.values()) {
            mergeModelMap(modelMap, each);
        }
    }

    private void mergeModelMap(Map<String, Model> target, Model source) {
        String sourceModelKey = source.getId();

        if (!target.containsKey(sourceModelKey)) {
            //if we encounter completely unknown model, just add it
            target.put(sourceModelKey, source);
        } else {
            //we can encounter a known model with an unknown property
            //if (de)serialization is not symmetrical (@JsonIgnore on setter, @JsonProperty on getter).
            //In these cases, don't overwrite the entire model entry for that type, just add the unknown property.
            Model targetModelValue = target.get(sourceModelKey);

            Map<String, ModelProperty> targetProperties = targetModelValue.getProperties();
            Map<String, ModelProperty> sourceProperties = source.getProperties();

            Set<String> newSourcePropKeys = newHashSet(sourceProperties.keySet());
            newSourcePropKeys.removeAll(targetProperties.keySet());
            Map<String, ModelProperty> mergedTargetProperties = Maps.newHashMap(targetProperties);
            for (String newProperty : newSourcePropKeys) {
                mergedTargetProperties.put(newProperty, sourceProperties.get(newProperty));
            }

            Model mergedModel = new ModelBuilder()
                    .id(targetModelValue.getId())
                    .name(targetModelValue.getName())
                    .type(targetModelValue.getType())
                    .qualifiedType(targetModelValue.getQualifiedType())
                    .properties(mergedTargetProperties)
                    .description(targetModelValue.getDescription())
                    .baseModel(targetModelValue.getBaseModel())
                    .discriminator(targetModelValue.getDiscriminator())
                    .subTypes(targetModelValue.getSubTypes())
                    .example(targetModelValue.getExample())
                    .build();

            target.put(sourceModelKey, mergedModel);
        }
    }
}
