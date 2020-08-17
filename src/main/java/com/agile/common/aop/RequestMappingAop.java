package com.agile.common.aop;

import cloud.agileframework.spring.util.ServletUtil;
import com.agile.common.log.BusinessLogService;
import com.agile.common.log.Constant;
import com.agile.common.log.ServiceExecutionInfo;
import com.agile.common.mvc.controller.MainController;
import com.agile.common.param.AgileParam;
import com.alibaba.fastjson.JSON;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.data.util.ProxyUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author 佟盟 on 2017/9/24
 * 处理服务调用日志部分
 */
@Component
@Aspect
@Order
public class RequestMappingAop {
    /**
     * 服务切面
     */
    @Pointcut(value = "@annotation(org.springframework.web.bind.annotation.Mapping)")
    public void controllerPointCut() {
    }

    /**
     * 日志打印环绕通知
     *
     * @param joinPoint 切入点
     * @return 返回切入点方法返回的结果
     * @throws Throwable 异常
     */
    @Around(value = "controllerPointCut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        ServiceExecutionInfo.ServiceExecutionInfoBuilder executionInfoBuilder = initServiceExecutionInfoBuilder(joinPoint);

        Logger logger = Constant.logger(ProxyUtils.getUserClass(joinPoint.getTarget()));
        logger.info(Constant.LOG_START);

        Object out;
        try {
            out = joinPoint.proceed();
            if (joinPoint.getTarget() instanceof MainController) {
                return out;
            }
            executionInfoBuilder.outParam(JSON.parseObject(JSON.toJSONString(out)));
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
    private ServiceExecutionInfo.ServiceExecutionInfoBuilder initServiceExecutionInfoBuilder(ProceedingJoinPoint joinPoint) {
        return ServiceExecutionInfo.builder()
                .ip(ServletUtil.getCurrentRequestIP())
                .url(ServletUtil.getCurrentRequestUrl())
                .executionDate(new Date())
                .userDetails(AgileParam.getUser())
                .startTime(System.currentTimeMillis())
                .bean(joinPoint.getTarget())
                .method(((MethodSignature) joinPoint.getSignature()).getMethod())
                .inParam(AgileParam.getInParam());
    }
}
