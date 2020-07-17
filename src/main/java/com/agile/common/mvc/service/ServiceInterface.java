package com.agile.common.mvc.service;

import com.agile.common.param.AgileParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Service 层顶级接口
 *
 * @author 佟盟
 */
public interface ServiceInterface {

    /**
     * 设置响应参数
     *
     * @param key   key值
     * @param value value值
     */
    void setOutParam(String key, Object value);

    /**
     * 提取请求参数
     *
     * @return 入参
     */
    Map<String, Object> getInParam();

    /**
     * 提取请求参数
     *
     * @param key 参数索引
     * @return 入参
     */
    Object getInParam(String key);

    /**
     * 从入参中提取对象
     *
     * @param clazz 类型
     * @param <T>   泛型
     * @return 结果
     */
    <T> T getInParam(Class<T> clazz);

    /**
     * 从入参中提取对象
     *
     * @param key   参数key值
     * @param clazz 类型
     * @param <T>   泛型
     * @return 入参
     */
    <T> T getInParam(String key, Class<T> clazz);
}
