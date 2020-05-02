package com.agile.common.task;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

/**
 * @author 佟盟
 * 日期 2020/5/2 17:03
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class InstantActuator extends AbstractActuator<Instant> {
    public InstantActuator(ScheduledFuture<?> scheduledFuture, Instant timeAbout, ThreadPoolTaskScheduler threadPoolTaskScheduler) {
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
        return new Date(getTimeAbout().toEpochMilli());
    }
}
