package com.agile.mvc.controller;

import cloud.agileframework.common.util.generator.IDUtil;
import cloud.agileframework.mvc.annotation.AgileService;
import cloud.agileframework.mvc.annotation.Mapping;
import cloud.agileframework.mvc.base.RETURN;
import cloud.agileframework.mvc.param.AgileReturn;
import cloud.agileframework.validate.ValidateCustomBusiness;
import cloud.agileframework.validate.ValidateMsg;
import cloud.agileframework.validate.annotation.Validate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static cloud.agileframework.mvc.base.RETURN.SUCCESS;

/**
 * @author 佟盟
 * 日期 2020/7/13 16:22
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@AgileService
public class Test2Service {
    /**
     * 描述：
     *
     * @author 佟盟
     * @date 2020/7/13 16:22
     */
    @Validate(value = "a", nullable = false)
    @Validate(value = "a", customBusiness = {MyValidate.class})
    @Mapping(path = "/test2/{id}")
    public RETURN test(int a, String id_d, MultipartFile[] file) {
        AgileReturn.add("params", IDUtil.generatorId());
        return SUCCESS;
    }

    public static class MyValidate implements ValidateCustomBusiness {

        @Override
        public List<ValidateMsg> validate(Object params) {
            return null;
        }
    }

    public Object test4() {
        return SUCCESS;
    }
}
