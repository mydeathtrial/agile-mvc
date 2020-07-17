package com.agile.mvc.controller;

import com.agile.common.annotation.AgileService;
import com.agile.common.annotation.Mapping;
import com.agile.common.base.RETURN;
import com.agile.common.mvc.model.dao.Dao;
import com.agile.common.param.AgileParam;
import com.agile.common.param.AgileReturn;
import com.agile.common.properties.KaptchaConfigProperties;
import com.agile.common.util.CacheUtil;
import com.agile.common.util.IdUtil;
import com.agile.common.util.ServletUtil;
import com.agile.mvc.entity.DictionaryDataEntity;
import com.agile.mvc.entity.SysTaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import sun.misc.Cache;

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
    @Mapping(path = "/test2")
    public RETURN test() {
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
        dao.save(DictionaryDataEntity.builder().code("tt").dictionaryDataId(IdUtil.generatorId().toString()).name("tt").isFixed(true).build());
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
