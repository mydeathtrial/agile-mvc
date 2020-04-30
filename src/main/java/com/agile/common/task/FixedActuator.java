package com.agile.common.task;

import java.util.Date;
import java.util.Timer;

/**
 * @author 佟盟
 * 日期 2020/4/30 15:11
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class FixedActuator implements TaskActuatorInterface {
    /**
     * 任务执行时间
     */
    private Date date;
    /**
     * 执行器
     */
    private Timer timer;

    public FixedActuator(Timer timer, Date date) {
        this.date = date;
        this.timer = timer;
    }

    @Override
    public void cancel() {
        if (timer == null) {
            return;
        }
        timer.cancel();
        System.gc();
        timer = null;
    }

    @Override
    public void reStart(TaskJob job) {
        if (timer == null && date.getTime() > System.currentTimeMillis()) {
            timer = new Timer();
            timer.schedule(job, date);
        }
    }

    @Override
    public Date nextExecutionTime() {
        return date;
    }
}
