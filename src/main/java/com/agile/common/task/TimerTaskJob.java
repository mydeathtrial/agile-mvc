package com.agile.common.task;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @author 佟盟
 * 日期 2020/4/29 17:33
 * 描述 指定固定时间只运行一次的定时任务
 * @version 1.0
 * @since 1.0
 */
@Setter
@Getter
public class TimerTaskJob extends AbstractJob {

    public TimerTaskJob(TaskManager taskManager, TaskProxy taskProxy, Task task, List<Target> targets) {
        super(taskManager, taskProxy, task, targets);
    }

    @Override
    public void parseLock() {
        //判断是否需要同步，同步情况下获取同步锁后方可执行，非同步情况下直接运行
        if (getTask().getSync() != null && getTask().getSync()) {
            //如果抢到同步锁，设置锁定时间并直接运行
            if (setNxLock(Long.toString(getTask().getCode()), new Date())) {
                invoke();
            }
        } else {
            invoke();
        }
    }
}
