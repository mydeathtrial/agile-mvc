package com.agile.common.task;

import com.agile.common.util.CacheUtil;

/**
 * @author 佟盟
 * 日期 2020/4/29 19:33
 * 描述 定时任务信息公共方法
 * @param <T> 执行器类型
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractTaskInfo<T> implements TaskInfoInterface {
    /**
     * 任务标识
     */
    private final String code;
    /**
     * 任务执行器
     */
    private T actuator;
    /**
     * 执行的任务
     */
    private final AbstractJob job;

    public AbstractTaskInfo(T actuator, AbstractJob job) {
        this.code = job.getTask().getCode().toString();
        this.actuator = actuator;
        this.job = job;
    }

    @Override
    public void cancel() {
        //清锁
        CacheUtil.evict(code);
    }

    public String getCode() {
        return code;
    }

    public T getActuator() {
        return actuator;
    }

    public void setActuator(T actuator) {
        this.actuator = actuator;
    }

    public AbstractJob getJob() {
        return job;
    }
}