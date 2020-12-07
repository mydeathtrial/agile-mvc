package cloud.agileframework.mvc.mvc.controller;

import cloud.agileframework.common.util.clazz.ClassInfo;
import cloud.agileframework.common.util.string.StringUtil;
import cloud.agileframework.mvc.annotation.ApiMethod;
import cloud.agileframework.mvc.annotation.Mapping;
import cloud.agileframework.mvc.base.AbstractResponseFormat;
import cloud.agileframework.mvc.base.RETURN;
import cloud.agileframework.mvc.exception.NoSuchRequestMethodException;
import cloud.agileframework.mvc.exception.NoSuchRequestServiceException;
import cloud.agileframework.mvc.exception.SpringExceptionHandler;
import cloud.agileframework.mvc.param.AgileReturn;
import cloud.agileframework.mvc.provider.ValidationHandlerProvider;
import cloud.agileframework.spring.util.BeanUtil;
import cloud.agileframework.spring.util.MappingUtil;
import cloud.agileframework.spring.util.ServletUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    @ResponseBody
    @RequestMapping(value = {"/", "/*", "/*/*/*/**"})
    public Object othersProcessor(HttpServletRequest request) throws NoSuchRequestServiceException {
        if (initApiInfoByRequestMapping(request)) {
            throw new NoSuchRequestServiceException();
        }
        return getModelAndViewWebAsyncTask();
    }

    private WebAsyncTask<ModelAndView> getModelAndViewWebAsyncTask() {
        Object bean = getService();
        Method method = getMethod();
        return asyncProcessor(() -> {
            try {
                return processor(bean, method);
            } catch (Throwable e) {
                return SpringExceptionHandler.createModelAndView(e);
            }
        });
    }

    /**
     * agile框架处理器
     *
     * @param service 服务名
     * @param method  方法名
     * @return 响应试图数据
     */
    @RequestMapping(value = {"/${agile.module-name:api}/{agileInParamService}/{agileInParamMethod}", "/${agile.module-name:api}/{agileInParamService}/{agileInParamMethod}/**", "/{agileInParamService}/{agileInParamMethod}"})
    public Object proxyProcessor(
            HttpServletRequest request,
            @PathVariable("agileInParamService") String service,
            @PathVariable("agileInParamMethod") String method
    ) throws NoSuchRequestServiceException, NoSuchRequestMethodException {
        initApiInfoByRequestMapping(request, service, method);
        return getModelAndViewWebAsyncTask();
    }

    private WebAsyncTask<ModelAndView> asyncProcessor(Callable<ModelAndView> callable) {
        Duration timeout = webMvcProperties.getAsync().getRequestTimeout();
        if (timeout == null) {
            timeout = Duration.ofSeconds(3);
        }
        WebAsyncTask<ModelAndView> asyncTask = new WebAsyncTask<>(timeout.toMillis(), callable);
        Duration finalTimeout = timeout;
        asyncTask.onTimeout(
                () -> SpringExceptionHandler.createModelAndView(
                        new InterruptedException(String.format("请求超时，最长过期时间%s", finalTimeout.toString())))
        );
        return asyncTask;
    }

    @Autowired
    private ObjectProvider<ValidationHandlerProvider> validationHandlerProviders;

    private ModelAndView processor(Object bean, Method method) throws Throwable {
        //入参验证
        List<ValidationHandlerProvider> validationHandlerProviderList = validationHandlerProviders.orderedStream().collect(Collectors.toList());
        for (ValidationHandlerProvider validationHandlerProvider : validationHandlerProviderList) {
            validationHandlerProvider.before(ServletUtil.getCurrentRequest(), ServletUtil.getCurrentResponse(), method);
        }

        //调用目标方法
        BeanUtil.getBean(this.getClass()).invoke(bean, method);

        //提取响应信息
        ModelAndView modelAndView = AgileReturn.build();

        //清空线程缓存
        clear();

        return modelAndView;
    }

    /**
     * 由于线程池的使用与threadLocal冲突,前后需要清理缓存
     */
    public static void clear() {
        SERVICE.remove();
        METHOD.remove();
        AgileReturn.clear();
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
        Method methodCache = ClassInfo.getCache(getService().getClass())
                .getAllMethod()
                .stream()
                .filter(m -> methodName.endsWith(m.getName()) && Modifier.isPublic(m.getModifiers()))
                .findFirst()
                .orElse(null);


        if (methodCache == null) {
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
    private static Object getService() {
        return SERVICE.get();
    }

    @Autowired
    private ObjectProvider<HandlerMethodArgumentResolver> handlerMethodArgumentResolvers;

    @Transactional(rollbackFor = Exception.class)
    public void invoke(Object bean, Method method) throws Throwable {

        try {
            int count = method.getParameterCount();

            Object returnData;
            if (count > 0) {
                Object[] args = new Object[count];
                IntStream.range(0, count).forEach(index -> {
                    MethodParameter methodParameter = new MethodParameter(method, index);
                    methodParameter.initParameterNameDiscovery(localVariableTableParameterNameDiscoverer);
                    args[index] = handlerMethodArgumentResolvers.orderedStream()
                            .filter(resolver -> resolver.supportsParameter(methodParameter))
                            .map(resolver -> {
                                try {
                                    return resolver.resolveArgument(methodParameter, null, null, null);
                                } catch (Exception e) {
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull)
                            .findFirst()
                            .orElse(null);
                });
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
                //如果未显示调用初始化返回值，则将返回数据直接放入返回参数
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
    private static Method getMethod() {
        return METHOD.get();
    }
}
