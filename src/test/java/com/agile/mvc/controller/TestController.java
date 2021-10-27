package com.agile.mvc.controller;

import cloud.agileframework.mvc.annotation.AgileInParam;
import cloud.agileframework.mvc.base.RETURN;
import cloud.agileframework.mvc.exception.AbstractCustomException;
import cloud.agileframework.mvc.exception.AgileArgumentException;
import cloud.agileframework.mvc.exception.NoSuchRequestMethodException;
import cloud.agileframework.mvc.param.AgileParam;
import cloud.agileframework.mvc.param.AgileReturn;
import cloud.agileframework.validate.annotation.Validate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author 佟盟
 * 日期 2020/7/12 20:21
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@Controller
public class TestController {
    @Validate(value = "name", nullable = false)
    @RequestMapping("/test")
    public ModelAndView test(@AgileInParam("name") String a) {
        AgileReturn.setHead(RETURN.SUCCESS);
        AgileReturn.add(AgileParam.getInParam());
        AgileReturn.add("a",a);
        return AgileReturn.build();
    }

    @RequestMapping("/test3")
    public RETURN test3(String a, MultipartFile[] file) throws NoSuchRequestMethodException {
        AgileReturn.add("a",a);
        AgileReturn.add("file",file);
        throw new NoSuchRequestMethodException();
    }

    @RequestMapping("/test4")
    public ModelAndView test4(String a) throws AgileArgumentException {
        throw new AgileArgumentException();
    }

    @RequestMapping("/test5")
    public ModelAndView test5(String a) throws AbstractCustomException {
        throw new MyException(a);
    }
}
