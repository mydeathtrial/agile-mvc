package com.agile.common.mvc.controller;

import com.agile.common.annotation.Mapping;
import com.agile.common.annotation.Validate;
import com.agile.common.annotation.Validates;
import com.agile.common.validate.ValidateType;
import com.agile.common.base.*;
import com.agile.common.exception.NoSuchRequestMethodException;
import com.agile.common.exception.NoSuchRequestServiceException;
import com.agile.common.exception.UnlawfulRequestException;
import com.agile.common.mvc.service.ServiceInterface;
import com.agile.common.util.*;
import com.agile.common.validate.ValidateMsg;
import com.agile.common.view.ForwardView;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
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
import java.util.*;

/**
 * 主控制层
 * Created by 佟盟 on 2017/8/22
 */
@Controller
public class MainController {

    //服务缓存变量
    private static ThreadLocal<ServiceInterface> service = new ThreadLocal<>();

    //方法缓存变量
    private static ThreadLocal<Method> method = new ThreadLocal<>();

    //request缓存变量
    private static ThreadLocal<HttpServletRequest> request = new ThreadLocal<>();

    /**
     * 非法请求处理器
     */
    @RequestMapping(value = {"/","/*","/*/*/*/**"})
    public void processor() throws UnlawfulRequestException {
        throw new UnlawfulRequestException();
    }

    @RequestMapping(value = {"/swaggerInfo"})
    public Object getSwaggerInfo(){
        return APIUtil.getApi();
    }

    @RequestMapping(value = {"/{resource}"},method = RequestMethod.GET)
    public Object processorOfGET0(HttpServletRequest currentRequest,HttpServletResponse currentResponse,@PathVariable String resource) throws Throwable {
        return processor(currentRequest,currentResponse,resource,"query");
    }

    @RequestMapping(value = {"/{resource}/{id}"},method = RequestMethod.GET)
    public Object processorOfGET1(HttpServletRequest currentRequest,HttpServletResponse currentResponse,@PathVariable String resource,@PathVariable String id) throws Throwable {
        RequestWrapper requestWrapper = new RequestWrapper(currentRequest);
        requestWrapper.addParameter("id",id);
        return processor(requestWrapper,currentResponse,resource,"queryById");
    }

    @RequestMapping(value = {"/{resource}/page/{page}/{size}"},method = RequestMethod.GET)
    public Object processorOfGET2(HttpServletRequest currentRequest,HttpServletResponse currentResponse,@PathVariable String resource,@PathVariable String page,@PathVariable String size) throws Throwable {
        RequestWrapper requestWrapper = new RequestWrapper(currentRequest);
        requestWrapper.addParameter("page",page);
        requestWrapper.addParameter("size",size);
        return processor(requestWrapper,currentResponse,resource,"pageQuery");
    }

    @RequestMapping(value = {"/{resource}"},method = RequestMethod.POST)
    public Object processorOfPOST(HttpServletRequest currentRequest,HttpServletResponse currentResponse,@PathVariable String resource) throws Throwable {
        return processor(currentRequest,currentResponse,resource,"save");
    }

    @RequestMapping(value = {"/{resource}/{id}"},method = RequestMethod.PUT)
    public Object processorOfPUT(HttpServletRequest currentRequest,HttpServletResponse currentResponse,@PathVariable String resource,@PathVariable String id) throws Throwable {
        RequestWrapper requestWrapper = new RequestWrapper(currentRequest);
        requestWrapper.addParameter("id",id);
        return processor(requestWrapper,currentResponse,resource,"update");
    }

    @RequestMapping(value = {"/{resource}"},method = RequestMethod.DELETE)
    public Object processorOfDELETE(HttpServletRequest currentRequest,HttpServletResponse currentResponse,@PathVariable String resource) throws Throwable {
        return processor(currentRequest,currentResponse,resource,"delete");
    }

    @RequestMapping(value = {"/{resource}/{id}"},method = RequestMethod.DELETE)
    public Object processorOfDELETE(HttpServletRequest currentRequest,HttpServletResponse currentResponse,@PathVariable String resource,@PathVariable String id) throws Throwable {
        RequestWrapper requestWrapper = new RequestWrapper(currentRequest);
        requestWrapper.addParameter("id",id);
        return processor(requestWrapper,currentResponse,resource,"delete");
    }

    /**
     * agile框架处理器
     * @param service 服务名
     * @param method 方法名
     * @return 响应试图数据
     */
    @RequestMapping(value = {"/api/{service}/{method}","/api/{service}/{method}/**"})
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
        if(!initMethodByRequestMapping()){
            initService(StringUtil.toLowerName(service));
            initMethod(StringUtil.toLowerName(method));
        }

        //处理入参
        handleInParam();

        //入参验证
        List<ValidateMsg> validateMsgs = handleInParamValidate();
        if(validateMsgs !=null && validateMsgs.size()>0){
            assert RETURN.PARAMETER_ERROR != null;
            return getResponseFormatData(new Head(RETURN.PARAMETER_ERROR),validateMsgs.toArray());
        }

        //调用目标方法
        Object returnData = getService().executeMethod(getService(),getMethod(),currentRequest,currentResponse);

        //获取出参
        Map<String, Object> outParam = getService().getOutParam();

        //判断是否跳转
        if(outParam.containsKey(Constant.RegularAbout.FORWARD)){
            return jump(Constant.RegularAbout.FORWARD);
        }
        if(outParam.containsKey(Constant.RegularAbout.REDIRECT)){
            return jump(Constant.RegularAbout.REDIRECT);
        }

        //获取格式化后的报文
        if(AbstractResponseFormat.class.isAssignableFrom(returnData.getClass())){
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addAllObjects((AbstractResponseFormat) returnData);
            return modelAndView;
        }
        ModelAndView modelAndView = getResponseFormatData(returnData instanceof RETURN ?new Head((RETURN) returnData):null,getService().getOutParam());

        //清理缓存
        clear();

        return modelAndView;
    }

    /**
     * 格式化响应报文
     * @param head 头信息
     * @param result 体信息
     * @return 格式化后的ModelAndView
     */
    private ModelAndView getResponseFormatData(Head head,Object result){
        ModelAndView modelAndView = new ModelAndView();
        AbstractResponseFormat abstractResponseFormat = FactoryUtil.getBean(AbstractResponseFormat.class);
        if(abstractResponseFormat!=null){
            modelAndView = abstractResponseFormat.buildResponse(head,result);
        }else{
            if(head!=null)
            modelAndView.addObject(Constant.ResponseAbout.HEAD, head);
            if(Map.class.isAssignableFrom(result.getClass())){
                modelAndView.addAllObjects((Map)result);
            }else{
                modelAndView.addObject(Constant.ResponseAbout.RESULT,result);
            }
        }
        return modelAndView;
    }

    /**
     * 入参验证
     * @return 验证信息集
     */
    private List<ValidateMsg> handleInParamValidate() throws InstantiationException, IllegalAccessException {
        List<ValidateMsg> list = null;
        Validates vs = getMethod().getAnnotation(Validates.class);
        if(vs != null){
            list = handleValidateAnnotation(vs);
        }
        Validate v = getMethod().getAnnotation(Validate.class);
        if(v != null){
            List<ValidateMsg> rs = handleValidateAnnotation(v);
            if(rs!=null){
                if(list!=null)
                list.addAll(rs);
                list = rs;
            }
        }
        return list;
    }

    /**
     * 根据参数验证注解取验证信息集
     * @param v Validate注解
     * @return 验证信息集
     */
    private List<ValidateMsg> handleValidateAnnotation(Validate v) throws IllegalAccessException, InstantiationException {
        if(v==null || v.value().equals(""))return null;
        String key = v.value();
        Object value = getService().getInParam().get(key);
        List<ValidateMsg> list = null;

        if(v.beanClass() != Class.class){
            Object bean = getService().getInParam(key, v.beanClass());
            if(bean==null){
                bean = v.beanClass().newInstance();
            }else{
                getService().setInParam(key,bean);
            }
            ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
            Validator validator = validatorFactory.getValidator();
            Set<ConstraintViolation<Object>> set = validator.validate(bean,v.validateGroups());
            if(set != null && set.size()>0){
                list = new ArrayList<>();
            }else{
                return null;
            }
            for (ConstraintViolation<Object> m:set) {
                ValidateMsg r = new ValidateMsg(m.getMessage(),false,String.format("%s.%s",key,m.getPropertyPath()),m.getInvalidValue());
                list.add(r);
            }
            return list;
        }

        ValidateType validateType = v.validateType();
        if(value!= null && value.getClass().isArray()){
            List<ValidateMsg> rs = validateType.validateArray(key, (String[]) value,v);
            if(rs != null)
            list = rs;
        }else{
            ValidateMsg r = validateType.validateParam(key,value,v);
            if(r != null){
                list = new ArrayList<>();
                list.add(r);
            }
        }
        return list;
    }

    /**
     * 根据参数验证集注解取验证信息集
     * @param vs Validates注解
     * @return 验证信息集
     */
    private List<ValidateMsg> handleValidateAnnotation(Validates vs) throws InstantiationException, IllegalAccessException {
        List<ValidateMsg> list = null;
        for (Validate v:vs.value()) {
            List<ValidateMsg> r = handleValidateAnnotation(v);
            if(r != null){
                if(list == null){
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
    private void clear(){
        service.remove();
        request.remove();
    }

    /**
     * 转发
     * @param jumpMethod 跳转方式
     */
    private ModelAndView jump(String jumpMethod){
        Map<String, Object> outParam = getService().getOutParam();
        Map<String, Object> inParam = getService().getInParam();

        ModelAndView model = new ModelAndView(exposeJumpUrl(jumpMethod,outParam));
        model.addAllObjects(outParam);
        model.addAllObjects(inParam);
        return model;
    }

    /**
     * 处理跳转地址及参数
     * @param jumpMethod 跳转方式
     * @param outParam 跳转之前的输出参数
     * @return 用于跳转的目标地址
     */
    private String exposeJumpUrl(String jumpMethod,Map<String, Object> outParam){
        //获取跳转地址
        String resourceUrl = outParam.get(jumpMethod).toString();

        StringBuilder url = new StringBuilder(jumpMethod+Constant.RegularAbout.COLON);
        //补充斜杠
        if(!resourceUrl.startsWith(Constant.RegularAbout.HTTP) && !resourceUrl.startsWith(Constant.RegularAbout.SLASH)){
            url.append(Constant.RegularAbout.SLASH);
        }
        url.append(resourceUrl);
        //补充问号
        if(!resourceUrl.contains(Constant.RegularAbout.QUESTION_MARK)){
            url.append(Constant.RegularAbout.QUESTION_MARK);
        }
        //移除本跳转防止死循环
        outParam.remove(jumpMethod);
        return url.toString();
    }

    private boolean includeMethod(Mapping requestMapping, RequestMethod requestMethod){
        if(requestMapping!=null && requestMapping.method().length>0 && ArrayUtil.contains(requestMapping.method(),requestMethod)){
            return true;
        }
        return false;
    }
    /**
     * 根据requestMapping信息初始化目标method信息
     * @return 成功/失败
     */
    private boolean initMethodByRequestMapping(){
        HttpServletRequest currentRequest = request.get();
        HandlerMethod info = APIUtil.getApiCache(currentRequest);
        if(info!=null){
            Object bean = info.getBean();
            Method method = info.getMethod();
            try {
                initServiceByObject(bean);
            } catch (NoSuchRequestServiceException e) {
                return false;
            }
            initMethodByObject(method);
            return true;
        }
        return false;
    }

    /**
     * 根据服务名在Spring上下文中获取服务bean
     */
    private void initServiceByObject(Object o)throws NoSuchRequestServiceException {
        try {
            service.set((ServiceInterface) o);
        }catch (Exception e){
            throw new NoSuchRequestServiceException();
        }
    }

    /**
     * 根据服务名在Spring上下文中获取服务bean
     * @param serviceName   服务名
     */
    private void initService(String serviceName)throws NoSuchRequestServiceException {
        try {
            Object o = APIUtil.getServiceCache(serviceName);
            if(o==null){
                o = FactoryUtil.getBean(serviceName);
            }
            service.set((ServiceInterface) o);
        }catch (Exception e){
            throw new NoSuchRequestServiceException();
        }
    }

    /**
     * 根据服务名在Spring上下文中获取服务bean
     */
    private void initMethodByObject(Method o) {
        method.set(o);
    }

    /**
     * 根据方法名初始化目标方法
     * @param methodName 方法名
     */
    private void initMethod(String methodName) throws NoSuchRequestMethodException {
        try {
            Method methodCache = getService().getClass().getMethod(methodName);
            Mapping requestMapping = methodCache.getAnnotation(Mapping.class);
            if(requestMapping!=null){
                if(!includeMethod(requestMapping,RequestMethod.valueOf(request.get().getMethod()))){
                    throw new NoSuchRequestMethodException();
                }
            }
            methodCache.setAccessible(true);
            method.set(methodCache);
        }catch (Exception e){
            throw new NoSuchRequestMethodException();
        }
    }

    /**
     * 根据servlet请求、认证信息、目标服务名、目标方法名处理入参
     */
    private void handleInParam() {
        getService().initInParam();
        HttpServletRequest currentRequest = request.get();
        Map<String,Object> inParam = new HashMap<>();

        Map<String, String[]> parameterMap = currentRequest.getParameterMap();
        if (parameterMap.size()>0){
            for (Map.Entry<String,String[]> map:parameterMap.entrySet() ) {
                String[] v = map.getValue();
                if(v.length==1){
                    inParam.put(map.getKey(),v[0]);
                }else{
                    inParam.put(map.getKey(),v);
                }
            }
        }

        if (currentRequest instanceof RequestWrapper){
            Map<String, String[]> forwardMap = ((RequestWrapper) currentRequest).getForwardParameterMap();
            for (Map.Entry<String,String[]> map:forwardMap.entrySet() ) {
                inParam.put(map.getKey(),map.getValue());
            }
        }

        //判断是否存在文件上传
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(currentRequest.getSession().getServletContext());
        if (multipartResolver.isMultipart(currentRequest)){
            inParam.putAll(FileUtil.getFileFormRequest(currentRequest));
        }else{
            Map<String, Object> bodyParam = ServletUtil.getBody(currentRequest);
            if(bodyParam!=null){
                inParam.putAll(bodyParam);
            }
        }

        Enumeration<String> attributeNames = currentRequest.getAttributeNames();
        while (attributeNames.hasMoreElements()){
            String key = attributeNames.nextElement();
            String PREFIX = ForwardView.getPrefix();
            if(key.startsWith(PREFIX)){
                inParam.put(key.replace(PREFIX,""),currentRequest.getAttribute(key));
            }
        }

        //处理Mapping参数
        String uri = currentRequest.getRequestURI();
        String extension = UriUtils.extractFileExtension(uri);
        if("json".equals(extension) || "xml".equals(extension) || "plain".equals(extension))
        uri = uri.replaceAll("."+extension,"");
        HandlerMethod info = APIUtil.getApiCache(currentRequest);

        if(info!=null){
            Object bean = info.getBean();
            Method method = info.getMethod();
            RequestMappingInfo requestMappingInfo = APIUtil.getMappingHandlerMapping().getMappingForMethod(method, bean.getClass());
            if(requestMappingInfo!=null){
                Set<String> mappingCache = requestMappingInfo.getPatternsCondition().getPatterns();
                for (String mapping:mappingCache){
                    String[] uris = StringUtil.split(uri,Constant.RegularAbout.SLASH);
                    String[] targetParams = StringUtil.split(mapping, Constant.RegularAbout.SLASH);
                    int i = 0;
                    for(String targetParam:targetParams){
                        if(StringUtil.findMatchedString(Constant.RegularAbout.URL_PARAM, targetParam)){
                            String key = StringUtil.getMatchedString(Constant.RegularAbout.URL_PARAM, targetParam, 0);
                            String value = uris[i];
                            inParam.put(key,value);
                        }
                        i++;
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
    private Method getMethod(){
        return method.get();
    }
}
