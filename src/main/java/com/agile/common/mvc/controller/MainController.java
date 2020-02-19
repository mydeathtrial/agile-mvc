package com.agile.common.mvc.controller;

import com.agile.common.annotation.ApiMethod;
import com.agile.common.annotation.Mapping;
import com.agile.common.base.AbstractResponseFormat;
import com.agile.common.base.ApiInfo;
import com.agile.common.base.Constant;
import com.agile.common.base.Head;
import com.agile.common.base.RETURN;
import com.agile.common.base.RequestWrapper;
import com.agile.common.exception.NoSuchRequestMethodException;
import com.agile.common.exception.NoSuchRequestServiceException;
import com.agile.common.exception.SpringExceptionHandler;
import com.agile.common.exception.UnlawfulRequestException;
import com.agile.common.mvc.service.ServiceInterface;
import com.agile.common.util.ApiUtil;
import com.agile.common.util.ArrayUtil;
import com.agile.common.util.FactoryUtil;
import com.agile.common.util.ParamUtil;
import com.agile.common.util.StringUtil;
import com.agile.common.validate.ValidateMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;

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

    @Autowired
    private WebMvcProperties webMvcProperties;

    /**
     * 非法请求处理器
     *
     * @param currentRequest  请求
     * @param currentResponse 响应
     * @return 视图
     */
    @RequestMapping(value = {"/", "/*", "/*/*/*/**"})
    public Object othersProcessor(HttpServletRequest currentRequest, HttpServletResponse currentResponse) {
        return asyncProcessor(() -> {
            try {
                //清理缓存
                clear();

                //设置当前request
                request.set(currentRequest);

                if (initApiInfoByRequestMapping()) {
                    throw new UnlawfulRequestException();
                }
                return processor(currentRequest, currentResponse);
            } catch (Throwable e) {
                return SpringExceptionHandler.createModelAndView(e);
            }
        });
    }

    @RequestMapping(value = {"/{resource}"}, method = RequestMethod.GET)
    public Object processorOfGET0(HttpServletRequest currentRequest, HttpServletResponse currentResponse, @PathVariable String resource) {
        return asyncProcessor(() -> {
            try {
                request.set(currentRequest);
                if (initApiInfoByRequestMapping()) {
                    return processor(currentRequest, currentResponse, StringUtil.removeExtension(resource), "query");
                }
                return processor(currentRequest, currentResponse);
            } catch (Throwable e) {
                return SpringExceptionHandler.createModelAndView(e);
            }
        });
    }

    @RequestMapping(value = {"/{resource}/{id}"}, method = RequestMethod.GET)
    public Object processorOfGET1(HttpServletRequest currentRequest, HttpServletResponse currentResponse, @PathVariable String resource, @PathVariable String id) {
        return asyncProcessor(() -> {
            try {
                request.set(currentRequest);
                if (initApiInfoByRequestMapping()) {
                    RequestWrapper requestWrapper = new RequestWrapper(currentRequest);
                    requestWrapper.addParameter("id", id);
                    return processor(requestWrapper, currentResponse, StringUtil.removeExtension(resource), "queryById");
                }
                return processor(currentRequest, currentResponse);
            } catch (Throwable e) {
                return SpringExceptionHandler.createModelAndView(e);
            }
        });
    }

    @RequestMapping(value = {"/{resource}/page/{page}/{size}"}, method = RequestMethod.GET)
    public Object processorOfGET2(HttpServletRequest currentRequest, HttpServletResponse currentResponse, @PathVariable String resource, @PathVariable String page, @PathVariable String size) {
        return asyncProcessor(() -> {
            try {
                request.set(currentRequest);
                if (initApiInfoByRequestMapping()) {
                    RequestWrapper requestWrapper = new RequestWrapper(currentRequest);
                    requestWrapper.addParameter("page", page);
                    requestWrapper.addParameter("size", size);
                    return processor(requestWrapper, currentResponse, StringUtil.removeExtension(resource), "pageQuery");
                }
                return processor(currentRequest, currentResponse);
            } catch (Throwable e) {
                return SpringExceptionHandler.createModelAndView(e);
            }
        });
    }

    @RequestMapping(value = {"/{resource}"}, method = RequestMethod.POST)
    public Object processorOfPOST(HttpServletRequest currentRequest, HttpServletResponse currentResponse, @PathVariable String resource) {
        return asyncProcessor(() -> {
            try {
                request.set(currentRequest);
                if (initApiInfoByRequestMapping()) {
                    return processor(currentRequest, currentResponse, StringUtil.removeExtension(resource), "save");
                }
                return processor(currentRequest, currentResponse);
            } catch (Throwable e) {
                return SpringExceptionHandler.createModelAndView(e);
            }
        });
    }

    @RequestMapping(value = {"/{resource}/{id}"}, method = RequestMethod.PUT)
    public Object processorOfPUT(HttpServletRequest currentRequest, HttpServletResponse currentResponse, @PathVariable String resource, @PathVariable String id) {
        return asyncProcessor(() -> {
            try {
                request.set(currentRequest);
                if (initApiInfoByRequestMapping()) {
                    RequestWrapper requestWrapper = new RequestWrapper(currentRequest);
                    requestWrapper.addParameter("id", id);
                    return processor(requestWrapper, currentResponse, StringUtil.removeExtension(resource), "update");
                }
                return processor(currentRequest, currentResponse);
            } catch (Throwable e) {
                return SpringExceptionHandler.createModelAndView(e);
            }
        });
    }

    @RequestMapping(value = {"/{resource}"}, method = RequestMethod.DELETE)
    public Object processorOfDELETE(HttpServletRequest currentRequest, HttpServletResponse currentResponse, @PathVariable String resource) {
        return asyncProcessor(() -> {
            try {
                request.set(currentRequest);
                if (initApiInfoByRequestMapping()) {
                    return processor(currentRequest, currentResponse, StringUtil.removeExtension(resource), "delete");
                }
                return processor(currentRequest, currentResponse);
            } catch (Throwable e) {
                return SpringExceptionHandler.createModelAndView(e);
            }
        });
    }

    @RequestMapping(value = {"/{resource}/{id}"}, method = RequestMethod.DELETE)
    public Object processorOfDELETE(HttpServletRequest currentRequest, HttpServletResponse currentResponse, @PathVariable String resource, @PathVariable String id) {
        return asyncProcessor(() -> {
            try {
                request.set(currentRequest);
                if (initApiInfoByRequestMapping()) {
                    RequestWrapper requestWrapper = new RequestWrapper(currentRequest);
                    requestWrapper.addParameter("id", id);
                    return processor(requestWrapper, currentResponse, StringUtil.removeExtension(resource), "delete");
                }
                return processor(currentRequest, currentResponse);
            } catch (Throwable e) {
                return SpringExceptionHandler.createModelAndView(e);
            }
        });
    }

    /**
     * agile框架处理器
     *
     * @param service         服务名
     * @param method          方法名
     * @param currentRequest  request信息
     * @param currentResponse response信息
     * @return 响应试图数据
     */
    @RequestMapping(value = {"/api/{service}/{method}", "/api/{service}/{method}/**", "/{service}/{method}"})
    public Object proxyProcessor(
            HttpServletRequest currentRequest,
            HttpServletResponse currentResponse,
            @PathVariable String service,
            @PathVariable String method
    ) {
        return asyncProcessor(() -> {
            try {
                return processor(currentRequest, currentResponse, service, method);
            } catch (Throwable e) {
                return SpringExceptionHandler.createModelAndView(e);
            }
        });

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
    public ModelAndView processor(
            HttpServletRequest currentRequest,
            HttpServletResponse currentResponse,
            String service,
            String method
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

    private WebAsyncTask<ModelAndView> asyncProcessor(Callable<ModelAndView> callable) {
        Duration timeout = webMvcProperties.getAsync().getRequestTimeout();
        WebAsyncTask<ModelAndView> asyncTask = new WebAsyncTask<>(timeout.toMillis(), callable);
        asyncTask.onTimeout(
                () -> SpringExceptionHandler.createModelAndView(new InterruptedException())
        );
        return asyncTask;
    }

    private ModelAndView processor(HttpServletRequest currentRequest, HttpServletResponse currentResponse) throws Throwable {

        //处理入参
        handleInParam();

        //入参验证
        List<ValidateMsg> validateMessages = ParamUtil.handleInParamValidate(getService(), getMethod());
        Optional<List<ValidateMsg>> optionalValidateMsgList = ParamUtil.aggregation(validateMessages);
        if (optionalValidateMsgList.isPresent()) {
            return ParamUtil.getResponseFormatData(new Head(RETURN.PARAMETER_ERROR), optionalValidateMsgList.get());
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
        ModelAndView modelAndView = ParamUtil.getResponseFormatData(returnData instanceof RETURN ? new Head((RETURN) returnData) : null, getService().getOutParam());

        //清理缓存
        clear();

        return modelAndView;
    }

    /**
     * 由于线程池的使用与threadLocal冲突,前后需要清理缓存
     */
    private void clear() {
        getService().clearInParam();
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
     * @throws NoSuchRequestServiceException 请求服务不存在
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
     * @throws NoSuchRequestServiceException 请求服务不存在
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
     * @throws NoSuchRequestMethodException 请求方法不存在
     */
    private void initMethod(String methodName) throws NoSuchRequestMethodException {
        Method methodCache;
        try {
            methodCache = getService().getClass().getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            throw new NoSuchRequestMethodException();
        }
        if (!Modifier.isPublic(methodCache.getModifiers())) {
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
        method.set(methodCache);
    }

    /**
     * 根据servlet请求、认证信息、目标服务名、目标方法名处理入参
     */
    private void handleInParam() {
        getService().clearInParam();
        HttpServletRequest currentRequest = request.get();

        //将处理过的所有请求参数传入调用服务对象
        getService().setInParam(ParamUtil.handleInParam(currentRequest));
    }

    /**
     * 获取当前线程下Service缓存
     *
     * @return 服务
     */
    private ServiceInterface getService() {
        return service.get();
    }

    /**
     * 获取当前线程下方法缓存
     *
     * @return 方法
     */
    private Method getMethod() {
        return method.get();
    }
}
