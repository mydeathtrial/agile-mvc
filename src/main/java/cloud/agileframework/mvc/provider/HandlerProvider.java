package cloud.agileframework.mvc.provider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author 佟盟
 * 日期 2020/8/00031 17:54
 * 描述 处理请求钩子函数
 * @version 1.0
 * @since 1.0
 */
public interface HandlerProvider {
    /**
     * 处理请求之前
     *
     * @param request  请求
     * @param response 响应
     * @param method   方法
     * @throws Exception 异常
     */
    void before(HttpServletRequest request, HttpServletResponse response, Method method) throws Exception;
}
