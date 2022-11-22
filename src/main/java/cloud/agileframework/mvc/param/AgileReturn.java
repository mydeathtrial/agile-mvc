package cloud.agileframework.mvc.param;

import cloud.agileframework.common.constant.Constant;
import cloud.agileframework.common.util.clazz.ClassUtil;
import cloud.agileframework.common.util.clazz.TypeReference;
import cloud.agileframework.common.util.object.ObjectUtil;
import cloud.agileframework.mvc.base.AbstractResponseFormat;
import cloud.agileframework.mvc.base.RETURN;
import cloud.agileframework.spring.util.BeanUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author 佟盟
 * 日期 2020/6/1 16:49
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class AgileReturn {
    private static final ThreadLocal<RETURN> HEAD = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, Object>> BODY = ThreadLocal.withInitial(LinkedHashMap::new);
    private static final ThreadLocal<Map<String, Object>> OTHER = ThreadLocal.withInitial(LinkedHashMap::new);
    private static final ThreadLocal<String> VIEW_NAME = new ThreadLocal<>();
    private static final ThreadLocal<View> VIEW = new ThreadLocal<>();

    private AgileReturn() {
    }

    public static void init(RETURN head, Object object) {
        AgileReturn.clear();
        HEAD.set(head);
        setBody(object);
    }

    public static void add(String key, Object value) {
        BODY.get().put(key, value);
    }

    public static void add(Object value) {
        if (Map.class.isAssignableFrom(value.getClass())) {
            BODY.get().putAll((Map<? extends String, ?>) value);
        } else if (value instanceof String || ClassUtil.isWrapOrPrimitive(value.getClass())) {
            BODY.get().put(Constant.ResponseAbout.RESULT, value);
        } else {
            Map<String, Object> map = ObjectUtil.to(value, new TypeReference<Map<String, Object>>() {
            });
            if (map == null) {
                BODY.get().put(Constant.ResponseAbout.RESULT, value);
            } else {
                BODY.get().putAll(map);
            }
        }
    }

    public static void setHead(RETURN r) {
        HEAD.set(r);
    }

    /**
     * 重置出参
     *
     * @param object 出参
     */
    private static void setBody(Object object) {
        BODY.get().clear();
        add(object);
    }

    public static RETURN getHead() {
        RETURN head = HEAD.get();
        if (head == null) {
            setHead(RETURN.SUCCESS);
            return HEAD.get();
        }
        return head;
    }

    public static Map<String, Object> getBody() {
        return BODY.get();
    }


    public static void setViewName(String viewName) {
        VIEW_NAME.set(viewName);
    }

    public static String getViewName() {
        return VIEW_NAME.get();
    }

    public static void setView(View view) {
        VIEW.set(view);
    }

    public static View getView() {
        return VIEW.get();
    }

    public static ModelAndView build() {
        ModelAndView modelAndView = new ModelAndView();
        AbstractResponseFormat abstractResponseFormat = BeanUtil.getBean(AbstractResponseFormat.class);
        if (abstractResponseFormat != null) {
            modelAndView = abstractResponseFormat.buildResponse(getHead(), getBody());
        } else {
            if (getHead() != null) {
                modelAndView.addObject(Constant.ResponseAbout.HEAD, getHead());
                modelAndView.setStatus(getHead().getStatus());
            }
            if (getBody() != null) {
                modelAndView.addAllObjects(getBody());
            } else {
                modelAndView.addObject(Constant.ResponseAbout.RESULT, getBody());
            }
        }
        String viewName = getViewName();
        if (viewName != null) {
            modelAndView.setViewName(viewName);
        }
        View view = getView();
        if (view != null) {
            modelAndView.setView(view);
        }
        final Map<String, Object> otherModel = OTHER.get();
        if (otherModel != null) {
            modelAndView.addAllObjects(otherModel);
        }

        return modelAndView;
    }

    public static void clear() {
        HEAD.remove();
        OTHER.remove();
        BODY.remove();
        VIEW_NAME.remove();
        VIEW.remove();
    }
}
