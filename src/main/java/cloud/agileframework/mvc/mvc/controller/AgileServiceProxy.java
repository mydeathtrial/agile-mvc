package cloud.agileframework.mvc.mvc.controller;

import cloud.agileframework.mvc.base.AbstractResponseFormat;
import cloud.agileframework.mvc.base.RETURN;
import cloud.agileframework.mvc.param.AgileReturn;
import cloud.agileframework.spring.util.ServletUtil;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * 方法
 */
public class AgileServiceProxy {
    private final LocalVariableTableParameterNameDiscoverer localVariableTableParameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
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
                                    return resolver.resolveArgument(methodParameter, new ModelAndViewContainer(), new ServletWebRequest(ServletUtil.getCurrentRequest(), ServletUtil.getCurrentResponse()), null);
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

}
