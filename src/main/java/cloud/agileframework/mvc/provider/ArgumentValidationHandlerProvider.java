package cloud.agileframework.mvc.provider;

import cloud.agileframework.mvc.base.Constant;
import cloud.agileframework.mvc.exception.AgileArgumentException;
import cloud.agileframework.spring.util.RequestWrapper;
import cloud.agileframework.validate.ValidateMsg;
import cloud.agileframework.validate.ValidateUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author 佟盟
 * 日期 2020/8/00031 17:57
 * 描述 参数验证解析
 * @version 1.0
 * @since 1.0
 */
public class ArgumentValidationHandlerProvider implements ValidationHandlerProvider {
    @Override
    public void before(HttpServletRequest request, HttpServletResponse response, Method method) throws Exception {
        RequestWrapper requestWrapper = (RequestWrapper) RequestWrapper.of(request);
        Map<String, Object> params = requestWrapper.getInParamWithFile();
        //入参验证
        List<ValidateMsg> validateMessages = ValidateUtil.handleInParamValidate(method, params);
        List<ValidateMsg> optionalValidateMsgList = ValidateUtil.aggregation(validateMessages);
        if (!optionalValidateMsgList.isEmpty()) {
            request.setAttribute(Constant.RequestAttributeAbout.ATTRIBUTE_ERROR, optionalValidateMsgList);
            throw new AgileArgumentException();
        }
    }

}
