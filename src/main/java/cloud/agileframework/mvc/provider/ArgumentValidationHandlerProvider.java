package cloud.agileframework.mvc.provider;

import cloud.agileframework.common.constant.Constant;
import cloud.agileframework.mvc.exception.AgileArgumentException;
import cloud.agileframework.mvc.param.AgileParam;
import cloud.agileframework.validate.ValidateMsg;
import cloud.agileframework.validate.ValidateUtil;
import com.google.common.collect.Maps;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

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
        //入参验证
        HashMap<String, Object> paramsClone = Maps.newHashMap(AgileParam.getInParam());
        paramsClone.remove(Constant.RequestAbout.SERVICE);
        paramsClone.remove(Constant.RequestAbout.METHOD);

        if (paramsClone.isEmpty()) {
            paramsClone = null;
        }

        List<ValidateMsg> validateMessages = ValidateUtil.handleInParamValidate(method, paramsClone);
        List<ValidateMsg> optionalValidateMsgList = ValidateUtil.aggregation(validateMessages);
        if (!optionalValidateMsgList.isEmpty()) {
            request.setAttribute(Constant.RequestAttributeAbout.ATTRIBUTE_ERROR, optionalValidateMsgList);
            throw new AgileArgumentException();
        }
    }

}
