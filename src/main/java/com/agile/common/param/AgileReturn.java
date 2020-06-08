package com.agile.common.param;

import com.agile.common.base.AbstractResponseFormat;
import com.agile.common.base.Constant;
import com.agile.common.base.Head;
import com.agile.common.base.RETURN;
import com.agile.common.util.FactoryUtil;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * @author 佟盟
 * 日期 2020/6/1 16:49
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class AgileReturn {
    private final Head head;
    private final Object object;

    public AgileReturn(RETURN r, Object object) {
        this.head = r == null ? null : new Head(r);
        this.object = object;
    }

    public AgileReturn(Head head, Object object) {
        this.head = head;
        this.object = object;
    }

    public Head getHead() {
        return head;
    }

    public Object getObject() {
        return object;
    }


    public ModelAndView build() {
        ModelAndView modelAndView = new ModelAndView();
        AbstractResponseFormat abstractResponseFormat = FactoryUtil.getBean(AbstractResponseFormat.class);
        if (abstractResponseFormat != null) {
            return abstractResponseFormat.buildResponse(head, object);
        } else {
            if (head != null) {
                modelAndView.addObject(Constant.ResponseAbout.HEAD, head);
            }
            if (object != null && Map.class.isAssignableFrom(object.getClass())) {
                modelAndView.addAllObjects((Map<String, ?>) object);
            } else {
                modelAndView.addObject(Constant.ResponseAbout.RESULT, object);
            }
        }
        return modelAndView;
    }
}
