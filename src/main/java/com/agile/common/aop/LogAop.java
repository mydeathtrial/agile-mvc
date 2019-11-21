package com.agile.common.aop;

import com.agile.common.base.ApiInfo;
import com.agile.common.base.Constant;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.factory.PoolFactory;
import com.agile.common.log.BusinessLogService;
import com.agile.common.log.ServiceExecutionInfo;
import com.agile.common.mvc.service.MainService;
import com.agile.common.util.ApiUtil;
import com.agile.common.util.ClassUtil;
import com.agile.common.util.DateUtil;
import com.agile.common.util.ServletUtil;
import org.apache.commons.logging.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.ProxyUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
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
    @Autowired(required = false)
    private BusinessLogService logService;

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

        ApiInfo apiInfo = extractApiInfo(joinPoint);

        if (apiInfo == null || !(apiInfo.getBean() instanceof MainService)) {
            return joinPoint.proceed();
        }

        ServiceExecutionInfo executionInfo = initServiceExecutionInfoBuilder(apiInfo, extractCurrentUser());

        boolean status = true;
        try {
            clearCurrentBusinessLog();
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            status = false;
            executionInfo.setE(throwable);
            throw throwable;
        } finally {
            executionInfo.setStatus(status);
            executionInfo.setTimeConsuming(startTime);
            executionInfo.setOutParam(((MainService) apiInfo.getBean()).getOutParam());
            logging(executionInfo);
            printLog(executionInfo);
            clearCurrentBusinessLog();
        }
    }

    private void clearCurrentBusinessLog() {
        if (logService != null) {
            logService.clear();
        }
    }

    /**
     * 初始化执行信息
     *
     * @param apiInfo     当前api信息
     * @param userDetails 当前用户信息
     * @return 构建者
     */
    private ServiceExecutionInfo initServiceExecutionInfoBuilder(ApiInfo apiInfo, UserDetails userDetails) {
        MainService service = (MainService) apiInfo.getBean();

        return ServiceExecutionInfo.builder()
                .bean(service)
                .method(apiInfo.getMethod())
                .inParam(service.getInParam())
                .ip(ServletUtil.getCurrentRequestIP())
                .url(ServletUtil.getCurrentRequestUrl())
                .executionDate(DateUtil.getCurrentDate())
                .userDetails(userDetails)
                .status(true)
                .build();
    }

    /**
     * 提取当前用户
     *
     * @return 用户信息
     */
    private UserDetails extractCurrentUser() {
        UserDetails userDetails = null;
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            Authentication authentication = context.getAuthentication();
            if (authentication != null) {
                Object user = authentication.getDetails();
                if (user instanceof UserDetails) {
                    userDetails = (UserDetails) user;
                }
            }
        }
        return userDetails;
    }

    /**
     * 切面中提取方法信息
     *
     * @param joinPoint 切入点
     * @return 方法信息
     */
    private ApiInfo extractApiInfo(ProceedingJoinPoint joinPoint) {
        ApiInfo apiInfo = ApiUtil.getApiCache(ServletUtil.getCurrentRequest());
        if (apiInfo == null || !(apiInfo.getBean() instanceof MainService)) {
            Object bean = joinPoint.getArgs()[Constant.NumberAbout.ZERO];
            Class<?> clazz = ProxyUtils.getUserClass(bean);
            Method method = (Method) joinPoint.getArgs()[Constant.NumberAbout.ONE];
            Method reallyMethod = ClassUtil.getMethod(clazz, method.getName(), method.getParameterTypes());
            return new ApiInfo(bean, reallyMethod);
        }
        return apiInfo;
    }

    /**
     * 记录操作日志
     *
     * @param serviceExecutionInfo 服务执行信息
     */
    private void logging(ServiceExecutionInfo serviceExecutionInfo) {
        if (logService != null) {
            logService.logging(serviceExecutionInfo);
        }
    }

    /**
     * 记录log4j日志
     *
     * @param executionInfo 服务执行信息
     */
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
