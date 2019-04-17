package com.agile.common.factory;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.concurrent.ScheduledFuture;

/**
 * @author 佟盟 on 2017/11/29
 */
public class TaskFactory {
    private static TaskFactory taskFactory;
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    private static final int POOL_SIZE = 10;

    public TaskFactory() {
    }

    public static TaskFactory after(ThreadPoolTaskScheduler threadPoolTaskScheduler) {
        taskFactory = new TaskFactory();
        taskFactory.threadPoolTaskScheduler = threadPoolTaskScheduler;
        return taskFactory;
    }

    public static void insert(Runnable task, String cron) {
        taskFactory.threadPoolTaskScheduler.setPoolSize(POOL_SIZE);
        taskFactory.threadPoolTaskScheduler.initialize();
        taskFactory.threadPoolTaskScheduler.schedule(task, new CronTrigger(cron));
    }

    public static void update(Runnable task, String cron) {
        ScheduledFuture<?> scheduledFuture = taskFactory.threadPoolTaskScheduler.schedule(task, new CronTrigger(cron));
        scheduledFuture.cancel(Boolean.TRUE);
        taskFactory.threadPoolTaskScheduler.schedule(task, new CronTrigger(cron));
    }

    public static void delete(Runnable task, String cron) {
        ScheduledFuture<?> scheduledFuture = taskFactory.threadPoolTaskScheduler.schedule(task, new CronTrigger(cron));
        scheduledFuture.cancel(Boolean.TRUE);
    }
}
