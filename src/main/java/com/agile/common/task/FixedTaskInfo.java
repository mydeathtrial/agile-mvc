package com.agile.common.task;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Timer;

/**
 * @author 佟盟
 * 日期 2020/4/29 19:02
 * 描述 固定时间任务
 * @version 1.0
 * @since 1.0
 */
@Setter
@Getter
public class FixedTaskInfo extends AbstractTaskInfo<Timer> {
    /**
     * 任务执行时间
     */
    private Date date;

    public FixedTaskInfo(Timer actuator, TimerTaskJob job, Date date) {
        super(actuator, job);
        this.date = date;
    }

    @Override
    public void cancel() {
        if (getActuator() == null) {
            return;
        }
        getActuator().cancel();
        System.gc();
        super.cancel();
    }

    @Override
    public void reStart() {
        Timer timer = new Timer();
        //新建任务
        timer.schedule(getJob(), date);
        setActuator(timer);
    }
}
