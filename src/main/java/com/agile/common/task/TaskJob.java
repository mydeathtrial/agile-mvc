package com.agile.common.task;

import com.agile.common.util.DateUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.scheduling.support.SimpleTriggerContext;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

/**
 * @author 佟盟
 * @version 1.0
 * 日期 2019/2/28 20:13
 * 描述 定时任务
 * @since 1.0
 */
@Setter
@Getter
public class TaskJob extends AbstractJob {


    private TaskTrigger trigger;

    public TaskJob(TaskManager taskManager, TaskProxy taskProxy, Task task, TaskTrigger trigger, List<Method> methods) {
        super(taskManager, taskProxy, task, methods);
        this.trigger = trigger;
    }

    @Override
    public void parseLock() {
        //获取下次执行时间（秒）
        Date nextTime = trigger.nextExecutionTime(new SimpleTriggerContext());

        //判断是否需要同步，同步情况下获取同步锁后方可执行，非同步情况下直接运行
        if (trigger.getSync() != null && trigger.getSync()) {
            //如果抢到同步锁，设置锁定时间并直接运行
            if (setNxLock(Long.toString(getTask().getCode()), nextTime)) {
                invoke();
            }
        } else {
            invoke();
        }
        logger.info(String.format(NEXT_TASK, getTask().getCode(), DateUtil.convertToString(nextTime, "yyyy-MM-dd HH:mm:ss")));

    }


}
