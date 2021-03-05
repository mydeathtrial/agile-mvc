package cloud.agileframework.mvc.container;

import cloud.agileframework.common.constant.Constant;
import cloud.agileframework.common.util.clazz.TypeReference;
import cloud.agileframework.common.util.object.ObjectUtil;
import cloud.agileframework.mvc.annotation.AgileInParam;
import cloud.agileframework.mvc.param.AgileParam;
import cloud.agileframework.spring.util.RequestWrapper;
import org.springframework.core.MethodParameter;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * @author 佟盟
 * 日期 2020/8/00024 14:12
 * 描述 处理方法入参赋值
 * @version 1.0
 * @since 1.0
 */
public class AgileHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        final String parameterName = parameter.getParameterName();
        if (parameterName == null) {
            return false;
        }
        return AgileParam.containsKey(parameterName)
                || parameter.getParameterAnnotation(AgileInParam.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        final RequestWrapper requestWrapper = webRequest.getNativeRequest(RequestWrapper.class);
        // 如果存在默认model，则清理
        if (mavContainer != null) {
            ModelMap defaultModel = mavContainer.getDefaultModel();
            defaultModel.clear();
        }

        Object result = null;
        Type type = parameter.getGenericParameterType();
        if (TypeVariable.class.isAssignableFrom(type.getClass())) {
            type = parameter.getParameterType();
        }
        AgileInParam agileInParam = parameter.getParameterAnnotation(AgileInParam.class);
        if (agileInParam != null) {
            result = parsing(type, agileInParam.value(), requestWrapper);
        }

        if (result == null) {
            String parameterName = parameter.getParameterName();
            if (parameterName == null) {
                return null;
            }

            result = parsing(type, parameterName, requestWrapper);
        }
        return result;
    }

    private Object parsing(Type type, String parameterName, RequestWrapper requestWrapper) throws IOException {
        if (type instanceof Class && MultipartFile.class.isAssignableFrom((Class<?>) type)) {
            return AgileParam.getInParamOfFile(parameterName);
        } else if (type instanceof Class && InputStream.class.isAssignableFrom((Class<?>) type)) {
            return AgileParam.getInParamOfFile(parameterName).getInputStream();
        } else if (parameterName.equalsIgnoreCase(Constant.RequestAbout.BODY)) {
            if (requestWrapper == null) {
                return AgileParam.getInParam(new TypeReference<>(type));
            }
            Object bodyData = requestWrapper.getAttribute(Constant.RequestAbout.BODY);
            if (Constant.RequestAbout.BODY_REF.equals(bodyData)) {
                return AgileParam.getInParam(new TypeReference<>(type));
            } else {
                return ObjectUtil.to(bodyData, new TypeReference<>(type));
            }
        } else {
            return AgileParam.getInParam(parameterName, new TypeReference<>(type));
        }
    }
}
