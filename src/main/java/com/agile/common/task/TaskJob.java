package com.agile.common.task;

import com.agile.common.base.Constant;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.util.CacheUtil;
import com.agile.common.util.FactoryUtil;
import com.agile.common.util.ObjectUtil;
import com.agile.mvc.entity.SysTaskTargetEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.logging.log4j.Level;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
    private List<SysTaskTargetEntity> sysTaskTargetEntityList;

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
                        invoke(sysTaskTargetEntityList);
                    }
                } else {
                    invoke(sysTaskTargetEntityList);
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
        synchronized (this) {
            //生成随机的Value值
            String lockKey = UUID.randomUUID().toString();
            //抢占锁
            Long lock = CacheUtil.setNx(lockName, lockKey);
            if (lock == 1) {
                //拿到Lock，设置超时时间
                CacheUtil.expire(lockName, second - 1);
            }
            return lock == 1;
        }
    }

    /**
     * 逐个执行定时任务目标方法
     *
     * @param sysTaskTargetEntityList 定时任务详情数据集
     */
    @Transactional(rollbackFor = Exception.class)
    public void invoke(List<SysTaskTargetEntity> sysTaskTargetEntityList) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //逐个执行定时任务目标方法
        for (SysTaskTargetEntity sysTaskTargetEntity : sysTaskTargetEntityList) {
            if (logger.isInfoEnabled()) {
                logger.info("开始定时任务:" + sysTaskTargetEntity.getName());
            }
            if (ObjectUtil.isEmpty(sysTaskTargetEntity)) {
                return;
            }
            String className = sysTaskTargetEntity.getTargetPackage() + "." + sysTaskTargetEntity.getTargetClass();
            Class<?> clazz = Class.forName(className);
            Object targetBaen = FactoryUtil.getBean(clazz);
            Method taretMethod = clazz.getDeclaredMethod(sysTaskTargetEntity.getTargetMethod());
            taretMethod.setAccessible(true);
            taretMethod.invoke(targetBaen);
        }
    }
}
