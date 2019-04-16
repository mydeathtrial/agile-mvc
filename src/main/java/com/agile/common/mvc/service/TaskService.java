package com.agile.common.mvc.service;

import com.agile.common.annotation.Init;
import com.agile.common.base.RETURN;
import com.agile.common.exception.NotFoundTaskException;
import com.agile.common.task.TaskInfo;
import com.agile.common.task.TaskJob;
import com.agile.common.task.TaskTrigger;
import com.agile.common.util.ObjectUtil;
import com.agile.mvc.entity.SysTaskEntity;
import com.agile.mvc.entity.SysTaskTargetEntity;
import org.springframework.context.ApplicationContext;
import org.springframework.data.util.ProxyUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * @author 佟盟 on 2018/2/2
 * @author 佟盟
 */
public class TaskService extends BusinessService<SysTaskEntity> {
    private static Map<String, TaskInfo> taskInfoMap = new HashMap<>();

    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;
    private final ApplicationContext applicationContext;

    public TaskService(ThreadPoolTaskScheduler threadPoolTaskScheduler, ApplicationContext applicationContext) {
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
        this.applicationContext = applicationContext;
    }

    /**
     * spring容器初始化时初始化全部定时任务
     */
    @Init
    @Transactional(rollbackFor = Exception.class)
    public void init() {
        initTaskTarget();
        //获取持久层定时任务数据集
        List<SysTaskEntity> list = dao.findAll(SysTaskEntity.class);
        for (SysTaskEntity sysTaskEntity : list) {
            addTask(sysTaskEntity);
        }
    }

    /**
     * 初始化全部任务目标方法
     */
    private void initTaskTarget() {
        String[] beans = applicationContext.getBeanNamesForAnnotation(Service.class);
        List<SysTaskTargetEntity> list = dao.findAll(SysTaskTargetEntity.class);
        Map<String, SysTaskTargetEntity> mapCache = new HashMap<>(list.size());
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
            for (Method method : methods) {
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
    public void addTask(SysTaskEntity sysTaskEntity) {
        dao.save(sysTaskEntity);
        //获取定时任务详情列表
        List<SysTaskTargetEntity> sysTaskTargetEntityList = dao.findAll(
                "select a.* from sys_task_target a left join sys_bt_task_target b on b.sys_task_target_id = a.sys_task_target_id where b.sys_task_id = ? order by b.order",
                SysTaskTargetEntity.class,
                sysTaskEntity.getSysTaskId());

        if (ObjectUtil.isEmpty(sysTaskTargetEntityList)) {
            return;
        }

        //新建定时任务触发器
        TaskTrigger trigger = new TaskTrigger(sysTaskEntity.getCron(), sysTaskEntity.getSync());

        //新建任务
        TaskJob job = new TaskJob(sysTaskEntity.getName(), trigger, sysTaskTargetEntityList);

        ScheduledFuture scheduledFuture = null;
        if (sysTaskEntity.getState()) {
            scheduledFuture = threadPoolTaskScheduler.schedule(job, trigger);
        }

        //定时任务装入缓冲区
        taskInfoMap.put(sysTaskEntity.getSysTaskId(), new TaskInfo(sysTaskEntity, trigger, job, scheduledFuture));
    }

    /**
     * 根据定时任务对象添加定时任务
     *
     * @return 是否添加成功
     */
    public RETURN addTask() {
        SysTaskEntity entity = getInParam(SysTaskEntity.class);
        if (!ObjectUtil.isValidity(entity)) {
            return RETURN.PARAMETER_ERROR;
        }
        addTask(entity);
        return RETURN.SUCCESS;
    }

    /**
     * 删除定时任务
     *
     * @return 是否成功
     */
    public RETURN removeTask() throws NotFoundTaskException {
        String id = this.getInParam("id", String.class);
        removeTask(id);
        return RETURN.SUCCESS;
    }

    public void removeTask(String id) throws NotFoundTaskException {
        if (taskInfoMap.containsKey(id)) {
            stopTask(id);
            taskInfoMap.remove(id);
        }
        this.dao.deleteById(SysTaskEntity.class, id);
    }

    /**
     * 停止定时任务
     *
     * @return 是否成功
     */
    public RETURN stopTask() throws NotFoundTaskException {
        String id = this.getInParam("id", String.class);
        stopTask(id);
        return RETURN.SUCCESS;
    }

    public void stopTask(String id) throws NotFoundTaskException {
        TaskInfo taskInfo = taskInfoMap.get(id);
        if (ObjectUtil.isEmpty(taskInfo)) {
            throw new NotFoundTaskException(String.format("未找到主键为%s的定时任务", id));
        }
        ScheduledFuture future = taskInfo.getScheduledFuture();
        if (ObjectUtil.isEmpty(future)) {
            return;
        }
        future.cancel(Boolean.TRUE);

        SysTaskEntity entity = dao.findOne(SysTaskEntity.class, id);
        entity.setState(false);
        dao.update(entity);
    }

    /**
     * 开启定时任务
     *
     * @return 是否成功
     */
    public RETURN startTask() throws NotFoundTaskException {
        String id = this.getInParam("id", String.class);
        startTask(id);
        return RETURN.SUCCESS;
    }

    public void startTask(String id) throws NotFoundTaskException {
        TaskInfo taskInfo = taskInfoMap.get(id);
        if (ObjectUtil.isEmpty(taskInfo)) {
            throw new NotFoundTaskException(String.format("未找到主键为%s的定时任务", id));
        }
        ScheduledFuture future = this.threadPoolTaskScheduler.schedule(taskInfo.getJob(), taskInfo.getTrigger());
        if (ObjectUtil.isEmpty(future)) {
            return;
        }
        taskInfo.setScheduledFuture(future);

        SysTaskEntity entity = dao.findOne(SysTaskEntity.class, id);
        entity.setState(true);
        dao.update(entity);
    }

    /**
     * 更新定时任务
     *
     * @return 是否成功
     */
    public RETURN updateTask() throws NotFoundTaskException {
        SysTaskEntity entity = ObjectUtil.getObjectFromMap(SysTaskEntity.class, this.getInParam());
        if (ObjectUtil.isEmpty(entity.getSysTaskId())) {
            return RETURN.PARAMETER_ERROR;
        }
        dao.update(entity);
        updateTask(entity);
        return RETURN.SUCCESS;
    }

    public void updateTask(SysTaskEntity sysTaskEntity) throws NotFoundTaskException {
        removeTask(sysTaskEntity.getSysTaskId());
        addTask(sysTaskEntity);
    }

}
