package cloud.agileframework.mvc.container;

import cloud.agileframework.mvc.param.AgileParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 佟盟
 * 日期 2020/6/1 21:48
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class CustomHandlerInterceptor implements HandlerInterceptor {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        AgileParam.clear();
        if (logger.isDebugEnabled()) {
            logger.debug("参数处理器已清理");
        }
    }
}
