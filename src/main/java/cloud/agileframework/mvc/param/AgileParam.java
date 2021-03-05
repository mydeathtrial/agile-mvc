package cloud.agileframework.mvc.param;

import cloud.agileframework.common.util.clazz.TypeReference;
import cloud.agileframework.spring.util.RequestWrapper;
import cloud.agileframework.spring.util.ServletUtil;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author 佟盟
 * 日期 2020/6/1 14:20
 * 描述 参数解析器
 * @version 1.0
 * @since 1.0
 */
public final class AgileParam {

    private AgileParam() {
    }

    public static Map<String, Object> getInParam() {
        RequestWrapper wrapper = getRequestWrapper();
        return wrapper.getInParam();
    }

    /**
     * 取当前的请求
     *
     * @return 当前的包装后的请求
     */
    private static RequestWrapper getRequestWrapper() {
        final HttpServletRequest currentRequest = ServletUtil.getCurrentRequest();
        return RequestWrapper.extract(currentRequest);
    }

    public static boolean containsKey(String key) {
        return getRequestWrapper().containsKey(key);
    }

    /**
     * 服务中调用该方法获取入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    public static Object getInParam(String key) {
        return getRequestWrapper().getInParam(key);
    }


    /**
     * 服务中调用该方法获取映射对象
     *
     * @param clazz 参数映射类型
     * @return 入参映射对象
     */
    public static <T> T getInParam(Class<T> clazz) {
        return getRequestWrapper().getInParam(clazz);
    }

    /**
     * 服务中调用该方法获取映射对象
     *
     * @param typeReference 参数映射类型
     * @return 入参映射对象
     */
    public static <T> T getInParam(TypeReference<T> typeReference) {
        return getRequestWrapper().getInParam(typeReference);
    }

    /**
     * 服务中调用该方法获取映射对象
     *
     * @param clazz  参数映射类型
     * @param prefix 筛选参数前缀
     * @return 入参映射对象
     */
    public static <T> T getInParamByPrefix(Class<T> clazz, String prefix) {
        return getRequestWrapper().getInParamByPrefix(clazz, prefix);
    }

    /**
     * 服务中调用该方法获取映射对象
     *
     * @param clazz  参数映射类型
     * @param prefix 筛选参数前缀
     * @param suffix 筛选参数后缀
     * @return 入参映射对象
     */
    public static <T> T getInParamByPrefixAndSuffix(Class<T> clazz, String prefix, String suffix) {
        return getRequestWrapper().getInParamByPrefixAndSuffix(clazz, prefix, suffix);
    }

    /**
     * 服务中调用该方法获取入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    public static String getInParam(String key, String defaultValue) {
        return getRequestWrapper().getInParam(key, defaultValue);
    }

    /**
     * 服务中调用该方法获取指定类型入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    public static <T> T getInParam(String key, Class<T> clazz) {
        return getRequestWrapper().getInParam(key, clazz);
    }

    /**
     * 取path下入参，转换为指定泛型
     *
     * @param key       参数path
     * @param reference 泛型
     * @param <T>       泛型
     * @return 转换后的入参
     */
    public static <T> T getInParam(String key, TypeReference<T> reference) {
        return getRequestWrapper().getInParam(key, reference);
    }

    /**
     * 服务中调用该方法获取指定类型入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    public static <T> T getInParam(String key, Class<T> clazz, T defaultValue) {
        return getRequestWrapper().getInParam(key, clazz, defaultValue);
    }

    /**
     * 获取上传文件
     *
     * @param key key值
     * @return 文件
     */
    public static MultipartFile getInParamOfFile(String key) {
        return getRequestWrapper().getInParamOfFile(key);
    }

    /**
     * 获取上传文件
     *
     * @param key key值
     * @return 文件
     */
    public static List<MultipartFile> getInParamOfFiles(String key) {
        return getRequestWrapper().getInParamOfFiles(key);
    }

    /**
     * 服务中调用该方法获取字符串数组入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    public static List<String> getInParamOfArray(String key) {
        return getRequestWrapper().getInParamOfArray(key);
    }

    /**
     * 服务中调用该方法获取指定类型入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    public static <T> List<T> getInParamOfArray(String key, Class<T> clazz) {
        return getRequestWrapper().getInParamOfArray(key, clazz);
    }
}
