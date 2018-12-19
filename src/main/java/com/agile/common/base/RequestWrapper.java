package com.agile.common.base;

import com.agile.common.util.ArrayUtil;
import org.springframework.data.domain.Page;
import org.springframework.validation.BeanPropertyBindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 佟盟 on 2018/3/26
 * HttpServletRequest扩展对象，用于跳转视图中参数传递
 */
public class RequestWrapper extends HttpServletRequestWrapper {

    private Map<String, String[]> params;

    public RequestWrapper(HttpServletRequest request) {
        this(request, new HashMap<>());
    }

    private RequestWrapper(HttpServletRequest request, Map<String, String[]> extendParams) {
        super(request);
        if (extendParams != null) {
            extendParams.remove(Constant.ResponseAbout.SERVICE);
            extendParams.remove(Constant.ResponseAbout.METHOD);
        }

        assert extendParams != null;
        Iterator entries = extendParams.entrySet().iterator();

        while (entries.hasNext()) {

            Map.Entry entry = (Map.Entry) entries.next();

            Object value = entry.getValue();

            if (value instanceof Page || value instanceof BeanPropertyBindingResult) {
                entries.remove();
            }
        }

        extendParams.putAll(request.getParameterMap());
        params = extendParams;
    }

    public Map<String, String[]> getForwardParameterMap() {
        return params;
    }

    public void addParameter(String key, String o) {
        if (this.params.containsKey(key)) {
            String[] value = params.get(key);
            params.put(key, ArrayUtil.add(value, o));
        }
        this.params.put(key, new String[]{o});
    }
}
