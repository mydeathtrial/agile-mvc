package com.agile.common.util;

import com.agile.common.annotation.Validate;
import com.agile.common.annotation.Validates;
import com.agile.common.base.AbstractResponseFormat;
import com.agile.common.base.ApiInfo;
import com.agile.common.base.Constant;
import com.agile.common.base.Head;
import com.agile.common.base.RequestWrapper;
import com.agile.common.mvc.service.ServiceInterface;
import com.agile.common.validate.ValidateMsg;
import com.agile.common.validate.ValidateType;
import com.agile.common.view.ForwardView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;
import org.apache.commons.compress.utils.Lists;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 佟盟
 * 日期 2019/4/12 14:10
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class ParamUtil {
    /**
     * 根据servlet请求、认证信息、目标服务名、目标方法名处理入参
     */
    public static Map<String, Object> handleInParam(HttpServletRequest currentRequest) {
        final int length = 16;
        Map<String, Object> inParam = new HashMap<>(length);

        Map<String, String[]> parameterMap = currentRequest.getParameterMap();
        if (parameterMap.size() > 0) {
            for (Map.Entry<String, String[]> map : parameterMap.entrySet()) {
                String[] v = map.getValue();
                if (v.length == 1) {
                    inParam.put(map.getKey(), v[0]);
                } else {
                    inParam.put(map.getKey(), v);
                }
            }
        }

        if (currentRequest instanceof RequestWrapper) {
            Map<String, String[]> forwardMap = ((RequestWrapper) currentRequest).getForwardParameterMap();
            for (Map.Entry<String, String[]> map : forwardMap.entrySet()) {
                inParam.put(map.getKey(), map.getValue());
            }
        }

        //判断是否存在文件上传
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(currentRequest.getSession().getServletContext());
        if (multipartResolver.isMultipart(currentRequest)) {
            Map<String, Object> formData = FileUtil.getFileFormRequest(currentRequest);

            for (Map.Entry<String, Object> entry : formData.entrySet()) {
                if (inParam.containsKey(entry.getKey())) {
                    continue;
                }
                inParam.put(entry.getKey(), entry.getValue());
            }
        } else {
            String bodyParam = ServletUtil.getBody(currentRequest);
            if (bodyParam != null) {
                inParam.put(Constant.ResponseAbout.BODY, bodyParam);
            }
        }

        Enumeration<String> attributeNames = currentRequest.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String key = attributeNames.nextElement();
            String prefix = ForwardView.getPrefix();
            if (key.startsWith(prefix)) {
                inParam.put(key.replace(prefix, ""), currentRequest.getAttribute(key));
            }
        }

        //处理Mapping参数
        String uri = currentRequest.getRequestURI();
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
        }
        ApiInfo info = ApiUtil.getApiCache(currentRequest);

        //处理路径入参
        if (info != null) {
            Set<RequestMappingInfo> requestMappingInfos = info.getRequestMappingInfos();
            if (requestMappingInfos != null) {
                Set<String> mappingCache = new HashSet<>();
                for (RequestMappingInfo requestMappingInfo : requestMappingInfos) {
                    mappingCache.addAll(requestMappingInfo.getPatternsCondition().getPatterns());
                }
                for (String mapping : mappingCache) {
                    String[] uris = StringUtil.split(uri, Constant.RegularAbout.SLASH);
                    String[] targetParams = StringUtil.split(mapping, Constant.RegularAbout.SLASH);
                    if (uris.length == targetParams.length) {
                        for (int i = 0; i < targetParams.length; i++) {
                            String targetParam = targetParams[i];
                            String value = uris[i];
                            Map<String, String> params = StringUtil.getParamFromMapping(value, targetParam);
                            if (params != null && params.size() > 0) {
                                inParam.putAll(params);
                            }
                        }
                    }
                }
            }
        }
        //将处理过的所有请求参数传入调用服务对象
        return inParam;
    }

    /**
     * 根据path key获取参数
     *
     * @param map 参数集合
     * @param key path key
     * @return 参数
     */
    public static Object getInParam(Map<String, Object> map, String key) {
        return ObjectUtil.pathGet(key, map);
    }

    /**
     * 根据path key获取参数,取空时返回默认值
     *
     * @param map          参数集合
     * @param key          path key
     * @param defaultValue 默认值
     * @return 参数
     */
    public static <T> T getInParam(Map<String, Object> map, String key, T defaultValue) {
        Object value = getInParam(map, key);
        if (value != null) {
            return (T) ObjectUtil.cast(defaultValue.getClass(), value);
        } else {
            return defaultValue;
        }
    }

    /**
     * 根据path key获取参数,并转换成指定类型，取空时返回默认值
     *
     * @param map          参数集合
     * @param key          path key
     * @param clazz        指定转换的类型
     * @param defaultValue 默认值
     * @param <T>          泛型
     * @return 参数
     */
    public static <T> T getInParam(Map<String, Object> map, String key, Class<T> clazz, T defaultValue) {
        T value = getInParam(map, key, clazz);
        if (value != null) {
            return value;
        } else {
            return defaultValue;
        }
    }

    /**
     * 判断path key是否存在
     *
     * @param map 参数集合
     * @param key path key
     * @return 是否存在
     */
    public static boolean containsKey(Map<String, Object> map, String key) {
        Object value = getInParam(map, key);
        return value != null;
    }

    /**
     * 入参转换成map嵌套map/list
     *
     * @param map 未处理的入参集合
     * @return 处理过后的入参集合
     */
    public static Map<String, Object> coverToMap(Map<String, Object> map) {
        Object body = map.get(Constant.ResponseAbout.BODY);
        if (body != null) {
            Map<String, Object> result = new HashMap<>(map);

            String bodyString = body.toString();
            if (bodyString != null) {
                Object json = JSONUtil.jsonCover(JSONUtil.toJSON(bodyString));
                if (json == null) {
                    return null;
                }
                if (Map.class.isAssignableFrom(json.getClass())) {
                    result.putAll((Map<? extends String, ?>) json);
                } else if (List.class.isAssignableFrom(json.getClass())) {
                    result.put(Constant.ResponseAbout.BODY, json);
                }
            }
            return result;
        }
        return map;
    }

    /**
     * 整个入参集转换成指定类型对象
     *
     * @param map   入参集
     * @param clazz 类型
     * @param <T>   泛型
     * @return 参数集和转换后的对象
     */
    public static <T> T getInParam(Map<String, Object> map, Class<T> clazz) {
        T result = null;
        Object json = getInParam(map, Constant.ResponseAbout.BODY);
        if (json != null) {
            result = JSONUtil.toBean(clazz, json.toString());
        }
        if (result == null || ObjectUtil.isAllNullValidity(result)) {
            result = ObjectUtil.getObjectFromMap(clazz, map);
        }
        return result;
    }

    public static <T> T getInParam(Map<String, Object> map, String key, Class<T> clazz) {
        Object result = getInParamOfBody(map, key, clazz);
        if (result == null) {
            result = ObjectUtil.cast(clazz, ObjectUtil.pathGet(key, map));
        }

        return (T) result;
    }

    public static <T> List<T> getInParamOfArray(Map<String, Object> map, String key, Class<T> clazz) {
        List<T> result;
        Object value = ObjectUtil.pathGet(key, map);
        if (value != null && Iterable.class.isAssignableFrom(value.getClass())) {
            result = ArrayUtil.cast(clazz, (Iterable) value);
        } else if (value != null && value.getClass().isArray()) {
            result = ArrayUtil.cast(clazz, ArrayUtil.asList((Object[]) value));
        } else {
            result = ArrayUtil.cast(clazz, new ArrayList<Object>() {{
                add(value);
            }});
        }
        return result;
    }

    public static <T> T getInParamOfBody(Map<String, Object> map, String key, TypeReference<T> reference) {
        T result = null;
        Object value = getInParamOfBody(map, key);
        if (value != null && JSON.class.isAssignableFrom(value.getClass())) {
            result = JSONUtil.parseObject(JSONUtil.toJSONString(value), reference);
        }
        return result;
    }

    public static <T> T getInParamOfBody(Map<String, Object> map, String key, Class<T> clazz) {
        T result;
        Object value = getInParamOfBody(map, key);
        if (value != null && JSON.class.isAssignableFrom(value.getClass())) {
            result = JSONUtil.toJavaObject((JSON) value, clazz);
        } else {
            result = ObjectUtil.cast(clazz, value);
        }
        return result;
    }

    /**
     * 从body参数中获取path参数
     *
     * @param map 入参集合
     * @param key path
     * @return 数据
     */
    private static Object getInParamOfBody(Map<String, Object> map, String key) {
        Object result = null;
        if (map.containsKey(Constant.ResponseAbout.BODY)) {
            Object jsonString = map.get(Constant.ResponseAbout.BODY);
            JSON json = JSONUtil.toJSON(jsonString);
            String prefix = Constant.ResponseAbout.BODY + Constant.RegularAbout.SPOT;
            if (key.startsWith(prefix)) {
                key = key.replaceFirst(prefix, Constant.RegularAbout.BLANK);
            }
            result = JSONPath.eval(json, key);
        }
        return result;
    }

    /**
     * 格式化响应报文
     *
     * @param head   头信息
     * @param result 体信息
     * @return 格式化后的ModelAndView
     */
    public static ModelAndView getResponseFormatData(Head head, Object result) {
        ModelAndView modelAndView = new ModelAndView();
        AbstractResponseFormat abstractResponseFormat = FactoryUtil.getBean(AbstractResponseFormat.class);
        if (abstractResponseFormat != null) {
            modelAndView = abstractResponseFormat.buildResponse(head, result);
        } else {
            if (head != null) {
                modelAndView.addObject(Constant.ResponseAbout.HEAD, head);
            }
            if (result != null && Map.class.isAssignableFrom(result.getClass())) {
                modelAndView.addAllObjects((Map<String, ?>) result);
            } else {
                modelAndView.addObject(Constant.ResponseAbout.RESULT, result);
            }
        }
        return modelAndView;
    }

    /**
     * 入参验证
     *
     * @param service 服务实例
     * @param method  方法
     * @return 验证信息集
     */
    public static List<ValidateMsg> handleInParamValidate(ServiceInterface service, Method method) {
        Optional<Validates> validatesOptional = Optional.ofNullable(method.getAnnotation(Validates.class));
        Optional<Validate> validateOptional = Optional.ofNullable(method.getAnnotation(Validate.class));

        List<Validate> validateList = Lists.newArrayList();
        validatesOptional.ifPresent(validates -> validateList.addAll(Stream.of(validates.value()).collect(Collectors.toList())));
        validateOptional.ifPresent(validateList::add);

        return validateList.stream().map(validate -> handleValidateAnnotation(service, validate)).reduce((all, validateMsgList) -> {
            all.addAll(validateMsgList);
            return all;
        }).orElse(Lists.newArrayList());
    }

    /**
     * 根据参数验证注解取验证信息集
     *
     * @param v Validate注解
     * @return 验证信息集
     */
    private static List<ValidateMsg> handleValidateAnnotation(ServiceInterface service, Validate v) {
        List<ValidateMsg> list = new ArrayList<>();

        if (v == null) {
            return list;
        }

        String key = v.value().trim();
        Object value;
        if (StringUtil.isBlank(key)) {
            value = service.getInParam();
        } else {
            value = service.getInParam(key);
        }

        ValidateType validateType = v.validateType();
        if (value != null && List.class.isAssignableFrom(value.getClass())) {
            list.addAll(validateType.validateArray(key, (List) value, v));
        } else {
            list.addAll(validateType.validateParam(key, value, v));
        }
        return list;
    }

    /**
     * 参数校验信息根据相同参数聚合错误信息
     *
     * @param list 聚合之前的错误信息
     * @return 聚合后的信息
     */
    public static Optional<List<ValidateMsg>> aggregation(List<ValidateMsg> list) {

        List<ValidateMsg> errors = list.parallelStream().filter(validateMsg -> !validateMsg.isState()).collect(Collectors.toList());

        Map<String, ValidateMsg> cache = Maps.newHashMapWithExpectedSize(errors.size());

        errors.forEach(validateMsg -> {
            String key = validateMsg.getItem();
            if (cache.containsKey(key)) {
                cache.get(key).addMessage(validateMsg.getMessage());
            } else {
                cache.put(key, validateMsg);
            }
        });
        if (cache.size() > 0) {
            return Optional.of(new ArrayList<>(cache.values()));
        }
        return Optional.empty();
    }
}
