package com.agile.mvc.controller;

import cloud.agileframework.validate.annotation.Validate;
import cloud.agileframework.mvc.annotation.AgileService;
import cloud.agileframework.mvc.annotation.Mapping;
import cloud.agileframework.mvc.base.RETURN;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

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
    @Mapping(path = "/test2/{id}")
    public RETURN test(int a, String id, MultipartFile[] file) {
//        AgileReturn.add("params",a);
        return SUCCESS;
    }

    public Object test4() {
        return SUCCESS;
    }
}
