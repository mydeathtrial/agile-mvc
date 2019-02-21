package com.agile.common.mvc.controller;

import com.agile.common.annotation.ApiMethod;
import com.agile.common.annotation.Mapping;
import com.agile.common.annotation.Validate;
import com.agile.common.annotation.Validates;
import com.agile.common.base.AbstractResponseFormat;
import com.agile.common.base.ApiInfo;
import com.agile.common.base.Constant;
import com.agile.common.base.Head;
import com.agile.common.base.RETURN;
import com.agile.common.base.RequestWrapper;
import com.agile.common.exception.NoSuchRequestMethodException;
import com.agile.common.exception.NoSuchRequestServiceException;
import com.agile.common.exception.UnlawfulRequestException;
import com.agile.common.mvc.service.ServiceInterface;
import com.agile.common.util.ApiUtil;
import com.agile.common.util.ArrayUtil;
import com.agile.common.util.FactoryUtil;
import com.agile.common.util.FileUtil;
import com.agile.common.util.ServletUtil;
import com.agile.common.util.StringUtil;
import com.agile.common.validate.ValidateMsg;
import com.agile.common.validate.ValidateType;
import com.agile.common.view.ForwardView;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 主控制层
 *
 * @author 佟盟 on 2017/8/22
 */
@Controller
public class MainController {

    /**
     * 服务缓存变量
     */
    private static ThreadLocal<ServiceInterface> service = new ThreadLocal<>();

    /**
     * 方法缓存变量
     */
    private static ThreadLocal<Method> method = new ThreadLocal<>();

    /**
     * request缓存变量
     */
    private static ThreadLocal<HttpServletRequest> request = new ThreadLocal<>();

    /**
     * 非法请求处理器
     *
     * @throws UnlawfulRequestException 非法路径请求
     */
    @RequestMapping(value = {"/", "/*", "/*/*/*/**"})
    public Object othersProcessor(HttpServletRequest currentRequest, HttpServletResponse currentResponse) throws Throwable {
        //清理缓存
        clear();

        //设置当前request
        request.set(currentRequest);

        if (initApiInfoByRequestMapping()) {
            throw new UnlawfulRequestException();
        }
        return processor(currentRequest, currentResponse);
    }

    @RequestMapping(value = {"/{resource}"}, method = RequestMethod.GET)
    public Object processorOfGET0(HttpServletRequest currentRequest, HttpServletResponse currentResponse, @PathVariable String resource) throws Throwable {
        request.set(currentRequest);
        if (initApiInfoByRequestMapping()) {
            return processor(currentRequest, currentResponse, StringUtil.removeExtension(resource), "query");
        }
        return processor(currentRequest, currentResponse);
    }

    @RequestMapping(value = {"/{resource}/{id}"}, method = RequestMethod.GET)
    public Object processorOfGET1(HttpServletRequest currentRequest, HttpServletResponse currentResponse, @PathVariable String resource, @PathVariable String id) throws Throwable {
        request.set(currentRequest);
        if (initApiInfoByRequestMapping()) {
            RequestWrapper requestWrapper = new RequestWrapper(currentRequest);
            requestWrapper.addParameter("id", id);
            return processor(requestWrapper, currentResponse, StringUtil.removeExtension(resource), "queryById");
        }
        return processor(currentRequest, currentResponse);
    }

    @RequestMapping(value = {"/{resource}/page/{page}/{size}"}, method = RequestMethod.GET)
    public Object processorOfGET2(HttpServletRequest currentRequest, HttpServletResponse currentResponse, @PathVariable String resource, @PathVariable String page, @PathVariable String size) throws Throwable {
        request.set(currentRequest);
        if (initApiInfoByRequestMapping()) {
            RequestWrapper requestWrapper = new RequestWrapper(currentRequest);
            requestWrapper.addParameter("page", page);
            requestWrapper.addParameter("size", size);
            return processor(requestWrapper, currentResponse, StringUtil.removeExtension(resource), "pageQuery");
        }
        return processor(currentRequest, currentResponse);
    }

    @RequestMapping(value = {"/{resource}"}, method = RequestMethod.POST)
    public Object processorOfPOST(HttpServletRequest currentRequest, HttpServletResponse currentResponse, @PathVariable String resource) throws Throwable {
        request.set(currentRequest);
        if (initApiInfoByRequestMapping()) {
            return processor(currentRequest, currentResponse, StringUtil.removeExtension(resource), "save");
        }
        return processor(currentRequest, currentResponse);
    }

    @RequestMapping(value = {"/{resource}/{id}"}, method = RequestMethod.PUT)
    public Object processorOfPUT(HttpServletRequest currentRequest, HttpServletResponse currentResponse, @PathVariable String resource, @PathVariable String id) throws Throwable {
        request.set(currentRequest);
        if (initApiInfoByRequestMapping()) {
            RequestWrapper requestWrapper = new RequestWrapper(currentRequest);
            requestWrapper.addParameter("id", id);
            return processor(requestWrapper, currentResponse, StringUtil.removeExtension(resource), "update");
        }
        return processor(currentRequest, currentResponse);
    }

    @RequestMapping(value = {"/{resource}"}, method = RequestMethod.DELETE)
    public Object processorOfDELETE(HttpServletRequest currentRequest, HttpServletResponse currentResponse, @PathVariable String resource) throws Throwable {
        request.set(currentRequest);
        if (initApiInfoByRequestMapping()) {
            return processor(currentRequest, currentResponse, StringUtil.removeExtension(resource), "delete");
        }
        return processor(currentRequest, currentResponse);
    }

    @RequestMapping(value = {"/{resource}/{id}"}, method = RequestMethod.DELETE)
    public Object processorOfDELETE(HttpServletRequest currentRequest, HttpServletResponse currentResponse, @PathVariable String resource, @PathVariable String id) throws Throwable {
        request.set(currentRequest);
        if (initApiInfoByRequestMapping()) {
            RequestWrapper requestWrapper = new RequestWrapper(currentRequest);
            requestWrapper.addParameter("id", id);
            return processor(requestWrapper, currentResponse, StringUtil.removeExtension(resource), "delete");
        }
        return processor(currentRequest, currentResponse);
    }

    /**
     * agile框架处理器
     *
     * @param service         服务名
     * @param method          方法名
     * @param currentRequest  request信息
     * @param currentResponse response信息
     * @return 响应试图数据
     * @throws Throwable 所有异常
     */
    @RequestMapping(value = {"/api/{service}/{method}", "/api/{service}/{method}/**", "/{service}/{method}"})
    public Object processor(
            HttpServletRequest currentRequest,
            HttpServletResponse currentResponse,
            @PathVariable String service,
            @PathVariable String method
    ) throws Throwable {
        //清理缓存
        clear();

        //设置当前request
        request.set(currentRequest);

        //处理目标API
        if (initApiInfoByRequestMapping()) {
            initService(StringUtil.toLowerName(service));
            initMethod(StringUtil.toLowerName(method));
        }

        return processor(currentRequest, currentResponse);
    }

    private Object processor(HttpServletRequest currentRequest, HttpServletResponse currentResponse) throws Throwable {

        //处理入参
        handleInParam();

        //入参验证
        List<ValidateMsg> validateMessages = handleInParamValidate();
        if (validateMessages != null && validateMessages.size() > 0) {
            assert RETURN.PARAMETER_ERROR != null;
            return getResponseFormatData(new Head(RETURN.PARAMETER_ERROR), validateMessages.toArray());
        }

        //调用目标方法
        Object returnData = getService().executeMethod(getService(), getMethod(), currentRequest, currentResponse);

        //获取出参
        Map<String, Object> outParam = getService().getOutParam();

        //判断是否跳转
        if (outParam.containsKey(Constant.RegularAbout.FORWARD)) {
            return jump(Constant.RegularAbout.FORWARD);
        }
        if (outParam.containsKey(Constant.RegularAbout.REDIRECT)) {
            return jump(Constant.RegularAbout.REDIRECT);
        }

        //获取格式化后的报文
        if (returnData instanceof AbstractResponseFormat) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addAllObjects((AbstractResponseFormat) returnData);
            return modelAndView;
        }
        ModelAndView modelAndView = getResponseFormatData(returnData instanceof RETURN ? new Head((RETURN) returnData) : null, getService().getOutParam());

        //清理缓存
        clear();

        return modelAndView;
    }

    /**
     * 格式化响应报文
     *
     * @param head   头信息
     * @param result 体信息
     * @return 格式化后的ModelAndView
     */
    private ModelAndView getResponseFormatData(Head head, Object result) {
        ModelAndView modelAndView = new ModelAndView();
        AbstractResponseFormat abstractResponseFormat = FactoryUtil.getBean(AbstractResponseFormat.class);
        if (abstractResponseFormat != null) {
            modelAndView = abstractResponseFormat.buildResponse(head, result);
        } else {
            if (head != null) {
                modelAndView.addObject(Constant.ResponseAbout.HEAD, head);
            }
            if (Map.class.isAssignableFrom(result.getClass())) {
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
     * @return 验证信息集
     * @throws InstantiationException 异常
     * @throws IllegalAccessException 异常
     */
    private List<ValidateMsg> handleInParamValidate() throws InstantiationException, IllegalAccessException {
        List<ValidateMsg> list = null;
        Validates vs = getMethod().getAnnotation(Validates.class);
        if (vs != null) {
            list = handleValidateAnnotation(vs);
        }
        Validate v = getMethod().getAnnotation(Validate.class);
        if (v != null) {
            List<ValidateMsg> rs = handleValidateAnnotation(v);
            if (rs != null) {
                if (list != null) {
                    list.addAll(rs);
                }
                list = rs;
            }
        }
        return list;
    }

    /**
     * 根据参数验证注解取验证信息集
     *
     * @param v Validate注解
     * @return 验证信息集
     * @throws IllegalAccessException 异常
     * @throws InstantiationException 异常
     */
    private List<ValidateMsg> handleValidateAnnotation(Validate v) throws IllegalAccessException, InstantiationException {
        if (v == null) {
            return null;
        }
        if (StringUtil.isBlank(v.value()) && v.beanClass() == Class.class) {
            return null;
        }
        String key = v.value().trim();
        Object value;
        if (StringUtil.isBlank(key)) {
            value = getService().getInParam();
        } else {
            value = getService().getInParam().get(key);
            if (value == null) {
                Object body = getService().getInParam().get(Constant.ResponseAbout.BODY);
                if (body != null) {
                    value = ((JsonNode) body).asText();
                }
            }
        }

        List<ValidateMsg> list = new ArrayList<>();

        Class<?> beanClass = v.beanClass();
        if (beanClass != Class.class) {
            Object bean = StringUtil.isBlank(key) ? getService().getInParam(beanClass) : getService().getInParam(key, beanClass);
            if (bean == null) {
                bean = v.beanClass().newInstance();
            } else {
                getService().setInParam(StringUtil.toLowerName(beanClass.getSimpleName()), bean);
            }
            ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
            Validator validator = validatorFactory.getValidator();
            Set<ConstraintViolation<Object>> set = validator.validate(bean, v.validateGroups());
            if (set != null && set.size() > 0) {
                list = new ArrayList<>();
            } else {
                return null;
            }
            for (ConstraintViolation<Object> m : set) {
                ValidateMsg r = new ValidateMsg(m.getMessage(), false, StringUtil.isBlank(key) ? m.getPropertyPath().toString() : String.format("%s.%s", key, m.getPropertyPath()), m.getInvalidValue());
                if (r.isState()) {
                    continue;
                }
                list.add(r);
            }
            return list;
        }

        ValidateType validateType = v.validateType();
        if (value != null && value.getClass().isArray()) {
            List<ValidateMsg> rs = validateType.validateArray(key, (String[]) value, v);

            if (rs != null) {
                for (ValidateMsg validateMsg : rs) {
                    if (validateMsg.isState()) {
                        continue;
                    }
                    list.add(validateMsg);
                }
            }
        } else {
            ValidateMsg r = validateType.validateParam(key, value, v);
            if (r != null && !r.isState()) {
                list = new ArrayList<>();
                list.add(r);
            }
        }
        return list;
    }

    /**
     * 根据参数验证集注解取验证信息集
     *
     * @param vs Validates注解
     * @return 验证信息集
     * @throws InstantiationException 异常
     * @throws IllegalAccessException 异常
     */
    private List<ValidateMsg> handleValidateAnnotation(Validates vs) throws InstantiationException, IllegalAccessException {
        List<ValidateMsg> list = null;
        for (Validate v : vs.value()) {
            List<ValidateMsg> r = handleValidateAnnotation(v);
            if (r != null) {
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.addAll(r);
            }
        }
        return list;
    }

    /**
     * 由于线程池的使用与threadLocal冲突,前后需要清理缓存
     */
    private void clear() {
        service.remove();
        method.remove();
        request.remove();
    }

    /**
     * 转发
     *
     * @param jumpMethod 跳转方式
     * @return 视图
     */
    private ModelAndView jump(String jumpMethod) {
        Map<String, Object> outParam = getService().getOutParam();
        Map<String, Object> inParam = getService().getInParam();

        ModelAndView model = new ModelAndView(exposeJumpUrl(jumpMethod, outParam));
        model.addAllObjects(outParam);
        model.addAllObjects(inParam);
        return model;
    }

    /**
     * 处理跳转地址及参数
     *
     * @param jumpMethod 跳转方式
     * @param outParam   跳转之前的输出参数
     * @return 用于跳转的目标地址
     */
    private String exposeJumpUrl(String jumpMethod, Map<String, Object> outParam) {
        //获取跳转地址
        String resourceUrl = outParam.get(jumpMethod).toString();

        StringBuilder url = new StringBuilder(jumpMethod + Constant.RegularAbout.COLON);
        //补充斜杠
        if (!resourceUrl.startsWith(Constant.RegularAbout.HTTP) && !resourceUrl.startsWith(Constant.RegularAbout.SLASH)) {
            url.append(Constant.RegularAbout.SLASH);
        }
        url.append(resourceUrl);
        //补充问号
        if (!resourceUrl.contains(Constant.RegularAbout.QUESTION_MARK)) {
            url.append(Constant.RegularAbout.QUESTION_MARK);
        }
        //移除本跳转防止死循环
        outParam.remove(jumpMethod);
        return url.toString();
    }

    /**
     * 判断请求方法是否合法
     *
     * @param requestMethods 允许方法
     * @param requestMethod  请求方法
     * @return 是/否
     */
    private boolean allowRequestMethod(RequestMethod[] requestMethods, RequestMethod requestMethod) {
        return requestMethods == null || requestMethods.length <= 0 || !ArrayUtil.contains(requestMethods, requestMethod);
    }

    /**
     * 根据requestMapping信息初始化目标method信息
     *
     * @return 成功/失败
     */
    private boolean initApiInfoByRequestMapping() {
        HttpServletRequest currentRequest = request.get();
        ApiInfo info = ApiUtil.getApiCache(currentRequest);
        if (info != null) {
            Object bean = info.getBean();
            Method targetMethod = info.getMethod();
            try {
                initServiceByObject(bean);
            } catch (NoSuchRequestServiceException e) {
                return true;
            }
            initMethodByObject(targetMethod);
            return false;
        }
        return true;
    }

    /**
     * 根据服务名在Spring上下文中获取服务bean
     *
     * @param o 对象
     */
    private void initServiceByObject(Object o) throws NoSuchRequestServiceException {
        try {
            service.set((ServiceInterface) o);
        } catch (Exception e) {
            throw new NoSuchRequestServiceException();
        }
    }

    /**
     * 根据服务名在Spring上下文中获取服务bean
     *
     * @param serviceName 服务名
     */
    private void initService(String serviceName) throws NoSuchRequestServiceException {
        Object o = FactoryUtil.getBean(serviceName);
        if (o == null || !ServiceInterface.class.isAssignableFrom(o.getClass())) {
            throw new NoSuchRequestServiceException();
        }
        service.set((ServiceInterface) o);
    }

    /**
     * 根据服务名在Spring上下文中获取服务bean
     *
     * @param o 方法
     */
    private void initMethodByObject(Method o) {
        method.set(o);
    }

    /**
     * 根据方法名初始化目标方法
     *
     * @param methodName 方法名
     */
    private void initMethod(String methodName) throws NoSuchRequestMethodException {
        Method methodCache;
        try {
            methodCache = getService().getClass().getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            throw new NoSuchRequestMethodException();
        }
        RequestMethod currentRequestMethod = RequestMethod.valueOf(request.get().getMethod());
        Mapping requestMapping = methodCache.getAnnotation(Mapping.class);
        if (requestMapping != null && allowRequestMethod(requestMapping.method(), currentRequestMethod)) {
            throw new NoSuchRequestMethodException();
        }
        ApiMethod apiMethod = methodCache.getAnnotation(ApiMethod.class);
        if (apiMethod != null && allowRequestMethod(apiMethod.value(), currentRequestMethod)) {
            throw new NoSuchRequestMethodException();
        }
        methodCache.setAccessible(true);
        method.set(methodCache);
    }

    /**
     * 根据servlet请求、认证信息、目标服务名、目标方法名处理入参
     */
    private void handleInParam() {
        final int length = 16;
        getService().initInParam();
        HttpServletRequest currentRequest = request.get();
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
            inParam.putAll(FileUtil.getFileFormRequest(currentRequest));
        } else {
            Map<String, Object> bodyParam = ServletUtil.getBody(currentRequest);
            if (bodyParam != null) {
                inParam.putAll(bodyParam);
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
        String extension = UriUtils.extractFileExtension(uri);
        if ("json".equals(extension) || "xml".equals(extension) || "plain".equals(extension)) {
            uri = uri.replaceAll("." + extension, "");
        }
        ApiInfo info = ApiUtil.getApiCache(currentRequest);

        //处理路径入参
        if (info != null) {
            RequestMappingInfo requestMappingInfo = info.getRequestMappingInfo();
            if (requestMappingInfo != null) {
                Set<String> mappingCache = requestMappingInfo.getPatternsCondition().getPatterns();
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
        getService().setInParam(inParam);
    }

    /**
     * 获取当前线程下Service缓存
     */
    private ServiceInterface getService() {
        return service.get();
    }

    /**
     * 获取当前线程下方法缓存
     */
    private Method getMethod() {
        return method.get();
    }
}
