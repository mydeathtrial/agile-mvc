package com.agile.common.mvc.service;

import com.agile.common.annotation.Init;
import com.agile.common.exception.NotFoundTaskException;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.task.ApiBase;
import com.agile.common.task.Target;
import com.agile.common.task.Task;
import com.agile.common.task.TaskInfo;
import com.agile.common.task.TaskJob;
import com.agile.common.task.TaskManager;
import com.agile.common.task.TaskProxy;
import com.agile.common.task.TaskTrigger;
import com.agile.common.util.DateUtil;
import com.agile.common.util.FactoryUtil;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.util.ProxyUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * @author 佟盟 on 2018/2/2
 * @author 佟盟
 */
public class TaskService {
    private final Log logger = LoggerFactory.createLogger("task", TaskService.class);
    private static final String INIT_TASK = "任务:[%s][完成初始化][下次执行时间%s]";
    private static final String INIT_TASKS = "检测出定时任务%s条";

    private static Map<Long, TaskInfo> taskInfoMap = new HashMap<>();
    private static Map<String, ApiBase> apiBaseMap = new HashMap<>();

    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;
    private final ApplicationContext applicationContext;
    private TaskManager taskManager;
    private TaskProxy taskProxy;

    public TaskService(ThreadPoolTaskScheduler threadPoolTaskScheduler, ApplicationContext applicationContext, TaskManager taskManager, TaskProxy taskProxy) {
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
        this.applicationContext = applicationContext;
        this.taskManager = taskManager;
        this.taskProxy = taskProxy;
    }

    /**
     * spring容器初始化时初始化全部定时任务
     */
    @Init
    @Async(value = "applicationTaskExecutor")
    @Transactional(rollbackFor = Exception.class)
    public void init() {
        initTaskTarget();
        //获取持久层定时任务数据集
        List<Task> list = taskManager.getTask();
        if (logger.isDebugEnabled()) {
            logger.debug(String.format(INIT_TASKS, list.size()));
        }
        for (Task task : list) {
            addTask(task);
        }
    }

    /**
     * 初始化全部任务目标方法
     */
    private void initTaskTarget() {
        String[] beans = applicationContext.getBeanDefinitionNames();

        for (String beanName : beans) {

            Object bean = applicationContext.getBean(beanName);
            Class<?> clazz = ProxyUtils.getUserClass(bean.getClass());
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getParameterCount() > 1 || method.getGenericReturnType() != void.class) {
                    continue;
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
        if (method.getParameterCount() > 1 || method.getGenericReturnType() != void.class) {
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
        Class<?> beanClass = method.getDeclaringClass();
        Object bean = FactoryUtil.getBean(beanClass);
        if (bean == null) {
            throw new NoSuchBeanDefinitionException(beanClass);
        }

        taskManager.save(task, method);

        // 目标方法信息放到内存中，任务执行时使用
        apiBaseMap.put(method.toGenericString(),
                new ApiBase(bean,
                        method,
                        FactoryUtil.getApplicationContext().getBeanNamesForType(beanClass)[0])
        );
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
        TaskJob job = new TaskJob(taskManager, taskProxy, task, trigger, targets);

        ScheduledFuture<?> scheduledFuture = null;
        if (task.getEnable()) {
            scheduledFuture = threadPoolTaskScheduler.schedule(job, trigger);
        }

        //定时任务装入缓冲区
        taskInfoMap.put(task.getCode(), new TaskInfo(task, trigger, job, scheduledFuture));

        if (logger.isDebugEnabled()) {
            Date date = trigger.nextExecutionTime(new SimpleTriggerContext());
            logger.debug(String.format(INIT_TASK, task.getCode(), DateUtil.convertToString(date, "yyyy-MM-dd HH:mm:ss")));
        }
    }


    public void removeTask(long id) throws NotFoundTaskException {
        if (taskInfoMap.containsKey(id)) {
            stopTask(id);
            taskInfoMap.remove(id);
        }
        taskManager.remove(id);
    }


    public void stopTask(long id) throws NotFoundTaskException {
        TaskInfo taskInfo = taskInfoMap.get(id);
        if (ObjectUtils.isEmpty(taskInfo)) {
            throw new NotFoundTaskException(String.format("未找到主键为%s的定时任务", id));
        }
        ScheduledFuture<?> future = taskInfo.getScheduledFuture();
        if (ObjectUtils.isEmpty(future)) {
            return;
        }
        future.cancel(Boolean.TRUE);

        //清锁
        RedisConnectionFactory redisConnectionFactory = FactoryUtil.getBean(RedisConnectionFactory.class);
        if (redisConnectionFactory != null) {
            RedisConnection connection = redisConnectionFactory.getConnection();
            connection.expire(Long.toString(id).getBytes(StandardCharsets.UTF_8), 0);
        }

    }

    public void startTask(long id) throws NotFoundTaskException {
        TaskInfo taskInfo = taskInfoMap.get(id);
        if (ObjectUtils.isEmpty(taskInfo)) {
            throw new NotFoundTaskException(String.format("未找到主键为%s的定时任务", id));
        }
        ScheduledFuture<?> future = this.threadPoolTaskScheduler.schedule(taskInfo.getJob(), taskInfo.getTrigger());
        if (ObjectUtils.isEmpty(future)) {
            return;
        }
        taskInfo.setScheduledFuture(future);
    }


    public void updateTask(Task task) throws NotFoundTaskException {
        removeTask(task.getCode());
        addTask(task);
        taskManager.save(task);
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
