package cloud.agileframework.mvc.mvc.controller;

import cloud.agileframework.mvc.base.RETURN;
import cloud.agileframework.mvc.exception.NoSuchRequestServiceException;
import cloud.agileframework.mvc.exception.SpringExceptionHandler;
import cloud.agileframework.mvc.param.AgileReturn;
import cloud.agileframework.mvc.provider.ValidationHandlerProvider;
import cloud.agileframework.spring.util.MappingUtil;
import cloud.agileframework.spring.util.ServletUtil;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * 主控制层
 *
 * @author 佟盟 on 2017/8/22
 */
@Controller
public class MainController {


    @Autowired
    private AgileServiceProxy proxyService;
    @Autowired
    private WebMvcProperties webMvcProperties;
    @Autowired
    private ObjectProvider<ValidationHandlerProvider> validationHandlerProviders;

    /**
     * 非法请求处理器
     *
     * @return 视图
     */
    @RequestMapping(value = {"/", "/**"})
    public Object othersProcessor(HttpServletRequest request, HttpServletResponse response) throws NoSuchRequestServiceException {
        HandlerMethod handlerMethod = MappingUtil.matching(request);
        if (handlerMethod == null || handlerMethod.getBean() instanceof MainController) {
            throw new NoSuchRequestServiceException();
        }
        return getModelAndViewWebAsyncTask(request, response, handlerMethod);
    }

    private WebAsyncTask<Object> getModelAndViewWebAsyncTask(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        return asyncProcessor(request, response, () -> {
            try {
                return processor(request, response, handlerMethod);
            } catch (Throwable e) {
                return SpringExceptionHandler.createModelAndView(request, response, e);
            }
        });
    }

    private WebAsyncTask<Object> asyncProcessor(HttpServletRequest request, HttpServletResponse response, Callable<Object> callable) {
        Duration timeout = webMvcProperties.getAsync().getRequestTimeout();
        if (timeout == null) {
            timeout = Duration.ofSeconds(15);
        }
        WebAsyncTask<Object> asyncTask = new WebAsyncTask<>(timeout.toMillis(), callable);
        Duration finalTimeout = timeout;
        asyncTask.onTimeout(
                () -> SpringExceptionHandler.createModelAndView(request, response,
                        new InterruptedException(String.format("请求超时，最长过期时间%s", finalTimeout)))
        );
        return asyncTask;
    }

    private Object processor(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Throwable {
        //入参验证
        List<ValidationHandlerProvider> validationHandlerProviderList = validationHandlerProviders.orderedStream().collect(Collectors.toList());
        for (ValidationHandlerProvider validationHandlerProvider : validationHandlerProviderList) {
            validationHandlerProvider.before(ServletUtil.getCurrentRequest(), ServletUtil.getCurrentResponse(), handlerMethod.getMethod());
        }

        //调用目标方法
        Object returnData = proxyService.invoke(request, response, handlerMethod);

        Class<?> parameterType = handlerMethod.getReturnType().getParameterType();
        if (parameterType == RETURN.class || parameterType == void.class) {
            ModelAndView modelAndView = AgileReturn.build();
            AgileReturn.clear();
            return modelAndView;
        }

        //提取响应信息
        return returnData;
    }
}
