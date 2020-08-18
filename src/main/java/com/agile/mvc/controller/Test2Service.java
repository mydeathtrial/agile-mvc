package com.agile.mvc.controller;

import cloud.agileframework.cache.util.CacheUtil;
import cloud.agileframework.jpa.dao.Dao;
import cloud.agileframework.kaptcha.properties.KaptchaConfigProperties;
import cloud.agileframework.spring.util.ServletUtil;
import cloud.agileframework.spring.util.spring.IdUtil;
import cloud.agileframework.validate.annotation.Validate;
import com.agile.common.annotation.AgileService;
import com.agile.common.annotation.Mapping;
import com.agile.common.base.RETURN;
import com.agile.common.param.AgileParam;
import com.agile.common.param.AgileReturn;
import com.agile.mvc.entity.DictionaryDataEntity;
import com.agile.mvc.entity.SysTaskEntity;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;

import static com.agile.common.base.RETURN.SUCCESS;

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
     * @author 佟盟
     * @date 2020/7/13 16:22
    */
    @Validate(value = "a",nullable = false)
    @Mapping(path = "/test2/{id}")
    public RETURN test(int a,String id) {
//        AgileReturn.add("params",a);
        return SUCCESS;
    }

    @Autowired
    private Dao dao;

    /**
     * 描述：
     * @author 佟盟
     * @date 2020/7/13 16:22
     */
    @Mapping(path = "/test3")
    public void test2(Integer a, SysTaskEntity entity) {
        AgileParam.getInParam("a", Integer.class);
        AgileReturn.setHead(SUCCESS);
        AgileReturn.add(AgileParam.getInParam());
        dao.save(DictionaryDataEntity.builder().code("tt").id(IdUtil.generatorId().toString()).name("tt").isFixed(true).build());
    }

    @Autowired
    private KaptchaConfigProperties properties;

    /**
     * 描述：
     * @author 佟盟
     * @date 2020/7/16 18:58
    */
    @Mapping(path = "/test4")
    public Object test3() {
        CacheUtil.put("tudou",1);
        return CacheUtil.get(codeToken(ServletUtil.getCurrentRequest()));
    }

    private String codeToken(HttpServletRequest req) {
        String codeToken;

        codeToken = req.getHeader(properties.getTokenHeader());
        if (codeToken == null && req.getCookies() != null) {
            codeToken = Arrays.stream(req.getCookies())
                    .filter(cookie -> cookie.getName().equals(properties.getTokenHeader()))
                    .map(Cookie::getValue)
                    .findFirst().orElse(null);
        }
        if (codeToken == null) {
            codeToken = (String) req.getAttribute(properties.getTokenHeader());
        }
        return codeToken;
    }
}
