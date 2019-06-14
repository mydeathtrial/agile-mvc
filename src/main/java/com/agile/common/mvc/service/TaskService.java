package com.agile.common.mvc.service;

import com.agile.common.annotation.Init;
import com.agile.common.exception.NotFoundTaskException;
import com.agile.common.task.ApiBase;
import com.agile.common.task.Target;
import com.agile.common.task.Task;
import com.agile.common.task.TaskInfo;
import com.agile.common.task.TaskJob;
import com.agile.common.task.TaskManager;
import com.agile.common.task.TaskTrigger;
import com.agile.common.util.FactoryUtil;
import com.agile.common.util.ObjectUtil;
import com.agile.mvc.entity.SysTaskEntity;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.util.ProxyUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * @author 佟盟 on 2018/2/2
 * @author 佟盟
 */
public class TaskService {
    private static Map<Long, TaskInfo> taskInfoMap = new HashMap<>();
    private static Map<String, ApiBase> apiBaseMap = new HashMap<>();

    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;
    private final ApplicationContext applicationContext;
    private TaskManager taskManager;

    public TaskService(ThreadPoolTaskScheduler threadPoolTaskScheduler, ApplicationContext applicationContext, TaskManager taskManager) {
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
        this.applicationContext = applicationContext;
        this.taskManager = taskManager;
    }

    /**
     * spring容器初始化时初始化全部定时任务
     */
    @Init
    @Transactional(rollbackFor = Exception.class)
    public void init() {
        initTaskTarget();
        //获取持久层定时任务数据集
        List<Task> list = taskManager.getTask();
        for (Task task : list) {
            addTask(task);
        }
    }

    /**
     * 初始化全部任务目标方法
     */
    private void initTaskTarget() {
        String[] beans = applicationContext.getBeanDefinitionNames();
        List<Target> list = taskManager.getApis();
        Map<String, Target> mapCache = new HashMap<>(list.size());
        for (Target entity : list) {
            mapCache.put(entity.getCode(), entity);
        }

        for (String beanName : beans) {

            Object bean = applicationContext.getBean(beanName);
            if (bean == null) {
                continue;
            }
            Class<?> clazz = ProxyUtils.getUserClass(bean.getClass());
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getParameterCount() > 1 || method.getGenericReturnType() != void.class) {
                    continue;
                }

                if (!mapCache.containsKey(method.toGenericString())) {
                    taskManager.save(method);
                }
                apiBaseMap.put(method.toGenericString(), new ApiBase(bean, method, beanName));
            }
        }
    }

    /**
     * 获取方法缓存
     *
     * @param generic 根据方法的toGenericString检索
     * @return 方法信息
     */
    public static ApiBase getApi(String generic) {
        return apiBaseMap.get(generic);
    }

    /**
     * 判断是否是合法的定时任务方法
     *
     * @param method 方法对象
     * @return 是否
     */
    private static boolean isTaskMethod(Method method) {
        if (method == null) {
            return false;
        }
        if (method.getParameterCount() > 0 || method.getGenericReturnType() != void.class) {
            new IllegalArgumentException(String.format("[Method:%s][Reason:Must be an empty argument method with a return value of void]", method.toGenericString())).printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 判断是否是合法的定时任务方法
     *
     * @param target 定时任务目标，包含方法信息
     * @return 是否
     */
    private static boolean isTaskMethod(Target target) {
        ApiBase apiBase = getApi(target.getCode());
        if (apiBase == null) {
            return false;
        }
        return isTaskMethod(apiBase.getMethod());
    }

    /**
     * 添加定时任务
     *
     * @param task   任务信息
     * @param method 方法
     */
    public void addTask(Task task, Method method) {
        taskManager.save(task, method);
        addTask(task);
    }

    /**
     * 添加定时任务
     *
     * @param task   定时任务信息
     * @param target 目标方法信息
     */
    public void addTask(Task task, Target target) {
        ApiBase apiBase = getApi(target.getCode());
        if (apiBase == null) {
            return;
        }
        addTask(task, apiBase.getMethod());
    }

    /**
     * 添加定时任务
     *
     * @param task 定时任务信息
     */
    public void addTask(Task task) {
        //获取定时任务详情列表
        List<Target> targets = taskManager.getApisByTaskCode(task.getCode());

        if (targets.size() == 0) {
            return;
        }

        //新建定时任务触发器
        TaskTrigger trigger = new TaskTrigger(task.getCron(), task.getSync());

        //新建任务
        TaskJob job = new TaskJob(task, trigger, targets);

        ScheduledFuture scheduledFuture = null;
        if (task.enable()) {
            scheduledFuture = threadPoolTaskScheduler.schedule(job, trigger);
        }

        //定时任务装入缓冲区
        taskInfoMap.put(task.getCode(), new TaskInfo(task, trigger, job, scheduledFuture));
    }


    public void removeTask(long id) throws NotFoundTaskException {
        if (taskInfoMap.containsKey(id)) {
            stopTask(id);
            taskInfoMap.remove(id);
        }
    }


    public void stopTask(long id) throws NotFoundTaskException {
        TaskInfo taskInfo = taskInfoMap.get(id);
        if (ObjectUtil.isEmpty(taskInfo)) {
            throw new NotFoundTaskException(String.format("未找到主键为%s的定时任务", id));
        }
        ScheduledFuture future = taskInfo.getScheduledFuture();
        if (ObjectUtil.isEmpty(future)) {
            return;
        }
        future.cancel(Boolean.TRUE);

        //清锁
        RedisConnectionFactory redisConnectionFactory = FactoryUtil.getBean(RedisConnectionFactory.class);
        if (redisConnectionFactory != null) {
            RedisConnection connection = redisConnectionFactory.getConnection();
            if (connection != null) {
                connection.expire(Long.toString(id).getBytes(StandardCharsets.UTF_8), 0);
            }
        }

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
    }


    public void updateTask(SysTaskEntity sysTaskEntity) throws NotFoundTaskException {
        removeTask(sysTaskEntity.getSysTaskId());
        addTask(sysTaskEntity);
    }

    public void removeTask(Method method) throws NotFoundTaskException {
        List<Task> tasks = taskManager.getTasksByApiCode(method.toGenericString());
        if (tasks == null) {
            return;
        }
        for (Task task : tasks) {
            removeTask(task.getCode());
        }
    }
}
