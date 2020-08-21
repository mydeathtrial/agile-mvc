package com.agile.common.mvc.controller;

import cloud.agileframework.common.util.clazz.TypeReference;
import cloud.agileframework.common.util.string.StringUtil;
import cloud.agileframework.spring.util.MappingUtil;
import cloud.agileframework.spring.util.ServletUtil;
import cloud.agileframework.spring.util.spring.BeanUtil;
import cloud.agileframework.validate.ValidateMsg;
import cloud.agileframework.validate.ValidateUtil;
import com.agile.common.annotation.ApiMethod;
import com.agile.common.annotation.Mapping;
import com.agile.common.base.AbstractResponseFormat;
import com.agile.common.base.Constant;
import com.agile.common.base.Head;
import com.agile.common.base.RETURN;
import com.agile.common.exception.NoSuchRequestMethodException;
import com.agile.common.exception.NoSuchRequestServiceException;
import com.agile.common.exception.SpringExceptionHandler;
import com.agile.common.param.AgileParam;
import com.agile.common.param.AgileReturn;
import com.agile.common.util.ViewUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

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
    private static final ThreadLocal<Object> SERVICE = new ThreadLocal<>();

    /**
     * 方法缓存变量
     */
    private static final ThreadLocal<Method> METHOD = new ThreadLocal<>();

    private final LocalVariableTableParameterNameDiscoverer localVariableTableParameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    @Autowired
    private WebMvcProperties webMvcProperties;

    /**
     * 非法请求处理器
     *
     * @return 视图
     */
    @RequestMapping(value = {"/", "/*", "/*/*/*/**"})
    public Object othersProcessor(HttpServletRequest request) throws NoSuchRequestServiceException {
        if (initApiInfoByRequestMapping(request)) {
            throw new NoSuchRequestServiceException();
        }
        return getModelAndViewWebAsyncTask();
    }

    private WebAsyncTask<ModelAndView> getModelAndViewWebAsyncTask() {
        Map<String, Object> params = AgileParam.getInParam();
        Object bean = getService();
        Method method = getMethod();
        return asyncProcessor(() -> {
            try {
                AgileParam.init(params);
                return processor(bean, method);
            } catch (Throwable e) {
                return SpringExceptionHandler.createModelAndView(e);
            }
        });
    }

    private Object processor(HttpServletRequest request, String service, String method, Consumer<Map<String, Object>> parseParams) throws NoSuchRequestServiceException, NoSuchRequestMethodException {
        initApiInfoByRequestMapping(request, service, method);
        parseParams.accept(AgileParam.getInParam());
        return getModelAndViewWebAsyncTask();
    }

    /**
     * agile框架处理器
     *
     * @param service 服务名
     * @param method  方法名
     * @return 响应试图数据
     */
    @RequestMapping(value = {"/api/{service}/{method}", "/api/{service}/{method}/**", "/{service}/{method}"})
    public Object proxyProcessor(
            HttpServletRequest request,
            @PathVariable("service") String service,
            @PathVariable("method") String method
    ) throws NoSuchRequestServiceException, NoSuchRequestMethodException {
        return processor(request, service, method, o -> {
        });
    }

    private WebAsyncTask<ModelAndView> asyncProcessor(Callable<ModelAndView> callable) {
        Duration timeout = webMvcProperties.getAsync().getRequestTimeout();
        WebAsyncTask<ModelAndView> asyncTask = new WebAsyncTask<>(timeout.toMillis(), callable);
        asyncTask.onTimeout(
                () -> SpringExceptionHandler.createModelAndView(new InterruptedException())
        );
        return asyncTask;
    }

    private ModelAndView processor(Object bean, Method method) throws Throwable {
        //入参验证
        List<ValidateMsg> validateMessages = ValidateUtil.handleInParamValidate(method, AgileParam.getInParam());
        List<ValidateMsg> optionalValidateMsgList = ValidateUtil.aggregation(validateMessages);
        if (!optionalValidateMsgList.isEmpty()) {
            return ViewUtil.getResponseFormatData(new Head(RETURN.PARAMETER_ERROR), optionalValidateMsgList);
        }

        //调用目标方法
        BeanUtil.getBean(this.getClass()).invoke(bean, method);

        //获取出参
        Map<String, Object> outParam = AgileReturn.getBody();

        //判断是否跳转
        if (outParam.containsKey(Constant.RegularAbout.FORWARD)) {
            return jump(Constant.RegularAbout.FORWARD);
        }
        if (outParam.containsKey(Constant.RegularAbout.REDIRECT)) {
            return jump(Constant.RegularAbout.REDIRECT);
        }

        /**
         * 异步线程时要清空异步线程缓存
         */
        clear();

        return AgileReturn.build();
    }

    /**
     * 由于线程池的使用与threadLocal冲突,前后需要清理缓存
     */
    public static void clear() {
        SERVICE.remove();
        METHOD.remove();
        AgileParam.clear();
    }

    /**
     * 转发
     *
     * @param jumpMethod 跳转方式
     * @return 视图
     */
    private ModelAndView jump(String jumpMethod) {
        ModelAndView model = new ModelAndView(exposeJumpUrl(jumpMethod, AgileReturn.getBody()));
        model.addAllObjects(AgileReturn.getBody());
        model.addAllObjects(AgileParam.getInParam());
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
        return requestMethods == null || requestMethods.length <= 0 || !ArrayUtils.contains(requestMethods, requestMethod);
    }

    /**
     * 根据requestMapping信息初始化目标method信息
     *
     * @return 成功/失败
     */
    private boolean initApiInfoByRequestMapping(HttpServletRequest currentRequest) {

        HandlerMethod handlerMethod = MappingUtil.matching(currentRequest);

        if (handlerMethod != null && !(handlerMethod.getBean() instanceof MainController)) {
            Object bean = handlerMethod.getBean();
            Method targetMethod = handlerMethod.getMethod();
            initServiceByObject(bean);
            initMethodByObject(targetMethod);
            return false;
        }
        return true;
    }

    private boolean initApiInfoByRequestMapping(HttpServletRequest request, String service, String method) throws NoSuchRequestServiceException, NoSuchRequestMethodException {

        //处理目标API
        if (initApiInfoByRequestMapping(request)) {
            initService(StringUtil.toLowerName(service));
            initMethod(StringUtil.toLowerName(method));
            return false;
        }
        return true;
    }

    /**
     * 根据服务名在Spring上下文中获取服务bean
     *
     * @param o 对象
     */
    private static void initServiceByObject(Object o) {
        SERVICE.set(o);
    }

    /**
     * 根据服务名在Spring上下文中获取服务bean
     *
     * @param serviceName 服务名
     */
    private static void initService(String serviceName) throws NoSuchRequestServiceException {
        Object o = BeanUtil.getBean(serviceName);
        if (o == null) {
            throw new NoSuchRequestServiceException();
        }
        SERVICE.set(o);
    }

    /**
     * 根据服务名在Spring上下文中获取服务bean
     *
     * @param o 方法
     */
    private static void initMethodByObject(Method o) {
        METHOD.set(o);
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
        RequestMethod currentRequestMethod = RequestMethod.valueOf(ServletUtil.getCurrentRequest().getMethod());
        Mapping requestMapping = methodCache.getAnnotation(Mapping.class);
        if (requestMapping != null && allowRequestMethod(requestMapping.method(), currentRequestMethod)) {
            throw new NoSuchRequestMethodException();
        }
        ApiMethod apiMethod = methodCache.getAnnotation(ApiMethod.class);
        if (apiMethod != null && allowRequestMethod(apiMethod.value(), currentRequestMethod)) {
            throw new NoSuchRequestMethodException();
        }
        METHOD.set(methodCache);
    }

    /**
     * 获取当前线程下Service缓存
     *
     * @return 服务
     */
    public static Object getService() {
        return SERVICE.get();
    }

    @Transactional(rollbackFor = Exception.class)
    public void invoke(Object bean, Method method) throws Throwable {
        try {
            Class<?>[] types = method.getParameterTypes();
            String[] names = localVariableTableParameterNameDiscoverer.getParameterNames(method);
            Object returnData;
            if (types.length > 0 && ArrayUtils.isSameLength(types, names)) {
                Object[] args = new Object[types.length];
                for (int i = 0; i < types.length; i++) {
                    args[i] = AgileParam.getInParam(names[i], new TypeReference<>(types[i]));
                }
                returnData = method.invoke(bean, args);
            } else {
                returnData = method.invoke(bean);
            }

            if (returnData instanceof RETURN) {
                //如果是头信息，则交给控制层处理
                AgileReturn.setHead((RETURN) returnData);
            } else if (returnData instanceof AbstractResponseFormat) {
                //如果直接返回模板类，则调用模板类的初始化返回数据方法
                ((AbstractResponseFormat) returnData).initAgileReturn();
            } else {
                //其他类型数据直接放入返回参数
                if (returnData != null) {
                    AgileReturn.add(returnData);
                }
            }
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    /**
     * 获取当前线程下方法缓存
     *
     * @return 方法
     */
    public static Method getMethod() {
        return METHOD.get();
    }
}
