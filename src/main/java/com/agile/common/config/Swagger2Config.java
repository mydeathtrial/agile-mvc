package com.agile.common.config;

import com.agile.common.properties.SwaggerConfigProperties;
import com.agile.common.swagger.ApiListingScanner;
import com.agile.common.swagger.ApiModelReader;
import com.agile.common.util.PropertiesUtil;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.type.AnnotatedTypeMetadata;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelProvider;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;
import springfox.documentation.spring.web.scanners.ApiDescriptionReader;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author 佟盟 on 2018/11/22
 */
@Configuration
@Conditional(Swagger2Config.class)
@EnableSwagger2
public class Swagger2Config implements Condition {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.agile"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(SwaggerConfigProperties.getTitle())
                .description(SwaggerConfigProperties.getDescription())
                .termsOfServiceUrl(SwaggerConfigProperties.getTermsOfServiceUrl())
                .version(SwaggerConfigProperties.getVersion())
                .build();
    }

    @Bean
    @Primary
    public ApiListingScanner customApiListingScanner(ApiDescriptionReader apiDescriptionReader, ApiModelReader apiModelReader, DocumentationPluginsManager pluginsManager, TypeResolver typeResolver) {
        return new ApiListingScanner(apiDescriptionReader, apiModelReader, pluginsManager, typeResolver);
    }

    @Bean
    public ApiModelReader customApiModelReader(@Qualifier("cachedModels") ModelProvider modelProvider, TypeResolver typeResolver, DocumentationPluginsManager pluginsManager) {
        return new ApiModelReader(modelProvider, typeResolver, pluginsManager);
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return PropertiesUtil.getProperty("agile.swagger.enable", boolean.class);
    }
}
