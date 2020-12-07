package cloud.agileframework.mvc.provider;

import cloud.agileframework.mvc.util.ApiUtil;
import cloud.agileframework.spring.util.RequestWrapper;
import com.google.common.collect.Maps;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 佟盟
 * 日期 2020/8/00031 18:03
 * 描述 处理路径参数
 * @version 1.0
 * @since 1.0
 */
public class ArgumentInitHandlerProvider implements HandlerProvider {
    @Override
    public void before(HttpServletRequest request, HttpServletResponse response, Method method) throws Exception {
        RequestWrapper requestWrapper = RequestWrapper.extract(request);
        requestWrapper.extendInParam(parseUriVariable(request));
    }

    /**
     * 处理路径变量
     *
     * @param currentRequest 请求
     * @return 返回变量集合
     */
    private static Map<String, Object> parseUriVariable(HttpServletRequest currentRequest) {

        Map<String, Object> uriVariables = Maps.newHashMap();

        //处理Mapping参数
        String uri = currentRequest.getRequestURI();
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
        }
        RequestMappingInfo requestMappingInfo = ApiUtil.getApiCache(currentRequest);


        if (requestMappingInfo == null) {
            HashMap<String, Object> map = (HashMap<String, Object>) currentRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            if (map != null) {
                return map;
            }
            return uriVariables;
        }

        //处理路径入参
        PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
        if (patternsCondition != null) {
            for (String mapping : patternsCondition.getPatterns()) {
                try {
                    uriVariables.putAll(new AntPathMatcher().extractUriTemplateVariables(mapping, uri));
                } catch (Exception ignored) {
                }
            }
        }

        return uriVariables;
    }
}
