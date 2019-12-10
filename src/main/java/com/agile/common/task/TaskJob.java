package com.agile.common.task;

import com.agile.common.factory.LoggerFactory;
import com.agile.common.mvc.service.TaskService;
import com.agile.common.util.CacheUtil;
import com.agile.common.util.DateUtil;
import com.agile.common.util.ObjectUtil;
import com.agile.common.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author 佟盟
 * @version 1.0
 * 日期 2019/2/28 20:13
 * 描述 定时任务
 * @since 1.0
 */
@Setter
@Getter
@AllArgsConstructor
public class TaskJob implements Serializable, Runnable {

    private static final String START_TASK = "任务:[%s][开始执行]";
    private static final String NO_API_TASK = "任务:[%s][非法任务，未绑定任何api信息，任务结束]";
    private static final String ILLEGAL_API_TASK = "任务:[%s][非法任务，入参大于1个，任务结束]";
    private static final String EXCEPTION_API_TASK = "任务:[%s][任务异常]";
    private static final String RUN_TASK_API = "任务:[%s][%s][执行]";
    private static final String EXCEPTION_RUN_TASK_API = "任务:[%s][%s][任务异常]";
    private static final String END_TASK = "任务:[%s][任务完成]";
    private static final String NEXT_TASK = "任务:[%s][下次执行时间%s]";

    private final Log logger = LoggerFactory.createLogger("task", TaskJob.class);
    /**
     * 持久层工具
     */
    private TaskManager taskManager;
    /**
     * 任务执行代理
     */
    private TaskProxy taskProxy;
    private Task task;
    private TaskTrigger trigger;
    private List<Target> targets;

    @Override
    public void run() {
        synchronized (this) {
            try {
                //获取下次执行时间（秒）
                Date nextTime = trigger.nextExecutionTime(new SimpleTriggerContext());

                //判断是否需要同步，同步情况下获取同步锁后方可执行，非同步情况下直接运行
                if (trigger.getSync()) {
                    //如果抢到同步锁，设置锁定时间并直接运行
                    if (setNxLock(Long.toString(task.getCode()), nextTime)) {
                        invoke();
                    }
                } else {
                    invoke();
                }
                logger.info(String.format(NEXT_TASK, task.getCode(), DateUtil.convertToString(nextTime, "yyyy-MM-dd HH:mm:ss")));
            } catch (Exception e) {
                logger.error(String.format(EXCEPTION_API_TASK, task.getCode()), e);
            }
        }
    }

    /**
     * 获取分布式锁
     *
     * @param lockName   锁名称
     * @param unlockTime 加锁时间（秒）
     * @return 如果获取到锁，则返回lockKey值，否则为null
     */
    private boolean setNxLock(String lockName, Date unlockTime) {
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


    /**
     * 逐个执行定时任务目标方法
     */
    @Transactional(rollbackFor = Exception.class)
    public void invoke() {
        RunDetail runDetail = RunDetail.builder().taskCode(task.getCode()).startTime(new Date()).ending(true).build();
        start(runDetail);
        running(runDetail);
        end(runDetail);
    }

    private void exception(Throwable e, RunDetail runDetail) {
        runDetail.addLog(StringUtil.coverToString(e));
        runDetail.setEnding(false);
        if (logger.isErrorEnabled()) {
            logger.error(String.format(EXCEPTION_API_TASK, runDetail.getTaskCode()), e);
        }
    }

    private void start(RunDetail runDetail) {
        if (logger.isInfoEnabled()) {
            logger.info(String.format(START_TASK, runDetail.getTaskCode()));
        }
        if (taskManager != null) {
            //通知持久层，任务开始运行
            taskManager.run(runDetail.getTaskCode());
        }

    }

    private void running(RunDetail runDetail) {
        if (ObjectUtil.isEmpty(targets)) {
            String log = String.format(NO_API_TASK, task.getCode());
            runDetail.addLog(log);
            if (logger.isErrorEnabled()) {
                logger.error(String.format(log, runDetail.getTaskCode()));
            }
            return;
        }

        targets.forEach(target -> {
            String log;
            ApiBase apiInfo = TaskService.getApi(target.getCode());
            if (apiInfo == null) {
                log = String.format(NO_API_TASK, task.getCode());
                runDetail.addLog(log);
                if (logger.isErrorEnabled()) {
                    logger.error(String.format(log, target.getCode()));
                }
                return;
            }
            if (apiInfo.getMethod().getParameterCount() > 1) {
                log = String.format(ILLEGAL_API_TASK, target.getCode());
                runDetail.addLog(log);
                if (logger.isErrorEnabled()) {
                    logger.error(String.format(log, target.getCode()));
                }
                return;
            }

            Optional.ofNullable(taskProxy).ifPresent((proxy) -> {
                try {
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format(RUN_TASK_API, target.getCode(), apiInfo.getMethod().toGenericString()));
                    }
                    proxy.invoke(apiInfo, task);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    if (logger.isErrorEnabled()) {
                        logger.error(String.format(EXCEPTION_RUN_TASK_API, target.getCode(), apiInfo.getMethod().toGenericString()), e);
                    }
                    exception(e, runDetail);
                }
            });
        });
    }

    private void end(RunDetail runDetail) {
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
}
