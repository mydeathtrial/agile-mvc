package com.agile.common.mvc.service;

import com.agile.common.base.Constant;
import com.agile.common.base.RETURN;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.mvc.model.dao.Dao;
import com.agile.common.param.AgileParam;
import com.agile.common.security.CustomerUserDetails;
import com.agile.common.util.clazz.TypeReference;
import com.agile.common.util.object.ObjectUtil;
import com.alibaba.fastjson.JSONValidator;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 佟盟 on 2017/1/9
 */
public class MainService implements ServiceInterface {

    /**
     * 入参
     */
    private static final ThreadLocal<AgileParam> IN_PARAM = new ThreadLocal<>();

    /**
     * 输出
     */
    private static final ThreadLocal<Map<String, Object>> OUT_PARAM = ThreadLocal.withInitial(LinkedHashMap::new);
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
    public Object executeMethod(Object object, Method method, AgileParam agileParam, HttpServletRequest currentRequest, HttpServletResponse currentResponse) throws Throwable {

        setInParam(agileParam);
        clearOutParam();
        try {
            Object returnData = method.invoke(object);
            if (returnData instanceof RETURN) {
                //如果是头信息，则交给控制层处理
                return returnData;
            } else {
                //其他类型数据直接放入返回参数
                if (returnData != null) {
                    setOutParam(returnData);
                }
            }
            return returnData;
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    /**
     * 服务中调用该方法获取入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    @Override
    public Object getInParam(String key) {
        AgileParam agileParam = IN_PARAM.get();
        return agileParam.getInParam(key);
    }


    /**
     * 服务中调用该方法获取映射对象
     *
     * @param clazz 参数映射类型
     * @return 入参映射对象
     */
    @Override
    public <T> T getInParam(Class<T> clazz) {
        AgileParam agileParam = IN_PARAM.get();
        return agileParam.getInParam(clazz);
    }

    /**
     * 服务中调用该方法获取映射对象
     *
     * @param clazz  参数映射类型
     * @param prefix 筛选参数前缀
     * @return 入参映射对象
     */
    protected <T> T getInParamByPrefix(Class<T> clazz, String prefix) {
        AgileParam agileParam = IN_PARAM.get();
        return agileParam.getInParamByPrefix(clazz, prefix);
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
        AgileParam agileParam = IN_PARAM.get();
        return agileParam.getInParamByPrefixAndSuffix(clazz, prefix, suffix);
    }

    /**
     * 服务中调用该方法获取入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    protected String getInParam(String key, String defaultValue) {
        AgileParam agileParam = IN_PARAM.get();
        return agileParam.getInParam(key, defaultValue);
    }

    /**
     * 服务中调用该方法获取指定类型入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    @Override
    public <T> T getInParam(String key, Class<T> clazz) {
        AgileParam agileParam = IN_PARAM.get();
        return agileParam.getInParam(key, clazz);
    }

    /**
     * 取path下入参，转换为指定泛型
     *
     * @param key       参数path
     * @param reference 泛型
     * @param <T>       泛型
     * @return 转换后的入参
     */
    public <T> T getInParam(String key, TypeReference<T> reference) {
        AgileParam agileParam = IN_PARAM.get();
        return agileParam.getInParam(key, reference);
    }

    /**
     * 服务中调用该方法获取指定类型入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    protected <T> T getInParam(String key, Class<T> clazz, T defaultValue) {
        AgileParam agileParam = IN_PARAM.get();
        return agileParam.getInParam(key, clazz, defaultValue);
    }

    /**
     * 获取上传文件
     *
     * @param key key值
     * @return 文件
     */
    protected MultipartFile getInParamOfFile(String key) {
        AgileParam agileParam = IN_PARAM.get();
        return agileParam.getInParamOfFile(key);
    }

    /**
     * 获取上传文件
     *
     * @param key key值
     * @return 文件
     */
    protected List<MultipartFile> getInParamOfFiles(String key) {
        AgileParam agileParam = IN_PARAM.get();
        return agileParam.getInParamOfFiles(key);
    }

    /**
     * 服务中调用该方法获取字符串数组入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    protected List<String> getInParamOfArray(String key) {
        AgileParam agileParam = IN_PARAM.get();
        return agileParam.getInParamOfArray(key);
    }

    /**
     * 服务中调用该方法获取指定类型入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    protected <T> List<T> getInParamOfArray(String key, Class<T> clazz) {
        AgileParam agileParam = IN_PARAM.get();
        return agileParam.getInParamOfArray(key, clazz);
    }

    /**
     * 服务中调用该方判断是否存在入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    protected boolean containsKey(String key) {
        AgileParam agileParam = IN_PARAM.get();
        return agileParam.containsKey(key);
    }

    /**
     * 服务中调用该方法获取入参集合
     *
     * @return 入参集合
     */
    @Override
    public Map<String, Object> getInParam() {
        AgileParam agileParam = IN_PARAM.get();
        return agileParam.getInParam();
    }


    /**
     * 控制层中调用该方法设置服务入参
     *
     * @param inParam 参数集
     */
    @Override
    public void setInParam(AgileParam inParam) {
        MainService.IN_PARAM.set(inParam);
    }

    /**
     * 控制层中调用该方法获取响应参数
     *
     * @return 响应参数集
     */
    @Override
    public Map<String, Object> getOutParam() {
        return OUT_PARAM.get();
    }

    /**
     * 服务中调用该方法设置响应参数
     */
    @SuppressWarnings("unchecked")
    public void setOutParam(Object outParam) {
        if (Map.class.isAssignableFrom(outParam.getClass())) {
            OUT_PARAM.get().putAll((Map<? extends String, ?>) outParam);
        } else {

            if (outParam instanceof String && !JSONValidator.from((String) outParam).validate()) {
                OUT_PARAM.get().put(Constant.ResponseAbout.RESULT, outParam);
            } else {
                Map<String, Object> map = ObjectUtil.to(outParam, new TypeReference<Map<String, Object>>() {
                });
                if (map == null) {
                    OUT_PARAM.get().put(Constant.ResponseAbout.RESULT, outParam);
                } else {
                    OUT_PARAM.get().putAll(map);
                }
            }

        }
    }

    /**
     * 服务中调用该方法设置响应参数
     *
     * @param key   参数索引字符串
     * @param value 参数值
     */
    @Override
    public void setOutParam(String key, Object value) {
        OUT_PARAM.get().put(key, value);
    }

    /**
     * 清理
     */
    @Override
    public void clearInParam() {
        IN_PARAM.remove();
    }

    /**
     * 清理
     */
    @Override
    public void clearOutParam() {
        OUT_PARAM.remove();
    }

    /**
     * 获取当前用户信息
     */
    public CustomerUserDetails getUser() {
        AgileParam agileParam = IN_PARAM.get();
        return agileParam.getUser();
    }

    public Log getLogger() {
        return logger;
    }
}
