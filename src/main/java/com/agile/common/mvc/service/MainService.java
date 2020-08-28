package com.agile.common.mvc.service;

import cloud.agileframework.common.util.clazz.TypeReference;
import cloud.agileframework.jpa.dao.Dao;
import com.agile.common.annotation.AgileService;
import com.agile.common.param.AgileParam;
import com.agile.common.param.AgileReturn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author 佟盟 on 2017/1/9
 */
@AgileService
public class MainService {

    @Autowired(required = false)
    protected Dao dao;
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 服务中调用该方法获取入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    public Object getInParam(String key) {
        return AgileParam.getInParam(key);
    }


    /**
     * 服务中调用该方法获取映射对象
     *
     * @param clazz 参数映射类型
     * @return 入参映射对象
     */
    public <T> T getInParam(Class<T> clazz) {
        return AgileParam.getInParam(clazz);
    }

    /**
     * 服务中调用该方法获取映射对象
     *
     * @param clazz  参数映射类型
     * @param prefix 筛选参数前缀
     * @return 入参映射对象
     */
    protected <T> T getInParamByPrefix(Class<T> clazz, String prefix) {
        return AgileParam.getInParamByPrefix(clazz, prefix);
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
        return AgileParam.getInParamByPrefixAndSuffix(clazz, prefix, suffix);
    }

    /**
     * 服务中调用该方法获取入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    protected String getInParam(String key, String defaultValue) {
        return AgileParam.getInParam(key, defaultValue);
    }

    /**
     * 服务中调用该方法获取指定类型入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    public <T> T getInParam(String key, Class<T> clazz) {
        return AgileParam.getInParam(key, clazz);
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
        return AgileParam.getInParam(key, reference);
    }

    /**
     * 服务中调用该方法获取指定类型入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    protected <T> T getInParam(String key, Class<T> clazz, T defaultValue) {
        return AgileParam.getInParam(key, clazz, defaultValue);
    }

    /**
     * 获取上传文件
     *
     * @param key key值
     * @return 文件
     */
    protected MultipartFile getInParamOfFile(String key) {
        return AgileParam.getInParamOfFile(key);
    }

    /**
     * 获取上传文件
     *
     * @param key key值
     * @return 文件
     */
    protected List<MultipartFile> getInParamOfFiles(String key) {
        return AgileParam.getInParamOfFiles(key);
    }

    /**
     * 服务中调用该方法获取字符串数组入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    protected List<String> getInParamOfArray(String key) {
        return AgileParam.getInParamOfArray(key);
    }

    /**
     * 服务中调用该方法获取指定类型入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    protected <T> List<T> getInParamOfArray(String key, Class<T> clazz) {
        return AgileParam.getInParamOfArray(key, clazz);
    }

    /**
     * 服务中调用该方判断是否存在入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    protected boolean containsKey(String key) {
        return AgileParam.containsKey(key);
    }

    /**
     * 服务中调用该方法获取入参集合
     *
     * @return 入参集合
     */
    public Map<String, Object> getInParam() {
        return AgileParam.getInParam();
    }

    /**
     * 服务中调用该方法设置响应参数
     */
    @SuppressWarnings("unchecked")
    public void setOutParam(Object outParam) {
        AgileReturn.add(outParam);
    }

    /**
     * 服务中调用该方法设置响应参数
     *
     * @param key   参数索引字符串
     * @param value 参数值
     */
    public void setOutParam(String key, Object value) {
        AgileReturn.add(key, value);
    }

    /**
     * 获取当前用户信息
     */
    public UserDetails getUser() {
        return AgileParam.getUser();
    }
}
