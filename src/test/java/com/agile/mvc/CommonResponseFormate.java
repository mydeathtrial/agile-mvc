package com.agile.mvc;

import cloud.agileframework.mvc.base.AbstractResponseFormat;
import cloud.agileframework.mvc.base.Head;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author 佟盟
 * 日期 2020/9/00003 11:15
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@Component
public class CommonResponseFormate extends AbstractResponseFormat {
    @Override
    public Map<String, Object> buildResponseData(Head head, Object result) {
        //head为组件整理后的响应头部信息
        //result为响应体信息
        //自行定制返回结果状态为Map格式返回，该Map将作为ModelAndView中的Model组装成视图写入response
        return null;
    }
}
