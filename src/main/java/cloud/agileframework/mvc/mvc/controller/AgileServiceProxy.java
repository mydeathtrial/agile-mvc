package cloud.agileframework.mvc.mvc.controller;

import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 方法
 */
public class AgileServiceProxy {
    private final LocalVariableTableParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
    private final HandlerMethodArgumentResolverComposite argumentResolvers;

    public AgileServiceProxy(List<HandlerMethodArgumentResolver> argumentResolvers) {

        if (argumentResolvers == null) {
            this.argumentResolvers = null;
        } else {
            this.argumentResolvers = new HandlerMethodArgumentResolverComposite();
            this.argumentResolvers.addResolvers(argumentResolvers);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Object invoke(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Throwable {
        InvocableHandlerMethod invocableHandlerMethod = new InvocableHandlerMethod(handlerMethod);
        invocableHandlerMethod.setHandlerMethodArgumentResolvers(argumentResolvers);
        invocableHandlerMethod.setParameterNameDiscoverer(parameterNameDiscoverer);
        return invocableHandlerMethod.invokeForRequest(new ServletWebRequest(request, response), new ModelAndViewContainer());
    }

}
