package com.agile.common.task;

import com.agile.common.base.Constant;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.mvc.service.TaskService;
import com.agile.common.util.FactoryUtil;
import com.agile.common.util.IdUtil;
import com.agile.common.util.ObjectUtil;
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
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
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

    private String taskName;
    private TaskTrigger trigger;
    private List<Target> taskTargetList;

    @Override
    public void run() {
        synchronized (this) {
            try {
                //判断是否需要同步，同步情况下获取同步锁后方可执行，非同步情况下直接运行
                if (this.trigger.isSync()) {
                    //获取下次执行时间（秒）
                    long nextTime = (Objects.requireNonNull(this.trigger.nextExecutionTime(new SimpleTriggerContext())).getTime() - System.currentTimeMillis()) / Constant.NumberAbout.THOUSAND;

                    //如果抢到同步锁，设置锁定时间并直接运行
                    if (setNxLock(this.taskName, (int) nextTime)) {
                        invoke(taskTargetList);
                    }
                } else {
                    invoke(taskTargetList);
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
     *
     * @param targets 定时任务详情数据集
     */
    @Transactional(rollbackFor = Exception.class)
    public void invoke(List<Target> targets) throws InvocationTargetException, IllegalAccessException {
        //逐个执行定时任务目标方法
        for (Target target : targets) {
            if (logger.isInfoEnabled()) {
                logger.info("开始定时任务:" + target.getCode());
            }
            if (ObjectUtil.isEmpty(target)) {
                return;
            }
            ApiBase apiInfo = TaskService.getApi(target.getCode());
            if (apiInfo == null || apiInfo.getMethod().getParameterCount() > 0) {
                logger.error("定时任务异常:" + target.getCode());
                return;
            }
            apiInfo.getMethod().invoke(apiInfo.getBean());
        }
    }

//    public static void main(String[] args) throws NoSuchMethodException {
//        System.out.println(SyncThreatBookThread.class.getMethod("sync").toGenericString());
//    }
}
