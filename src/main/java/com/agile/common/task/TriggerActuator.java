package com.agile.common.task;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.SimpleTriggerContext;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

/**
 * @author 佟盟
 * 日期 2020/5/2 16:56
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class TriggerActuator extends AbstractActuator<Trigger> {
    public TriggerActuator(ScheduledFuture<?> scheduledFuture, Trigger timeAbout, ThreadPoolTaskScheduler threadPoolTaskScheduler) {
        super(scheduledFuture, timeAbout, threadPoolTaskScheduler);
    }

    @Override
    public void reStart(TaskJob job) {
        if (getScheduledFuture().isCancelled()) {
            setScheduledFuture(getThreadPoolTaskScheduler().schedule(job, getTimeAbout()));
        }
    }

    @Override
    public Date nextExecutionTime() {
        return getTimeAbout().nextExecutionTime(new SimpleTriggerContext());
    }
}
