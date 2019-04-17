package com.agile.common.config;

import com.agile.common.properties.SwaggerConfigProperties;
import com.agile.common.swagger.CustomApiListingScanner;
import com.agile.common.swagger.CustomApiModelReader;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.CachingModelProvider;
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
@EnableConfigurationProperties(value = {SwaggerConfigProperties.class})
@ConditionalOnProperty(name = "enable", prefix = "agile.swagger", havingValue = "true")
@ConditionalOnClass({Docket.class, ApiInfo.class, CustomApiListingScanner.class, CustomApiModelReader.class})
@EnableSwagger2
public class Swagger2AutoConfiguration {

    private final SwaggerConfigProperties swaggerConfigProperties;

    @Autowired
    public Swagger2AutoConfiguration(SwaggerConfigProperties swaggerConfigProperties) {
        this.swaggerConfigProperties = swaggerConfigProperties;
    }

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
                .title(swaggerConfigProperties.getTitle())
                .description(swaggerConfigProperties.getDescription())
                .termsOfServiceUrl(swaggerConfigProperties.getTermsOfServiceUrl())
                .version(swaggerConfigProperties.getVersion())
                .build();
    }

    @Bean
    @Primary
    public CustomApiListingScanner customApiListingScanner(ApiDescriptionReader apiDescriptionReader, CustomApiModelReader apiModelReader, DocumentationPluginsManager pluginsManager, TypeResolver typeResolver) {
        return new CustomApiListingScanner(apiDescriptionReader, apiModelReader, pluginsManager, typeResolver);
    }

    @Bean
    public CustomApiModelReader customApiModelReader(CachingModelProvider modelProvider, TypeResolver typeResolver, DocumentationPluginsManager pluginsManager) {
        return new CustomApiModelReader(modelProvider, typeResolver, pluginsManager);
    }
}
