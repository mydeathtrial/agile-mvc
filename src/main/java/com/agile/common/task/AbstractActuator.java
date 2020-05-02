package com.agile.common.task;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ScheduledFuture;

/**
 * @author 佟盟
 * 日期 2020/4/30 15:18
 * 描述 任务调度执行器
 * @param <T> 时间计算器类型
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractActuator<T> implements TaskActuatorInterface {
    private final T timeAbout;
    /**
     * 依赖的线程池
     */
    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;
    /**
     * 执行器
     */
    private ScheduledFuture<?> scheduledFuture;

    public AbstractActuator(ScheduledFuture<?> scheduledFuture, T timeAbout, ThreadPoolTaskScheduler threadPoolTaskScheduler) {
        this.timeAbout = timeAbout;
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
        this.scheduledFuture = scheduledFuture;
    }

    @Override
    public void cancel() {
        if (scheduledFuture == null || scheduledFuture.isCancelled()) {
            return;
        }
        scheduledFuture.cancel(Boolean.TRUE);
    }

    public ThreadPoolTaskScheduler getThreadPoolTaskScheduler() {
        return threadPoolTaskScheduler;
    }

    public ScheduledFuture<?> getScheduledFuture() {
        return scheduledFuture;
    }

    public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    public T getTimeAbout() {
        return timeAbout;
    }
}
