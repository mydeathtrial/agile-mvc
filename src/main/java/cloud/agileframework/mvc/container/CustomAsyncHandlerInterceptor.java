package cloud.agileframework.mvc.container;

import cloud.agileframework.mvc.mvc.controller.MainController;
import cloud.agileframework.mvc.param.AgileParam;
import cloud.agileframework.mvc.provider.HandlerProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 佟盟
 * 日期 2020/7/13 13:58
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class CustomAsyncHandlerInterceptor implements AsyncHandlerInterceptor {
    @Autowired
    private ObjectProvider<HandlerProvider> handlerProviders;

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        AgileParam.clear();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        List<HandlerProvider> handlerProviderList = handlerProviders.orderedStream().collect(Collectors.toList());
        for (HandlerProvider handlerProvider : handlerProviderList) {
            handlerProvider.before(request, response, ((HandlerMethod) handler).getMethod());
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MainController.clear();
    }


}
