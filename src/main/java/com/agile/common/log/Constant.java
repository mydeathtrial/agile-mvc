package com.agile.common.log;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author 佟盟
 * 日期 2020/7/13 18:33
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class Constant {
    private Constant() {
    }

    public static final Map<Class<?>, Logger> LOGGERS = Maps.newHashMap();
    /**
     * 日志模板
     */
    public static final String LOG_START = "---------------------------------------------------------------------------";
    public static final String LOG_TEMPLATE = "\n状    态: {}" +
            "\nIP  地址: {}" +
            "\nURL 地址: {}" +
            "\n帐    号: {}" +
            "\n服    务: {}" +
            "\n方    法: {}" +
            "\n入    参:\n{}" +
            "\n出    参:\n{}" +
            "\n耗    时: {}ms";
    public static final String ERROR_LOG_TEMPLATE = "\n状    态: {}" +
            "\n异    常: {}" +
            "\n信    息: {}" +
            "\nIP  地址: {}" +
            "\nURL 地址: {}" +
            "\n帐    号: {}" +
            "\n服    务: {}" +
            "\n方    法: {}" +
            "\n入    参: \n{}" +
            "\n耗    时: {}ms";
    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";

    public static Logger logger(Class<?> clazz) {
        return Constant.LOGGERS.computeIfAbsent(clazz, a -> {
            Logger l = LoggerFactory.getLogger(a);
            Constant.LOGGERS.put(clazz, l);
            return l;
        });
    }

    /**
     * 记录log4j日志
     *
     * @param serviceExecutionInfo 服务执行信息
     */
    public static void printLog(Logger logger, ServiceExecutionInfo serviceExecutionInfo) {

        if (serviceExecutionInfo.getE() == null) {
            logger.info(Constant.LOG_TEMPLATE,
                    Constant.SUCCESS,
                    serviceExecutionInfo.getIp(),
                    serviceExecutionInfo.getUrl(),
                    serviceExecutionInfo.getUsername(),
                    serviceExecutionInfo.getBeanName(),
                    serviceExecutionInfo.getMethodName(),
                    serviceExecutionInfo.getInParamToJson(),
                    serviceExecutionInfo.getOutParamToJson(),
                    serviceExecutionInfo.getTimeConsuming());
        } else {
            logger.error(Constant.ERROR_LOG_TEMPLATE,
                    Constant.ERROR,
                    serviceExecutionInfo.getE().getClass(),
                    serviceExecutionInfo.getE().getMessage(),
                    serviceExecutionInfo.getIp(),
                    serviceExecutionInfo.getUrl(),
                    serviceExecutionInfo.getUsername(),
                    serviceExecutionInfo.getBeanName(),
                    serviceExecutionInfo.getMethodName(),
                    serviceExecutionInfo.getInParamToJson(),
                    serviceExecutionInfo.getTimeConsuming());
        }
    }
}
