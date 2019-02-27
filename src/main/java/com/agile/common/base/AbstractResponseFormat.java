package com.agile.common.base;

import com.agile.common.annotation.Remark;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author 佟盟 on 2018/11/2
 */
public abstract class AbstractResponseFormat extends LinkedHashMap<String, Object> {

    /**
     * 构建响应报文体
     *
     * @param head   头信息
     * @param result 体信息
     * @return 返回ModelAndView
     */
    public ModelAndView buildResponse(Head head, Object result) {
        Map<String, Object> map = new HashMap<>(Constant.NumberAbout.FOUR);

        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Remark remark = field.getAnnotation(Remark.class);
            if (remark != null) {
                try {
                    String param = remark.value();
                    if (Constant.ResponseAbout.RESULT.equals(param)) {
                        if (result == null) {
                            map.put(field.getName(), null);
                            continue;
                        }
                        boolean isMap = Map.class.isAssignableFrom(result.getClass());
                        if (isMap && ((Map) result).containsKey(Constant.ResponseAbout.RESULT)) {
                            Object o = ((Map) result).get(Constant.ResponseAbout.RESULT);
                            if (o.getClass() == PageImpl.class) {
                                map.put(field.getName(), new HashMap<String, Object>(Constant.NumberAbout.TWO) {{
                                    put("total", ((PageImpl) o).getTotalElements());
                                    put("data", ((PageImpl) o).getContent());
                                }});
                            } else {
                                map.put(field.getName(), ((Map) result).get(Constant.ResponseAbout.RESULT));
                            }
                        } else {
                            map.put(field.getName(), result);
                        }
                    } else {
                        if (head == null) {
                            continue;
                        }
                        Field f = Head.class.getDeclaredField(remark.value());
                        f.setAccessible(true);
                        map.put(field.getName(), f.get(head));

                    }
                } catch (Exception ignored) {
                }
            }
        }
        ModelAndView mv = new ModelAndView();
        mv.addAllObjects(map);
        return mv;
    }
}
