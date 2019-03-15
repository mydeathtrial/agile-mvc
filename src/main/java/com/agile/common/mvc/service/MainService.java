package com.agile.common.mvc.service;

import com.agile.common.base.Constant;
import com.agile.common.base.RETURN;
import com.agile.common.exception.NoSignInException;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.mvc.model.dao.Dao;
import com.agile.common.security.CustomerUserDetails;
import com.agile.common.util.ArrayUtil;
import com.agile.common.util.JSONUtil;
import com.agile.common.util.ObjectUtil;
import com.agile.common.util.ViewUtil;
import com.fasterxml.jackson.databind.JsonNode;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 佟盟 on 2017/1/9
 */
public class MainService implements ServiceInterface {

    /**
     *
     */
    private static ThreadLocal<Map<String, Object>> inParam = new ThreadLocal<>();
    /**
     * 输出
     */
    private static ThreadLocal<Map<String, Object>> outParam = ThreadLocal.withInitial(LinkedHashMap::new);
    @Autowired(required = false)
    protected Dao dao;
    protected Log logger = LoggerFactory.getServiceLog(this.getClass());

    /**
     * 根据对象及方法名通过反射执行该对象的指定方法
     *
     * @param object 服务子类对象，为解决Hystrix组件无法识别服务子类问题（识别成了父类）
     * @return 返回执行结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object executeMethod(Object object, Method method, HttpServletRequest currentRequest, HttpServletResponse currentResponse) throws Throwable {

        initOutParam();
        try {
            Object returnData = method.invoke(object);
            if (returnData instanceof RETURN) {
                //如果是头信息，则交给控制层处理
                return returnData;
            } else {
                //其他类型数据直接放入返回参数
                if (returnData != null) {
                    setOutParam(Constant.ResponseAbout.RETURN, returnData);
                }
            }
            return returnData;
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    /**
     * 控制层中调用该方法设置服务入参
     *
     * @param key 参数key
     * @param o   参数value
     */
    @Override
    public void setInParam(String key, Object o) {
        inParam.get().put(key, o);
    }

    /**
     * 服务中调用该方法获取入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    protected Object getInParam(String key) {
        return getInParam().get(key);
    }

    /**
     * 服务中调用该方法获取映射对象
     *
     * @param clazz 参数映射类型
     * @return 入参映射对象
     */
    @Override
    public <T> T getInParam(Class<T> clazz) {
        T result = null;
        Object jsonNode = getInParam(Constant.ResponseAbout.BODY);
        if (jsonNode != null) {
            result = JSONUtil.toBean(clazz, jsonNode.toString());
        }
        if (result == null || ObjectUtil.isAllNullValidity(result)) {
            result = ObjectUtil.getObjectFromMap(clazz, this.getInParam());
        }
        return result;
    }

    /**
     * 服务中调用该方法获取映射对象
     *
     * @param clazz  参数映射类型
     * @param prefix 筛选参数前缀
     * @return 入参映射对象
     */
    protected <T> T getInParamByPrefix(Class<T> clazz, String prefix) {
        return ObjectUtil.getObjectFromMap(clazz, this.getInParam(), prefix);
    }

    /**
     * 服务中调用该方法获取映射对象
     *
     * @param clazz  参数映射类型
     * @param prefix 筛选参数前缀
     * @param suffix 筛选参数后缀
     * @return 入参映射对象
     */
    protected <T> T getInParamByPrefixAndSuffix(Class<T> clazz, String prefix, String suffix) {
        return ObjectUtil.getObjectFromMap(clazz, this.getInParam(), prefix, suffix);
    }

    /**
     * 服务中调用该方法获取入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    protected String getInParam(String key, String defaultValue) {
        Object value = inParam.get().get(key);
        if (ObjectUtil.isEmpty(value)) {
            Object body = inParam.get().get(Constant.ResponseAbout.BODY);
            if (body != null) {
                JsonNode v = ((JsonNode) body).get(key);
                if (v != null) {
                    return v.asText();
                }
            }
            return defaultValue;
        }
        return String.valueOf(value);
    }

    /**
     * 服务中调用该方法获取指定类型入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    @Override
    public <T> T getInParam(String key, Class<T> clazz) {
        return ViewUtil.getInParam(getInParam(), key, clazz);
    }

    /**
     * 服务中调用该方法获取指定类型入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    protected <T> T getInParam(String key, Class<T> clazz, T defaultValue) {
        T result = getInParam(key, clazz);

        if (result == null) {
            return defaultValue;
        }

        return result;
    }

    /**
     * 服务中调用该方法获取字符串数组入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    protected List<String> getInParamOfArray(String key) {
        return getInParamOfArray(key, String.class);
    }

    /**
     * 服务中调用该方法获取指定类型入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    protected <T> List<T> getInParamOfArray(String key, Class<T> clazz) {
        Object o = getInParam().get(key);
        if (o == null) {
            return null;
        }
        if (Iterable.class.isAssignableFrom(o.getClass())) {
            return ArrayUtil.cast(clazz, (Iterable) o);
        } else if (o.getClass().isArray()) {
            return ArrayUtil.cast(clazz, ArrayUtil.asList((Object[]) o));
        } else {
            return null;
        }
    }

    /**
     * 服务中调用该方判断是否存在入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    protected boolean containsKey(String key) {
        return inParam.get().containsKey(key);
    }

    /**
     * 服务中调用该方法获取入参集合
     *
     * @return 入参集合
     */
    @Override
    public Map<String, Object> getInParam() {
        Map<String, Object> map = inParam.get();
        Object body = map.get(Constant.ResponseAbout.BODY);
        if (body != null) {
            Map<String, Object> result = new HashMap<>(map);

            String bodyString = body.toString();
            if (bodyString != null) {
                JSON json = JSONUtil.toJSON(bodyString);
                if (JSONObject.class.isAssignableFrom(json.getClass())) {
                    Map<String, Object> bodyParam = JSONUtil.jsonObjectCoverMap((JSONObject) json);
                    if (bodyParam != null) {
                        result.putAll(bodyParam);
                    }
                } else if (JSONArray.class.isAssignableFrom(json.getClass())) {
                    List bodyParam = JSONUtil.jsonArrayCoverArray((JSONArray) json);
                    if (bodyParam != null) {
                        result.put(Constant.ResponseAbout.BODY, bodyParam);
                    }
                }
            }
            return result;
        }
        return map;
    }


    /**
     * 控制层中调用该方法设置服务入参
     *
     * @param inParam 参数集
     */
    @Override
    public void setInParam(Map<String, Object> inParam) {
        MainService.inParam.set(inParam);
    }

    /**
     * 控制层中调用该方法获取响应参数
     *
     * @return 响应参数集
     */
    @Override
    public Map<String, Object> getOutParam() {
        return outParam.get();
    }

    /**
     * 服务中调用该方法设置响应参数
     */
    public <V> void setOutParam(Map<? extends String, ? extends V> map) {
        outParam.get().putAll(map);
    }

    /**
     * 服务中调用该方法设置响应参数
     *
     * @param key   参数索引字符串
     * @param value 参数值
     */
    @Override
    public void setOutParam(String key, Object value) {
        outParam.get().put(key, value);
    }

    /**
     * 清理
     */
    @Override
    public void initInParam() {
        inParam.remove();
    }

    /**
     * 清理
     */
    @Override
    public void initOutParam() {
        outParam.remove();
    }

    /**
     * 获取当前用户信息
     */
    public CustomerUserDetails getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new NoSignInException("账号尚未登陆，服务中无法获取登陆信息");
        } else {
            return (CustomerUserDetails) authentication.getDetails();
        }
    }
}
