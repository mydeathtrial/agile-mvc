package com.agile.common.mvc.service;

import com.agile.common.annotation.Init;
import com.agile.common.exception.NotFoundTaskException;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.task.CycleTaskInfo;
import com.agile.common.task.FixedTaskInfo;
import com.agile.common.task.Target;
import com.agile.common.task.Task;
import com.agile.common.task.TaskInfoInterface;
import com.agile.common.task.TaskJob;
import com.agile.common.task.TaskManager;
import com.agile.common.task.TaskProxy;
import com.agile.common.task.TaskTrigger;
import com.agile.common.task.TimerTaskJob;
import com.agile.common.util.DateUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.springframework.context.ApplicationContext;
import org.springframework.data.util.ProxyUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

/**
 * @author 佟盟 on 2018/2/2
 * @author 佟盟
 */
public class TaskService {
    private final Log logger = LoggerFactory.createLogger("task", TaskService.class);
    private static final String INIT_TASK = "任务:[%s][完成初始化][下次执行时间%s]";
    private static final String INIT_TASKS = "检测出定时任务%s条";

    private static final Map<Long, TaskInfoInterface> TASK_INFO_MAP = new HashMap<>();
    private static final Map<String, Method> API_BASE_MAP = new HashMap<>();

    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;
    private final ApplicationContext applicationContext;
    private final TaskManager taskManager;
    private final TaskProxy taskProxy;

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
    public void init() throws NotFoundTaskException {
        initTaskTarget();
        //获取持久层定时任务数据集
        List<Task> list = taskManager.getTask();
        if (logger.isDebugEnabled()) {
            logger.debug(String.format(INIT_TASKS, list.size()));
        }
        for (Task task : list) {
            //获取定时任务详情列表
            List<Target> targets = taskManager.getApisByTaskCode(task.getCode());
            if (ObjectUtils.isEmpty(targets)) {
                continue;
            }
            List<Method> methods = targets.stream()
                    .map(n -> API_BASE_MAP.get(n.getCode()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            updateTask(task, methods);
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

                API_BASE_MAP.put(method.toGenericString(), method);
            }
        }
    }

    /**
     * 获取方法缓存
     *
     * @param generic 根据方法的toGenericString检索
     * @return 方法信息
     */
    public static Method getApi(String generic) {
        return API_BASE_MAP.get(generic);
    }

    /**
     * 添加/更新定时任务
     *
     * @param task 定时任务信息
     */
    public void updateTask(Task task, List<Method> methods) throws NotFoundTaskException {
        if (ObjectUtils.isEmpty(methods)) {
            return;
        }

        for (Method method : methods) {
            // 目标方法信息放到内存中，任务执行时使用
            API_BASE_MAP.put(method.toGenericString(), method);
        }

        // 当任务已经存在时，删掉旧的过时任务，再重新定义任务
        if (TASK_INFO_MAP.get(task.getCode()) != null) {
            removeTask(task.getCode());
        }

        //下一次执行时间
        Date nextRunTime;

        // 如果表达式位时间戳，则执行固定时间执行任务,否则执行周期任务
        if (NumberUtils.isCreatable(task.getCron())) {
            long executeTime = NumberUtils.toLong(task.getCron());
            Timer timer = null;
            //新建任务
            TimerTaskJob job = new TimerTaskJob(taskManager, taskProxy, task, methods);

            if (task.getEnable() != null && task.getEnable()) {
                timer = new Timer();
                timer.schedule(job, new Date(executeTime));
            }

            nextRunTime = new Date(executeTime);

            //定时任务装入缓冲区
            TASK_INFO_MAP.put(task.getCode(), new FixedTaskInfo(timer, job, nextRunTime));
        } else {
            //新建定时任务触发器
            TaskTrigger trigger = new TaskTrigger(task.getCron(), task.getSync());

            //新建任务
            TaskJob job = new TaskJob(taskManager, taskProxy, task, trigger, methods);

            ScheduledFuture<?> scheduledFuture = null;
            if (task.getEnable() != null && task.getEnable()) {
                scheduledFuture = threadPoolTaskScheduler.schedule(job, trigger);
            }

            nextRunTime = trigger.nextExecutionTime(new SimpleTriggerContext());
            //定时任务装入缓冲区
            TASK_INFO_MAP.put(task.getCode(), new CycleTaskInfo(scheduledFuture, job, threadPoolTaskScheduler, trigger));
        }

        // 同步更新持久层数据
        taskManager.save(task, methods);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format(INIT_TASK, task.getCode(), DateUtil.convertToString(nextRunTime, "yyyy-MM-dd HH:mm:ss")));
        }
    }

    /**
     * 删除定时任务
     *
     * @param id 任务标识
     * @throws NotFoundTaskException 没找到
     */
    public void removeTask(long id) throws NotFoundTaskException {
        if (TASK_INFO_MAP.containsKey(id)) {
            stopTask(id);
            TASK_INFO_MAP.remove(id);
        }
        taskManager.remove(id);
    }

    /**
     * 停止定时任务
     *
     * @param id 任务标识
     * @throws NotFoundTaskException 没找到
     */
    public void stopTask(long id) throws NotFoundTaskException {
        TaskInfoInterface taskInfo = TASK_INFO_MAP.get(id);
        if (ObjectUtils.isEmpty(taskInfo)) {
            throw new NotFoundTaskException(String.format("未找到主键为%s的定时任务", id));
        }
        //任务取消
        taskInfo.cancel();
    }

    public void startTask(long id) throws NotFoundTaskException {
        TaskInfoInterface taskInfo = TASK_INFO_MAP.get(id);
        if (ObjectUtils.isEmpty(taskInfo)) {
            throw new NotFoundTaskException(String.format("未找到主键为%s的定时任务", id));
        }
        taskInfo.reStart();
    }

    public void removeTaskByMethod(Method method) throws NotFoundTaskException {
        List<Task> tasks = taskManager.getTasksByApiCode(method.toGenericString());
        if (tasks == null) {
            return;
        }
        for (Task task : tasks) {
            removeTask(task.getCode());
        }
    }
}
