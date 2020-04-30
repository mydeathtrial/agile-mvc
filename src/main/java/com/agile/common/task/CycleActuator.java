package com.agile.common.task;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.SimpleTriggerContext;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

/**
 * @author 佟盟
 * 日期 2020/4/30 15:18
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class CycleActuator implements TaskActuatorInterface {
    /**
     * 时间计算器
     */
    private TaskTrigger trigger;
    /**
     * 依赖的线程池
     */
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    /**
     * 执行器
     */
    private ScheduledFuture<?> scheduledFuture;

    public CycleActuator(ScheduledFuture<?> scheduledFuture, TaskTrigger trigger, ThreadPoolTaskScheduler threadPoolTaskScheduler) {
        this.trigger = trigger;
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

    @Override
    public void reStart(TaskJob job) {
        if (scheduledFuture.isCancelled()) {
            scheduledFuture = this.threadPoolTaskScheduler.schedule(job, trigger);
        }
    }

    @Override
    public Date nextExecutionTime() {
        return trigger.nextExecutionTime(new SimpleTriggerContext());
    }
}
