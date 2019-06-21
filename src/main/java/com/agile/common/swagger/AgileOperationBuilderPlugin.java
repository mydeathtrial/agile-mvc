package com.agile.common.swagger;

import com.fasterxml.classmate.types.ResolvedObjectType;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spring.web.readers.parameter.ExpansionContext;
import springfox.documentation.spring.web.readers.parameter.ModelAttributeParameterExpander;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 佟盟
 * 日期 2019/5/27 16:18
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class AgileOperationBuilderPlugin implements OperationBuilderPlugin {

    @Autowired
    private ModelAttributeParameterExpander expander;

    @Override
    public void apply(OperationContext context) {
        List<ApiImplicitParam> apiImplicitParamList = context.findAnnotation(ApiImplicitParams.class)
                .transform(apiImplicitParams -> Arrays
                        .stream(apiImplicitParams.value())
                        .collect(Collectors.toList())).or(new ArrayList<>());
        ApiImplicitParam apiParam = context.findAnnotation(ApiImplicitParam.class).orNull();
        if (apiParam != null) {
            apiImplicitParamList.add(apiParam);
        }

        for (ApiImplicitParam apiImplicitParam : apiImplicitParamList) {
            if ("body".equals(apiImplicitParam.paramType())) {
                continue;
            }
            List<Parameter> parameters = expander.expand(new ExpansionContext(apiImplicitParam.name(), ResolvedObjectType.create(apiImplicitParam.dataTypeClass(), null, null, null), context));
            context.operationBuilder().parameters(parameters);
        }
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }
}
