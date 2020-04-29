package com.agile.common.task;

import lombok.Getter;
import lombok.Setter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ScheduledFuture;

/**
 * @author 佟盟
 * @version 1.0
 * 日期 2019/2/28 20:17
 * 描述 定时任务信息
 * @since 1.0
 */
@Setter
@Getter
public class CycleTaskInfo extends AbstractTaskInfo<ScheduledFuture<?>> {
    /**
     * 触发器
     */
    private TaskTrigger trigger;

    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    public CycleTaskInfo(ScheduledFuture<?> actuator, TaskJob job, ThreadPoolTaskScheduler threadPoolTaskScheduler, TaskTrigger trigger) {
        super(actuator, job);
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
        this.trigger = trigger;
    }

    @Override
    public void cancel() {
        if (getActuator() == null) {
            return;
        }
        getActuator().cancel(Boolean.TRUE);
        super.cancel();
    }

    @Override
    public void reStart() {
        this.threadPoolTaskScheduler.schedule(getJob(), trigger);
    }
}
