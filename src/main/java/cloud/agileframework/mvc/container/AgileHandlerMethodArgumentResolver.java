package cloud.agileframework.mvc.container;

import cloud.agileframework.common.util.clazz.TypeReference;
import cloud.agileframework.mvc.param.AgileParam;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * @author 佟盟
 * 日期 2020/8/00024 14:12
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class AgileHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return true;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String parameterName = parameter.getParameterName();
        if (parameterName == null) {
            return null;
        }
        Type type = parameter.getGenericParameterType();
        if (type instanceof Class && MultipartFile.class.isAssignableFrom((Class) type)) {
            return AgileParam.getInParamOfFile(parameter.getParameterName());
        } else if (type instanceof Class && InputStream.class.isAssignableFrom((Class) type)) {
            return AgileParam.getInParamOfFile(parameter.getParameterName()).getInputStream();
        }
        return AgileParam.getInParam(parameterName.replace("_", "."), new TypeReference<>(type));
    }
}
