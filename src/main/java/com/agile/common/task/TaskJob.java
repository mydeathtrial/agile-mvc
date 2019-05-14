package com.agile.common.task;

import com.agile.common.base.Constant;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.mvc.service.TaskService;
import com.agile.common.util.FactoryUtil;
import com.agile.common.util.IdUtil;
import com.agile.common.util.ObjectUtil;
import com.agile.common.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.logging.log4j.Level;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    private final Log logger = LoggerFactory.createLogger("task", TaskJob.class, Level.INFO, Level.ERROR);

    private Task task;
    private TaskTrigger trigger;
    private List<Target> targets;

    @Override
    public void run() {
        synchronized (this) {
            try {
                //判断是否需要同步，同步情况下获取同步锁后方可执行，非同步情况下直接运行
                if (this.trigger.isSync()) {
                    //获取下次执行时间（秒）
                    long nextTime = (Objects.requireNonNull(this.trigger.nextExecutionTime(new SimpleTriggerContext())).getTime() - System.currentTimeMillis()) / Constant.NumberAbout.THOUSAND;

                    //如果抢到同步锁，设置锁定时间并直接运行
                    if (setNxLock(Long.toString(this.task.getCode()), (int) nextTime)) {
                        invoke();
                    }
                } else {
                    invoke();
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    /**
     * 获取分布式锁
     *
     * @param lockName 锁名称
     * @param second   加锁时间（秒）
     * @return 如果获取到锁，则返回lockKey值，否则为null
     */
    private boolean setNxLock(String lockName, int second) {
        RedisConnectionFactory redisConnectionFactory = FactoryUtil.getBean(RedisConnectionFactory.class);
        if (redisConnectionFactory == null) {
            return true;
        }
        synchronized (this) {

            RedisConnection connection = redisConnectionFactory.getConnection();

            if (connection == null) {
                return true;
            }
            //生成随机的Value值
            String lockKey = String.valueOf(IdUtil.generatorId());

            if (lockName == null) {
                return true;
            }
            byte[] lockNameBytes = lockName.getBytes(StandardCharsets.UTF_8);
            byte[] lockKeyBytes = lockKey.getBytes(StandardCharsets.UTF_8);

            //抢占锁
            boolean isLock = connection.setNX(lockNameBytes, lockKeyBytes);
            if (isLock) {
                //拿到Lock，设置超时时间
                connection.expire(lockNameBytes, second - 1);
            }
            return isLock;
        }
    }

    /**
     * 逐个执行定时任务目标方法
     */
    @Transactional(rollbackFor = Exception.class)
    public void invoke() {
        TaskManager taskManager = FactoryUtil.getBean(TaskManager.class);
        if (taskManager != null) {
            //通知持久层，任务开始运行
            taskManager.run(task.getCode());
        }
        RunDetail runDetail = RunDetail.builder().taskCode(task.getCode()).startTime(new Date()).build();

        boolean ending = true;
        String log;
        //逐个执行定时任务目标方法
        for (Target target : targets) {
            if (logger.isInfoEnabled()) {
                log = "开始定时任务:" + task.getCode();
                logger.info(log);
            }
            if (ObjectUtil.isEmpty(target)) {
                log = "该定时任务中未绑定任何api信息，任务结束";
                runDetail.addLog(log);
                logger.info(log);
                return;
            }
            ApiBase apiInfo = TaskService.getApi(target.getCode());
            if (apiInfo == null || apiInfo.getMethod().getParameterCount() > 0) {
                log = "该定时任务中绑定的方法不合法，任务结束";
                runDetail.addLog(log);
                logger.info(log);
                return;
            }
            try {
                apiInfo.getMethod().invoke(apiInfo.getBean());
            } catch (Exception e) {
                log = StringUtil.coverToString(e);
                runDetail.addLog(log);
                ending = false;
                logger.info(log);
            }

        }

        if (taskManager != null) {
            runDetail.setEndTime(new Date());
            runDetail.setEnding(ending);
            taskManager.logging(runDetail);
            //通知持久层，任务开始运行
            taskManager.finish(task.getCode());
        }

    }
}
