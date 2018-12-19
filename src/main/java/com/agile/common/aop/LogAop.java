package com.agile.common.aop;

import com.agile.common.factory.LoggerFactory;
import com.agile.common.factory.PoolFactory;
import com.agile.common.mvc.service.MainService;
import com.agile.common.util.JSONUtil;
import com.agile.common.util.MapUtil;
import com.agile.common.util.ServletUtil;
import org.apache.commons.logging.Log;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by 佟盟 on 2017/9/24
 * 处理服务调用日志部分
 */
@Component
@Aspect
public class LogAop {

    private static final int MAX_LENGTH = 5000;
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_IMUM_POOL_SIZE = 30;
    private static final int KEEP_ALIVE_TIME = 1;
    //日志线程池
    private static ThreadPoolExecutor pool = PoolFactory.pool(CORE_POOL_SIZE, MAX_IMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MINUTES, new LinkedBlockingQueue<>(), new ThreadPoolExecutor.DiscardPolicy());

    //日志模板
    private static final String LOG_TEMPLATE = "\n状    态: %s\nIP  地址: %s\nURL 地址: %s\n服    务: %s\n方    法: %s\n入    参: \n%s\n出参:\n%s\n耗    时: %sms\n---------------------------------------------------------------------------";
    private static final String ERROR_LOG_TEMPLATE = "\n状    态: %s\n异常类型: %s\n异常信息: %s\nIP  地址: %s\nURL 地址: %s\n服    务: %s\n方    法: %s\n入    参: \n%s\n耗    时: %sms\n---------------------------------------------------------------------------";
    private static final String SUCCESS = "SUCCESS";
    private static final String ERROR = "ERROR";
    private static final String DETAIL_ERROR_INFO = "详细错误信息：\n";
    private static final int LOG_TAB = 10;
    private static final int REQUEST_INDEX = 2;
    private static final int METHOD_INDEX = 1;

    /**
     * 服务切面
     */
    @Pointcut(value = "execution(* com.agile.common.mvc.service.ServiceInterface.executeMethod(..))")
    public void servicePointCut() {
    }

    /**
     * 日志打印环绕通知
     *
     * @param joinPoint 切入点
     * @return 返回切入点方法返回的结果
     * @throws Throwable 异常
     */
    @Around(value = "servicePointCut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result;
        long startTime = System.currentTimeMillis();
        long endTime;
        MainService service = getService(joinPoint);
        HttpServletRequest request = (HttpServletRequest) joinPoint.getArgs()[REQUEST_INDEX];
        Method method = (Method) joinPoint.getArgs()[METHOD_INDEX];
        Map<String, Object> inParam = service.getInParam();
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            endTime = System.currentTimeMillis();
            printLog(throwable, endTime - startTime, service, method.getName(), ServletUtil.getCustomerIPAddr(request), ServletUtil.getCurrentUrl(request), inParam);
            throw throwable;
        }
        endTime = System.currentTimeMillis();
        printLog(endTime - startTime, service, method.getName(), ServletUtil.getCustomerIPAddr(request), ServletUtil.getCurrentUrl(request), inParam, service.getOutParam());
        return result;
    }

    /**
     * 打印日志
     *
     * @param time       耗时
     * @param service    service
     * @param methodName 方法
     * @param ip         请求ip
     * @param url        请求地址
     * @param inParam    入参
     * @param outParam   出参
     */
    private void printLog(long time, MainService service, String methodName, String ip, String url, Map<String, Object> inParam, Map<String, Object> outParam) {
        LogThread thread = new LogThread(time, service, methodName, ip, url, inParam, outParam);
        pool.execute(thread);
    }

    /**
     * 打印异常日志
     *
     * @param throwable  异常
     * @param time       耗时
     * @param service    service
     * @param methodName 方法
     * @param ip         请求ip
     * @param url        请求地址
     * @param inParam    入参
     */
    private void printLog(Throwable throwable, long time, MainService service, String methodName, String ip, String url, Map<String, Object> inParam) {
        LogThread thread = new LogThread(throwable, time, service, methodName, ip, url, inParam);
        pool.execute(thread);
    }

    /**
     * 从切面中获取触发切面的service
     *
     * @param joinPoint 触发切面的切入点
     * @return 返回住service
     */
    private MainService getService(JoinPoint joinPoint) {
        return (MainService) joinPoint.getTarget();
    }

    /**
     * 日志线程
     */
    private class LogThread implements Runnable {
        Throwable e;
        long time;
        Object service;
        String methodName;
        String ip;
        String url;
        Map<String, Object> inParam;
        Map<String, Object> outParam;

        LogThread(long time, MainService service, String methodName, String ip, String url, Map<String, Object> inParam, Map<String, Object> outParam) {
            this(null, time, service, methodName, ip, url, inParam);
            this.outParam = MapUtil.coverCanSerializer(outParam);
        }

        LogThread(Throwable e, long time, MainService service, String methodName, String ip, String url, Map<String, Object> inParam) {
            this.e = e;
            this.time = time;
            this.service = service;
            this.methodName = methodName;
            this.ip = ip;
            this.url = url;
            this.inParam = MapUtil.coverCanSerializer(inParam);
        }

        @Override
        public void run() {
            try {
                Class<?> serviceClass = service.getClass();
                Log logger = LoggerFactory.getServiceLog(serviceClass);
                if (e == null) {
                    String outStr = JSONUtil.toStringPretty(outParam, LOG_TAB);
                    String print = (outStr != null && outStr.length() > MAX_LENGTH) ? outStr.substring(0, MAX_LENGTH) + "...}" : outStr;
                    logger.info(String.format(LOG_TEMPLATE, SUCCESS, ip, url, serviceClass.getSimpleName(), methodName, JSONUtil.toStringPretty(inParam, LOG_TAB), print, time));
                } else {
                    logger.error(String.format(ERROR_LOG_TEMPLATE, ERROR, e.getClass(), e.getMessage(), ip, url, serviceClass.getSimpleName(), methodName, JSONUtil.toStringPretty(inParam, LOG_TAB), time));
                    logger.error(DETAIL_ERROR_INFO, e);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
