package com.agile.common.swagger;

import com.agile.common.annotation.Models;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationModelsProviderPlugin;
import springfox.documentation.spi.service.contexts.RequestMappingContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

/**
 * @author 佟盟
 * 日期 2019/5/27 14:04
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class AgileOperationModelsProviderPlugin implements OperationModelsProviderPlugin {

    @Override
    public void apply(RequestMappingContext context) {
        Models models = context.findAnnotation(Models.class).orNull();
        if (models == null) {
            return;
        }
        for (Class model : models.value()) {
            context.operationModelsBuilder().addInputParam(model);
        }
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }
}
