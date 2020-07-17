package com.agile.common.aop;

import com.agile.common.log.BusinessLogService;
import com.agile.common.log.Constant;
import com.agile.common.log.ServiceExecutionInfo;
import com.agile.common.mvc.controller.MainController;
import com.agile.common.param.AgileParam;
import com.agile.common.param.AgileReturn;
import com.agile.common.util.DateUtil;
import com.agile.common.util.ServletUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.data.util.ProxyUtils;
import org.springframework.stereotype.Component;

/**
 * @author 佟盟 on 2017/9/24
 * 处理服务调用日志部分
 */
@Component
@Aspect
@Order
public class MappingAop {

    /**
     * 服务切面
     */
    @Pointcut(value = "@annotation(com.agile.common.annotation.Mapping)")
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
    public Object serviceLogAround(ProceedingJoinPoint joinPoint) throws Throwable {
        ServiceExecutionInfo.ServiceExecutionInfoBuilder executionInfoBuilder = initServiceExecutionInfoBuilder();

        //取日志工具
        Logger logger = Constant.logger(ProxyUtils.getUserClass(MainController.getService()));
        logger.info(Constant.LOG_START);

        Object out;
        try {
            out = joinPoint.proceed();
            executionInfoBuilder.outParam(AgileReturn.getBody());
        } catch (Throwable throwable) {
            executionInfoBuilder.e(throwable);
            throw throwable;
        } finally {
            ServiceExecutionInfo executionInfo = executionInfoBuilder.build();
            BusinessLogService.info(executionInfo);
            Constant.printLog(logger, executionInfo);
        }
        return out;
    }

    /**
     * 初始化执行信息
     *
     * @return 构建者
     */
    private ServiceExecutionInfo.ServiceExecutionInfoBuilder initServiceExecutionInfoBuilder() {
        return ServiceExecutionInfo.builder()
                .ip(ServletUtil.getCurrentRequestIP())
                .url(ServletUtil.getCurrentRequestUrl())
                .executionDate(DateUtil.getCurrentDate())
                .userDetails(AgileParam.getUser())
                .startTime(System.currentTimeMillis())
                .bean(MainController.getService())
                .method(MainController.getMethod())
                .inParam(AgileParam.getInParam());
    }


}
