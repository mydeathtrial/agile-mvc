package com.agile.common.task;

import com.agile.common.factory.LoggerFactory;
import com.agile.common.mvc.service.TaskService;
import com.agile.common.util.CacheUtil;
import com.agile.common.util.DateUtil;
import com.agile.common.util.string.StringUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimerTask;

/**
 * @author 佟盟
 * 日期 2020/4/29 17:42
 * 描述 定时任务抽象类
 * @version 1.0
 * @since 1.0
 */
@Setter
@Getter
public class TaskJob extends TimerTask implements Runnable, Cloneable {
    static final String START_TASK = "任务:[%s][开始执行]";
    static final String NO_API_TASK = "任务:[%s][非法任务，未绑定任何api信息，任务结束]";
    static final String ILLEGAL_API_TASK = "任务:[%s][非法任务，入参大于1个，任务结束]";
    static final String EXCEPTION_API_TASK = "任务:[%s][任务异常]";
    static final String RUN_TASK_API = "任务:[%s][执行]";
    static final String EXCEPTION_RUN_TASK_API = "任务:[%s][任务异常]";
    static final String END_TASK = "任务:[%s][任务完成]";
    static final String NEXT_TASK = "任务:[%s][下次执行时间%s]";
    static final String LAST_TASK = "任务:[%s][本次为最后一次执行]";

    final Log logger = LoggerFactory.createLogger("task", TaskJob.class);
    /**
     * 持久层工具
     */
    private TaskManager taskManager;
    private Task task;
    private List<Method> methods;
    /**
     * 任务执行代理
     */
    private TaskProxy taskProxy;

    public TaskJob(TaskManager taskManager, TaskProxy taskProxy, Task task, List<Method> methods) {
        this.taskManager = taskManager;
        this.taskProxy = taskProxy;
        this.task = task;
        this.methods = methods;
    }

    /**
     * 获取分布式锁
     *
     * @param lockName   锁名称
     * @param unlockTime 加锁时间（秒）
     * @return 如果获取到锁，则返回lockKey值，否则为null
     */
    boolean setNxLock(String lockName, Date unlockTime) {
        synchronized (this) {
            //抢占锁
            boolean isLock = CacheUtil.lock(lockName);
            if (isLock) {
                //拿到Lock，设置超时时间
                CacheUtil.unlock(lockName, Duration.ofMillis((unlockTime.getTime() - System.currentTimeMillis()) - 1));
            }
            return isLock;
        }
    }

    @Override
    public void run() {
        synchronized (this) {
            try {
                //重启该定时任务下所有执行器
                TaskService.reStart(task.getCode());

                //获取下次执行时间（秒）
                Date nextTime = TaskService.nextExecutionTimeByTaskCode(getTask().getCode());

                //判断是否需要同步，同步情况下获取同步锁后方可执行，非同步情况下直接运行
                if (getTask().getSync() != null && getTask().getSync()) {
                    //如果抢到同步锁，设置锁定时间并直接运行
                    if (setNxLock(Long.toString(getTask().getCode()), nextTime)) {
                        invoke();
                    }
                } else {
                    invoke();
                }

                if (nextTime == null) {
                    logger.info(String.format(LAST_TASK, getTask().getCode()));
                } else {
                    logger.info(String.format(NEXT_TASK, getTask().getCode(), DateUtil.convertToString(nextTime, "yyyy-MM-dd HH:mm:ss")));
                }
            } catch (Exception e) {
                logger.error(String.format(EXCEPTION_API_TASK, getTask().getCode()), e);
            }
        }
    }

    /**
     * 逐个执行定时任务目标方法
     */
    public void invoke() {
        RunDetail runDetail = RunDetail.builder().taskCode(task.getCode()).startTime(new Date()).ending(true).build();
        start(runDetail);
        running(runDetail);
        end(runDetail);
    }

    void exception(Throwable e, RunDetail runDetail) {
        runDetail.addLog(StringUtil.exceptionToString(e));
        runDetail.setEnding(false);
        if (logger.isErrorEnabled()) {
            logger.error(String.format(EXCEPTION_API_TASK, runDetail.getTaskCode()), e);
        }
    }

    void start(RunDetail runDetail) {
        if (logger.isInfoEnabled()) {
            logger.info(String.format(START_TASK, runDetail.getTaskCode()));
        }
        if (taskManager != null) {
            //通知持久层，任务开始运行
            taskManager.run(runDetail.getTaskCode());
        }

    }

    void end(RunDetail runDetail) {
        if (logger.isInfoEnabled()) {
            logger.info(String.format(END_TASK, runDetail.getTaskCode()));
        }
        if (taskManager != null) {
            runDetail.setEndTime(new Date());
            taskManager.logging(runDetail);
            //通知持久层，任务开始运行
            taskManager.finish(task.getCode());
        }
    }

    private void running(RunDetail runDetail) {
        if (ObjectUtils.isEmpty(getMethods())) {
            String log = String.format(NO_API_TASK, getTask().getCode());
            runDetail.addLog(log);
            if (logger.isErrorEnabled()) {
                logger.error(String.format(log, runDetail.getTaskCode()));
            }
            return;
        }

        getMethods().forEach(method -> {
            String log;
            String code = method.toGenericString();
            if (method.getParameterCount() > 1) {
                log = String.format(ILLEGAL_API_TASK, code);
                runDetail.addLog(log);
                if (logger.isErrorEnabled()) {
                    logger.error(String.format(log, code));
                }
                return;
            }

            Optional.ofNullable(taskProxy).ifPresent(proxy -> {
                try {
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format(RUN_TASK_API, code));
                    }
                    proxy.invoke(method, getTask());
                } catch (InvocationTargetException | IllegalAccessException e) {
                    if (logger.isErrorEnabled()) {
                        logger.error(String.format(EXCEPTION_RUN_TASK_API, code), e);
                    }
                    exception(e, runDetail);
                }
            });
        });
    }

    @Override
    public TaskJob clone() throws CloneNotSupportedException {
        return (TaskJob) super.clone();
    }
}
