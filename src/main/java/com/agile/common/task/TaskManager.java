package com.agile.common.task;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author 佟盟
 * 日期 2019/5/9 16:45
 * 描述 定时任务持久层操作
 * @version 1.0
 * @since 1.0
 */
public interface TaskManager {
    /**
     * 取所有定时任务
     *
     * @return 定时任务列表
     */
    List<Task> getTask();

    /**
     * 根据定时任务标识查询所属任务目标
     *
     * @param code 定时任务标识
     * @return 任务目标列表
     */
    List<Target> getApisByTaskCode(Long code);

    /**
     * 根据定时任务标识查询所属任务目标
     *
     * @param code 定时任务标识
     * @return 任务目标列表
     */
    List<Task> getTasksByApiCode(String code);

    /**
     * 保存任务和对应的任务列表
     *
     * @param task   任务
     * @param methods 目标
     */
    void save(Task task, List<Method> methods);

    /**
     * 删除定时任务
     *
     * @param id 定时任务
     */
    void remove(Long id);

    /**
     * 运行
     *
     * @param taskCode 任务标识
     */
    void run(Long taskCode);

    /**
     * 已完成运行
     *
     * @param taskCode 任务标识
     */
    void finish(Long taskCode);

    /**
     * 记录运行日志
     *
     * @param runDetail 运行信息
     */
    void logging(RunDetail runDetail);
}
