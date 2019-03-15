package com.agile.common.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 佟盟
 * @version 1.0
 * 日期 2019/2/28 20:10
 * 描述 定时任务触发器
 * @since 1.0
 */
@Setter
@Getter
@AllArgsConstructor
public class TaskTrigger implements Trigger, Serializable {
    private String cron;
    private boolean  sync;

    @Override
    public Date nextExecutionTime(TriggerContext triggerContext) {
        CronTrigger cronTrigger = new CronTrigger(this.cron);
        return cronTrigger.nextExecutionTime(triggerContext);
    }
}
