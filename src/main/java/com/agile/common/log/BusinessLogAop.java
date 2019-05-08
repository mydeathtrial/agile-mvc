package com.agile.common.log;

import com.agile.common.factory.PoolFactory;
import com.agile.common.mvc.service.BusinessLogService;
import com.agile.common.mvc.service.MainService;
import com.agile.common.util.AopUtil;
import com.agile.common.util.DateUtil;
import com.agile.common.util.ServletUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 佟盟
 * 日期 2019/5/7 11:35
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@Component
@Aspect
public class BusinessLogAop {
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_IMUM_POOL_SIZE = 30;
    private static final int KEEP_ALIVE_TIME = 1;
    private static ThreadPoolExecutor pool = PoolFactory.pool(CORE_POOL_SIZE, MAX_IMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MINUTES, new LinkedBlockingQueue<>(), new ThreadPoolExecutor.DiscardPolicy());

    @Autowired
    private BusinessLogService businessLogService;

    /**
     * 服务切面
     */
    @Pointcut(value = "@annotation(com.agile.common.log.BusinessLog)")
    public void businessServicePointCut() {
    }

    @Around(value = "businessServicePointCut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        Date startDate = DateUtil.getCurrentDate();
        boolean status = true;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            status = false;
        }
        ServiceExecutionInfo.ServiceExecutionInfoBuilder builder = ServiceExecutionInfo.builder();
        MainService service = AopUtil.getTarget(joinPoint, MainService.class);

        if (service != null) {
            BusinessLog businessLog = service.getClass().getDeclaredAnnotation(BusinessLog.class);
            builder.bean(service)
                    .method(AopUtil.getMethd(joinPoint))
                    .inParam(service.getInParam())
                    .outParam(service.getOutParam())
                    .businessLog(businessLog)
                    .ip(ServletUtil.getCurrentRequestIP())
                    .url(ServletUtil.getCurrentRequestUrl())
                    .status(status)
                    .executionDate(DateUtil.getCurrentDate())
                    .userDetails(service.getUser())
                    .timeConsuming(DateUtil.getInterval(startDate, DateUtil.getCurrentDate()))
                    .build();

            pool.execute(new BusinessLogThread(builder.build()));
        }


        return result;
    }

    /**
     * 业务日志记录线程
     */
    private class BusinessLogThread implements Runnable {
        private ServiceExecutionInfo serviceExecutionInfo;

        BusinessLogThread(ServiceExecutionInfo serviceExecutionInfo) {
            this.serviceExecutionInfo = serviceExecutionInfo;
        }

        @Override
        public void run() {
            businessLogService.saveLog(serviceExecutionInfo);
        }
    }
}
