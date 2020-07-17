package com.agile.mvc.controller;

import com.agile.common.base.RETURN;
import com.agile.common.param.AgileParam;
import com.agile.common.param.AgileReturn;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @RequestMapping("/test")
    public ModelAndView test(String a) {
        AgileReturn.setHead(RETURN.SUCCESS);
        AgileReturn.add(AgileParam.getInParam());
        return AgileReturn.build();
    }
}
