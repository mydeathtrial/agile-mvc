package com.agile.common.mvc.service;

import com.agile.common.annotation.Init;
import com.agile.common.base.RETURN;
import com.agile.common.exception.NoSuchIDException;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.util.CacheUtil;
import com.agile.common.util.FactoryUtil;
import com.agile.common.util.ObjectUtil;
import com.agile.mvc.entity.SysTaskEntity;
import com.agile.mvc.entity.SysTaskTargetEntity;
import org.apache.commons.logging.Log;
import org.springframework.context.ApplicationContext;
import org.springframework.data.util.ProxyUtils;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

/**
 * @author 佟盟 on 2018/2/2
 *
 * @author 佟盟
 */
public class TaskService extends BusinessService<SysTaskEntity> {
    private static Map<String, TaskInfo> taskInfoMap = new HashMap<>();
    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;
    private final ApplicationContext applicationContext;
    private Log log = LoggerFactory.TASK_LOG;

    public TaskService(ThreadPoolTaskScheduler threadPoolTaskScheduler, ApplicationContext applicationContext) {
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
        this.applicationContext = applicationContext;
    }

    /**
     * 逐个执行定时任务目标方法
     *
     * @param sysTaskTargetEntityList 定时任务详情数据集
     */
    @Transactional
    public void invoke(List<SysTaskTargetEntity> sysTaskTargetEntityList) {
        try {
            //逐个执行定时任务目标方法
            for (int i = 0; i < sysTaskTargetEntityList.size(); i++) {
                SysTaskTargetEntity sysTaskTargetEntity = sysTaskTargetEntityList.get(i);
                if (log.isInfoEnabled()) {
                    log.info("开始定时任务:" + sysTaskTargetEntity.getName());
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
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                if (e instanceof ClassNotFoundException | e instanceof IllegalAccessException) {
                    log.error("定时任务中描述的Class类没有找到");
                } else if (e instanceof NoSuchMethodException) {
                    log.error("定时任务中描述的目标方法没有找到");
                } else {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * spring容器初始化时初始化全部定时任务
     */
    @Init
    @Transactional
    public void init() {
        initTaskTarget();
        //获取持久层定时任务数据集
        List<SysTaskEntity> list = dao.findAll(SysTaskEntity.class);
        for (int i = 0; i < list.size(); i++) {
            SysTaskEntity sysTaskEntity = list.get(i);
            addTask(sysTaskEntity);
        }
    }

    /**
     * 初始化全部任务目标方法
     */
    private void initTaskTarget() {
        String[] beans = applicationContext.getBeanNamesForAnnotation(Service.class);
        List<SysTaskTargetEntity> list = dao.findAll(SysTaskTargetEntity.class);
        Map<String, SysTaskTargetEntity> mapCache = new HashMap<>();
        for (SysTaskTargetEntity entity : list) {
            mapCache.put(entity.getSysTaskTargetId(), entity);
        }
        for (String beanName : beans) {
            Object bean = applicationContext.getBean(beanName);
            if (bean == null) {
                continue;
            }
            Class<?> clazz = ProxyUtils.getUserClass(bean.getClass());
            Method[] methods = clazz.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if (method.getParameterCount() > 0) {
                    continue;
                }
                String methodName = method.getName();

                String id = clazz.getName() + "." + methodName;
                SysTaskTargetEntity newData = SysTaskTargetEntity.builder().sysTaskTargetId(id)
                        .targetPackage(clazz.getPackage().getName())
                        .targetClass(clazz.getSimpleName())
                        .targetMethod(methodName)
                        .name(id).build();
                SysTaskTargetEntity oldData = mapCache.get(id);
                if (oldData == null) {
                    dao.save(newData);
                } else if (!ObjectUtil.compareValue(newData, oldData, "name")) {
                    dao.update(newData);
                }
            }
        }
    }

    /**
     * 根据定时任务对象添加定时任务
     *
     * @return 是否添加成功
     */
    private boolean addTask(SysTaskEntity sysTaskEntity) {
        try {
            //获取定时任务详情列表
            List<SysTaskTargetEntity> sysTaskTargetEntityList = dao.findAll(
                    "select a.* from sys_task_target a left join sys_bt_task_target b on b.sys_task_target_id = a.sys_task_target_id where b.sys_task_id = ? order by b.order",
                    SysTaskTargetEntity.class,
                    sysTaskEntity.getSysTaskId());

            if (ObjectUtil.isEmpty(sysTaskTargetEntityList)) {
                return true;
            }

            //新建定时任务触发器
            TaskTrigger trigger = new TaskTrigger(sysTaskEntity.getCron(), sysTaskEntity.getSync());

            //新建任务
            TaskService.Job job = new TaskService.Job(sysTaskEntity.getName(), trigger, sysTaskTargetEntityList);

            ScheduledFuture scheduledFuture = null;
            if (sysTaskEntity.getState()) {
                scheduledFuture = threadPoolTaskScheduler.schedule(job, trigger);
            }

            //定时任务装入缓冲区
            taskInfoMap.put(sysTaskEntity.getSysTaskId(), new TaskInfo(sysTaskEntity, trigger, job, scheduledFuture));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 根据定时任务对象添加定时任务
     *
     * @return 是否添加成功
     */
    public RETURN addTask() {
        SysTaskEntity entity = ObjectUtil.getObjectFromMap(SysTaskEntity.class, this.getInParam());
        if (!ObjectUtil.isValidity(entity)) {
            return RETURN.PARAMETER_ERROR;
        }
        dao.save(entity);
        if (this.addTask(entity)) {
            return RETURN.SUCCESS;
        }
        return RETURN.EXPRESSION;
    }

    /**
     * 删除定时任务
     *
     * @return 是否成功
     */
    public RETURN removeTask() throws NoSuchIDException {
        String id = this.getInParam("id", String.class);
        if (this.removeTask(id)) {
            this.dao.deleteById(SysTaskEntity.class, id);
            return RETURN.SUCCESS;
        }
        return RETURN.EXPRESSION;
    }

    private boolean removeTask(String id) {
        if (taskInfoMap.containsKey(id)) {
            if (!stopTask(id)) {
                return false;
            }
            taskInfoMap.remove(id);
        }
        return true;
    }

    /**
     * 停止定时任务
     *
     * @return 是否成功
     */
    public RETURN stopTask() {
        String id = this.getInParam("id", String.class);
        if (this.stopTask(id)) {
            SysTaskEntity entity = dao.findOne(SysTaskEntity.class, id);
            entity.setState(false);
            dao.update(entity);
            return RETURN.SUCCESS;
        }
        return RETURN.EXPRESSION;
    }

    private boolean stopTask(String id) {
        try {
            TaskInfo taskInfo = taskInfoMap.get(id);
            if (ObjectUtil.isEmpty(taskInfo)) {
                return true;
            }
            ScheduledFuture future = taskInfo.getScheduledFuture();
            if (ObjectUtil.isEmpty(future)) {
                return true;
            }
            future.cancel(Boolean.TRUE);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 开启定时任务
     *
     * @return 是否成功
     */
    public RETURN startTask() {
        try {
            String id = this.getInParam("id", String.class);
            TaskInfo taskInfo = taskInfoMap.get(id);
            if (ObjectUtil.isEmpty(taskInfo)) {
                return RETURN.EXPRESSION;
            }
            ScheduledFuture future = this.threadPoolTaskScheduler.schedule(taskInfo.getJob(), taskInfo.getTrigger());
            taskInfo.setScheduledFuture(future);

            SysTaskEntity entity = dao.findOne(SysTaskEntity.class, id);
            entity.setState(true);
            dao.update(entity);
            return RETURN.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return RETURN.EXPRESSION;
        }
    }

    /**
     * 更新定时任务
     *
     * @return 是否成功
     */
    public RETURN updateTask() {
        SysTaskEntity entity = ObjectUtil.getObjectFromMap(SysTaskEntity.class, this.getInParam());
        if (ObjectUtil.isEmpty(entity.getSysTaskId())) {
            return RETURN.PARAMETER_ERROR;
        }
        dao.update(entity);
        if (this.updateTask(entity)) {
            return RETURN.SUCCESS;
        }
        return RETURN.EXPRESSION;
    }

    private boolean updateTask(SysTaskEntity sysTaskEntity) {
        try {
            if (!removeTask(sysTaskEntity.getSysTaskId())) {
                return false;
            }
            return addTask(sysTaskEntity);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public RETURN query() {
        final int defPage = 0;
        final int defSize = 10;
        int page = this.getInParam("page", Integer.class, defPage);
        int size = this.getInParam("size", Integer.class, defSize);
        this.setOutParam("queryList", dao.findAll(SysTaskEntity.class, page, size));
        return RETURN.SUCCESS;
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
     * 定时任务信息
     */
    public class TaskInfo {
        /**
         * 触发器
         */
        private TaskTrigger trigger;
        /**
         * 任务
         */
        private TaskService.Job job;
        private ScheduledFuture scheduledFuture;

        TaskInfo(SysTaskEntity sysTaskEntity, TaskTrigger trigger, TaskService.Job job, ScheduledFuture scheduledFuture) {
            ObjectUtil.copyProperties(sysTaskEntity, this);
            this.trigger = trigger;
            this.job = job;
            this.scheduledFuture = scheduledFuture;
        }

        TaskTrigger getTrigger() {
            return trigger;
        }

        public void setTrigger(TaskTrigger trigger) {
            this.trigger = trigger;
        }

        TaskService.Job getJob() {
            return job;
        }

        public void setJob(TaskService.Job job) {
            this.job = job;
        }

        ScheduledFuture getScheduledFuture() {
            return scheduledFuture;
        }

        void setScheduledFuture(ScheduledFuture scheduledFuture) {
            this.scheduledFuture = scheduledFuture;
        }
    }

    /**
     * 定时任务触发器
     */
    public class TaskTrigger implements Trigger, Serializable {
        private String cron;
        private boolean sync;

        TaskTrigger(String cron, boolean sync) {
            this.cron = cron;
            this.sync = sync;
        }

        @Override
        public Date nextExecutionTime(TriggerContext triggerContext) {
            CronTrigger cronTrigger = new CronTrigger(this.cron);
            return cronTrigger.nextExecutionTime(triggerContext);
        }

        public String getCron() {
            return cron;
        }

        public void setCron(String cron) {
            this.cron = cron;
        }

        public boolean isSync() {
            return sync;
        }

        public void setSync(boolean sync) {
            this.sync = sync;
        }
    }

    /**
     * 任务对象
     */
    public class Job implements Serializable, Runnable {
        private static final long serialVersionUID = 1352043270981352844L;

        private String taskName;
        private TaskTrigger trigger;
        private List<SysTaskTargetEntity> sysTaskTargetEntityList;
        private static final int TIME_UNIT = 1000;

        Job(String taskName, TaskTrigger trigger, List<SysTaskTargetEntity> sysTaskTargetEntityList) {
            this.taskName = taskName;
            this.trigger = trigger;
            this.sysTaskTargetEntityList = sysTaskTargetEntityList;
        }

        @Override
        public void run() {
            synchronized (this) {
                //判断是否需要同步，同步情况下获取同步锁后方可执行，非同步情况下直接运行
                if (this.trigger.isSync()) {
                    //获取下次执行时间（秒）
                    long nextTime = (Objects.requireNonNull(this.trigger.nextExecutionTime(new SimpleTriggerContext())).getTime() - System.currentTimeMillis()) / TIME_UNIT;

                    //如果抢到同步锁，设置锁定时间并直接运行
                    if (setNxLock(this.taskName, (int) nextTime)) {
                        invoke(sysTaskTargetEntityList);
                    }
                } else {
                    invoke(sysTaskTargetEntityList);
                }
            }
        }
    }

}
