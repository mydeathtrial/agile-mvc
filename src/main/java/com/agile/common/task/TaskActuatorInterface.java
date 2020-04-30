package com.agile.common.task;

import java.util.Date;

/**
 * @author 佟盟
 * 日期 2020/4/29 18:47
 * 描述 定时任务信息接口
 * @version 1.0
 * @since 1.0
 */
public interface TaskActuatorInterface {
    /**
     * 取消执行
     */
    void cancel();

    /**
     * 重启
     * @param job 运行的任务
     */
    void reStart(TaskJob job);

    /**
     * 取近期执行时间
     * @return 时间
     */
    Date nextExecutionTime();
}
