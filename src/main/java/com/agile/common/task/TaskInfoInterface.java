package com.agile.common.task;

/**
 * @author 佟盟
 * 日期 2020/4/29 18:47
 * 描述 定时任务信息接口
 * @version 1.0
 * @since 1.0
 */
public interface TaskInfoInterface {
    /**
     * 取消执行
     */
    void cancel();

    /**
     * 重启
     */
    void reStart();
}
