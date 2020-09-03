package cloud.agileframework.mvc.param;

import cloud.agileframework.common.util.clazz.TypeReference;
import cloud.agileframework.common.util.object.ObjectUtil;
import cloud.agileframework.spring.util.ParamUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 佟盟
 * 日期 2020/6/1 14:20
 * 描述 参数解析器
 * @version 1.0
 * @since 1.0
 */
@JsonIgnoreProperties(value = "user")
public class AgileParam {
    private static final ThreadLocal<Map<String, Object>> PARAMS = ThreadLocal.withInitial(HashMap::new);

    private AgileParam() {
    }

    public static void init(Map<String, Object> sourceParam) {
        PARAMS.set(sourceParam);
    }

    public static Map<String, Object> getInParam() {
        return PARAMS.get();
    }

    public static boolean containsKey(String key) {
        return ParamUtil.containsKey(getInParam(), key);
    }

    /**
     * 服务中调用该方法获取入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    public static Object getInParam(String key) {
        return ParamUtil.getInParam(getInParam(), key);
    }


    /**
     * 服务中调用该方法获取映射对象
     *
     * @param clazz 参数映射类型
     * @return 入参映射对象
     */
    public static <T> T getInParam(Class<T> clazz) {
        return ParamUtil.getInParam(getInParam(), clazz);
    }

    /**
     * 服务中调用该方法获取映射对象
     *
     * @param clazz  参数映射类型
     * @param prefix 筛选参数前缀
     * @return 入参映射对象
     */
    public static <T> T getInParamByPrefix(Class<T> clazz, String prefix) {
        return ObjectUtil.getObjectFromMap(clazz, getInParam(), prefix);
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
        return ObjectUtil.getObjectFromMap(clazz, getInParam(), prefix, suffix);
    }

    /**
     * 服务中调用该方法获取入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    public static String getInParam(String key, String defaultValue) {
        return ParamUtil.getInParam(getInParam(), key, defaultValue);
    }

    /**
     * 服务中调用该方法获取指定类型入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    public static <T> T getInParam(String key, Class<T> clazz) {
        return ParamUtil.getInParam(getInParam(), key, clazz);
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
        return ParamUtil.getInParam(getInParam(), key, reference);
    }

    /**
     * 服务中调用该方法获取指定类型入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    public static <T> T getInParam(String key, Class<T> clazz, T defaultValue) {
        return ParamUtil.getInParam(getInParam(), key, clazz, defaultValue);
    }

    /**
     * 获取上传文件
     *
     * @param key key值
     * @return 文件
     */
    public static MultipartFile getInParamOfFile(String key) {
        return ParamUtil.getInParamOfFile(getInParam(), key);
    }

    /**
     * 获取上传文件
     *
     * @param key key值
     * @return 文件
     */
    public static List<MultipartFile> getInParamOfFiles(String key) {
        return ParamUtil.getInParamOfFiles(getInParam(), key);
    }

    /**
     * 服务中调用该方法获取字符串数组入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    public static List<String> getInParamOfArray(String key) {
        return getInParamOfArray(key, String.class);
    }

    /**
     * 服务中调用该方法获取指定类型入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    public static <T> List<T> getInParamOfArray(String key, Class<T> clazz) {
        return ParamUtil.getInParamOfArray(getInParam(), key, clazz);
    }

    public static void clear() {
        PARAMS.remove();
    }
}