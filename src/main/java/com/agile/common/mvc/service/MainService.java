package com.agile.common.mvc.service;

import com.agile.common.base.AbstractResponseFormat;
import com.agile.common.base.Constant;
import com.agile.common.base.RETURN;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.mvc.model.dao.Dao;
import com.agile.common.security.SecurityUser;
import com.agile.common.util.ArrayUtil;
import com.agile.common.util.ClassUtil;
import com.agile.common.util.JSONUtil;
import com.agile.common.util.ObjectUtil;
import com.agile.common.util.PropertiesUtil;
import com.agile.mvc.entity.SysUsersEntity;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 佟盟 on 2017/1/9
 */
public class MainService implements ServiceInterface {

    //输入
    private static ThreadLocal<Map<String, Object>> inParam = new ThreadLocal<>();
    //输出
    private static ThreadLocal<Map<String, Object>> outParam = ThreadLocal.withInitial(LinkedHashMap::new);
    @Autowired
    protected Dao dao;
    protected Log logger = LoggerFactory.getServiceLog(this.getClass());

    /**
     * 根据对象及方法名通过反射执行该对象的指定方法
     *
     * @param object 服务子类对象，为解决Hystrix组件无法识别服务子类问题（识别成了父类）
     * @return 返回执行结果
     */
    @Override
    @Transactional
    public Object executeMethod(Object object, Method method, HttpServletRequest currentRequest, HttpServletResponse currentResponse) throws Throwable {

        initOutParam();
        try {
            Object returnData = method.invoke(object);
            if (returnData instanceof AbstractResponseFormat) {//如果是自定义报文bean，则格式化报文
                setOutParam(((AbstractResponseFormat) returnData).buildResponse());
            } else if (returnData instanceof RETURN) {//如果是头信息，则交给控制层处理
                return returnData;
            } else {//其他类型数据直接放入返回参数
                if (returnData != null) {
                    setOutParam(Constant.ResponseAbout.RESULT, returnData);
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
        MainService.inParam.get().put(key, o);
    }

    /**
     * 服务中调用该方法获取入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    protected Object getInParam(String key) {
        return inParam.get().get(key);
    }

    /**
     * 服务中调用该方法获取映射对象
     *
     * @param clazz 参数映射类型
     * @return 入参映射对象
     */
    protected <T> T getInParam(Class<T> clazz) {
        T o = null;
        Object json = getInParam(Constant.ResponseAbout.BODY);
        if (json != null) {
            o = JSONUtil.toBean(clazz, (JSONObject) json);
        }
        if (o == null) {
            o = ObjectUtil.getObjectFromMap(clazz, this.getInParam());
        }
        return o;
    }

    /**
     * 服务中调用该方法获取映射对象
     *
     * @param clazz  参数映射类型
     * @param prefix 筛选参数前缀
     * @return 入参映射对象
     */
    protected <T> T getInParam(Class<T> clazz, String prefix) {
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
    protected <T> T getInParam(Class<T> clazz, String prefix, String suffix) {
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
        if (!containsKey(key)) {
            return null;
        }
        Object v = getInParam(key);
        T value;
        if (v instanceof JSONObject) {
            value = PropertiesUtil.getObjectFromJson(clazz, (JSONObject) getInParam(key));
        } else {
            if (ClassUtil.isCustomClass(clazz)) {
                value = getInParam(key, clazz, null);
            } else {
                value = ObjectUtil.cast(clazz, v);
            }
        }

        return value;
    }

    /**
     * 服务中调用该方法获取指定类型入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    protected <T> T getInParam(String key, Class<T> clazz, T defaultValue) {
        if (!inParam.get().containsKey(key)) {
            return defaultValue;
        }
        Object o;
        try {
            Object[] value = (Object[]) inParam.get().get(key);
            o = value[0];
        } catch (ClassCastException e) {
            o = inParam.get().get(key);
        }
        T result = ObjectUtil.cast(clazz, o);
        return ObjectUtil.isEmpty(result) ? null : result;
    }

    /**
     * 服务中调用该方法获取字符串数组入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    protected String[] getInParamOfArray(String key) {
        return (String[]) inParam.get().get(key);
    }

    /**
     * 服务中调用该方法获取指定类型入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    protected <T> T[] getInParamOfArray(String key, Class<T> clazz) {
        String[] value = (String[]) inParam.get().get(key);
        if (value != null && value.length > 0) {
            return ArrayUtil.cast(clazz, value);
        }
        return null;
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
        return inParam.get();
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
    public void setOutParam(Map map) {
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
    public SecurityUser getUser() {
        try {
            return (SecurityUser) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getDetails();
        } catch (Exception e) {
            return new SecurityUser(SysUsersEntity.builder().setName("土豆").setSaltKey("admin").setSaltValue("密码").setSysUsersId("123456").build(), null);
        }
    }
}
