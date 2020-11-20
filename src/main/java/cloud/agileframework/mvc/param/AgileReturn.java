package cloud.agileframework.mvc.param;

import cloud.agileframework.common.util.clazz.ClassUtil;
import cloud.agileframework.common.util.clazz.TypeReference;
import cloud.agileframework.common.util.object.ObjectUtil;
import cloud.agileframework.mvc.base.AbstractResponseFormat;
import cloud.agileframework.common.constant.Constant;
import cloud.agileframework.mvc.base.Head;
import cloud.agileframework.mvc.base.RETURN;
import cloud.agileframework.spring.util.BeanUtil;
import org.springframework.web.servlet.ModelAndView;

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
    private static final ThreadLocal<Head> HEAD = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, Object>> OBJECT = ThreadLocal.withInitial(LinkedHashMap::new);
    private static final ThreadLocal<Boolean> IS_INIT = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private AgileReturn() {
    }

    public static void init(RETURN r, Object object) {
        init(new Head(r), object);
    }

    public static void init(Head head, Object object) {
        AgileReturn.clear();
        HEAD.set(head);
        setBody(object);
    }

    public static boolean isInit() {
        return IS_INIT.get();
    }

    public static void add(String key, Object value) {
        IS_INIT.set(true);
        OBJECT.get().put(key, value);
    }

    public static void add(Object value) {
        IS_INIT.set(true);
        if (Map.class.isAssignableFrom(value.getClass())) {
            OBJECT.get().putAll((Map<? extends String, ?>) value);
        } else if (value instanceof String || ClassUtil.isWrapOrPrimitive(value.getClass())) {
            OBJECT.get().put(Constant.ResponseAbout.RESULT, value);
        } else {
            Map<String, Object> map = ObjectUtil.to(value, new TypeReference<Map<String, Object>>() {
            });
            if (map == null) {
                OBJECT.get().put(Constant.ResponseAbout.RESULT, value);
            } else {
                OBJECT.get().putAll(map);
            }
        }
    }

    public static void setHead(RETURN r) {
        IS_INIT.set(true);
        setHead(new Head(r));
    }

    private static void setHead(Head head) {
        IS_INIT.set(true);
        HEAD.set(head);
    }

    /**
     * 重置出参
     *
     * @param object 出参
     */
    private static void setBody(Object object) {
        OBJECT.get().clear();
        add(object);
    }

    public static Head getHead() {
        Head head = HEAD.get();
        if (head == null) {
            setHead(RETURN.SUCCESS);
            return HEAD.get();
        }
        return head;
    }

    public static Map<String, Object> getBody() {
        return OBJECT.get();
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
        AgileReturn.clear();
        return modelAndView;
    }

    public static void clear() {
        HEAD.remove();
        OBJECT.remove();
        IS_INIT.remove();
    }
}
