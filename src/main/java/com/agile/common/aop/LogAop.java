package com.agile.common.aop;

import com.agile.common.base.ApiInfo;
import com.agile.common.base.Constant;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.factory.PoolFactory;
import com.agile.common.log.BusinessLogService;
import com.agile.common.log.ServiceExecutionInfo;
import com.agile.common.mvc.service.MainService;
import com.agile.common.util.ApiUtil;
import com.agile.common.util.DateUtil;
import com.agile.common.util.FactoryUtil;
import com.agile.common.util.ServletUtil;
import org.apache.commons.logging.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.util.ProxyUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 佟盟 on 2017/9/24
 * 处理服务调用日志部分
 */
@Component
@Aspect
public class LogAop {

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_IMUM_POOL_SIZE = 30;
    private static final int KEEP_ALIVE_TIME = 1;
    /**
     * 日志模板
     */
    private static final String LOG_TEMPLATE = "%n状    态: %s%nIP  地址: %s%nURL 地址: %s%n服    务: %s%n方    法: %s%n入    参: %n%s%n出    参:%n%s%n耗    时: %sms%n---------------------------------------------------------------------------";
    private static final String ERROR_LOG_TEMPLATE = "%n状    态: %s%n异常类型: %s%n异常信息: %s%nIP  地址: %s%nURL 地址: %s%n服    务: %s%n方    法: %s%n入    参: %n%s%n耗    时: %sms%n---------------------------------------------------------------------------";
    private static final String SUCCESS = "SUCCESS";
    private static final String ERROR = "ERROR";
    private static final String DETAIL_ERROR_INFO = "详细错误信息：\n";

    /**
     * 日志线程池
     */
    private static ThreadPoolExecutor pool = PoolFactory.pool(CORE_POOL_SIZE, MAX_IMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MINUTES, new LinkedBlockingQueue<>(), new ThreadPoolExecutor.DiscardPolicy());

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
        long startTime = System.currentTimeMillis();

        ApiInfo apiInfo = extract(joinPoint);

        if (apiInfo == null || !(apiInfo.getBean() instanceof MainService)) {
            return joinPoint.proceed();
        }
        MainService service = (MainService) apiInfo.getBean();

        ServiceExecutionInfo.ServiceExecutionInfoBuilder builder = ServiceExecutionInfo.builder();

        builder.bean(service)
                .method(apiInfo.getMethod())
                .inParam(service.getInParam())
                .ip(ServletUtil.getCurrentRequestIP())
                .url(ServletUtil.getCurrentRequestUrl())
                .executionDate(DateUtil.getCurrentDate())
                .userDetails((UserDetails) SecurityContextHolder.getContext().getAuthentication().getDetails())
                .status(true)
                .build();
        BusinessLogService logService = FactoryUtil.getBean(BusinessLogService.class);
        if (logService != null) {
            logService.clear();
            logService.initCurrentBusinessLogCode();
        }

        ServiceExecutionInfo serviceExecutionInfo;
        try {
            Object result = joinPoint.proceed();
            builder.timeConsuming(System.currentTimeMillis() - startTime)
                    .outParam(service.getOutParam());
            serviceExecutionInfo = builder.build();
            logging(serviceExecutionInfo);
            printLog(serviceExecutionInfo);
            return result;
        } catch (Throwable throwable) {
            builder.status(false);
            builder.timeConsuming(System.currentTimeMillis() - startTime)
                    .e(throwable);
            serviceExecutionInfo = builder.build();
            logging(serviceExecutionInfo);
            printLog(serviceExecutionInfo);
            throw throwable;
        }
    }

    private ApiInfo extract(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        ApiInfo apiInfo = ApiUtil.getApiCache(ServletUtil.getCurrentRequest());
        if (apiInfo == null || !(apiInfo.getBean() instanceof MainService)) {
            Object bean = joinPoint.getArgs()[Constant.NumberAbout.ZERO];
            Class<?> clazz = ProxyUtils.getUserClass(bean);
            Method method = (Method) joinPoint.getArgs()[Constant.NumberAbout.ONE];
            Method reallyMethod = clazz.getDeclaredMethod(method.getName(), method.getParameterTypes());
            return new ApiInfo(bean, reallyMethod, null, null);
        }
        return apiInfo;
    }

    private void logging(ServiceExecutionInfo serviceExecutionInfo) {
        BusinessLogService logService = FactoryUtil.getBean(BusinessLogService.class);
        if (logService != null) {
            logService.logging(serviceExecutionInfo);
        }
    }

    private void printLog(ServiceExecutionInfo executionInfo) {
        pool.execute(new LogThread(executionInfo));
    }


    /**
     * 日志线程
     */
    private static class LogThread implements Runnable {
        ServiceExecutionInfo serviceExecutionInfo;

        LogThread(ServiceExecutionInfo serviceExecutionInfo) {
            this.serviceExecutionInfo = serviceExecutionInfo;
        }

        @Override
        public void run() {

            Log logger = LoggerFactory.getServiceLog(ProxyUtils.getUserClass(serviceExecutionInfo.getBean()));
            if (serviceExecutionInfo.getE() == null) {
                logger.info(String.format(LOG_TEMPLATE,
                        SUCCESS,
                        serviceExecutionInfo.getIp(),
                        serviceExecutionInfo.getUrl(),
                        serviceExecutionInfo.getBeanName(),
                        serviceExecutionInfo.getMethodName(),
                        serviceExecutionInfo.getInParamToJson(),
                        serviceExecutionInfo.getOutParamToJson(),
                        serviceExecutionInfo.getTimeConsuming()));
            } else {
                logger.error(String.format(ERROR_LOG_TEMPLATE,
                        ERROR,
                        serviceExecutionInfo.getE().getClass(),
                        serviceExecutionInfo.getE().getMessage(),
                        serviceExecutionInfo.getIp(),
                        serviceExecutionInfo.getUrl(),
                        serviceExecutionInfo.getBeanName(),
                        serviceExecutionInfo.getMethodName(),
                        serviceExecutionInfo.getInParamToJson(),
                        serviceExecutionInfo.getTimeConsuming()));

                logger.error(DETAIL_ERROR_INFO,
                        serviceExecutionInfo.getE());
            }
        }
    }
}
