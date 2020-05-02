package com.agile.common.mvc.service;

import com.agile.common.annotation.Init;
import com.agile.common.base.Constant;
import com.agile.common.exception.NotFoundTaskException;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.task.InstantActuator;
import com.agile.common.task.Target;
import com.agile.common.task.Task;
import com.agile.common.task.TaskActuatorInterface;
import com.agile.common.task.TaskInfo;
import com.agile.common.task.TaskJob;
import com.agile.common.task.TaskManager;
import com.agile.common.task.TaskProxy;
import com.agile.common.task.TriggerActuator;
import com.agile.common.util.DateUtil;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.springframework.context.ApplicationContext;
import org.springframework.data.util.ProxyUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

/**
 * @author 佟盟 on 2018/2/2
 * @author 佟盟
 */
public class TaskService {
    private final Log logger = LoggerFactory.createLogger("task", TaskService.class);

    private static final String NO_SUCH_TARGETS_ERROR = "任务:[%s]任务未绑定任何执行方法数据，无法加载该任务";
    private static final String NO_SUCH_CRON_ERROR = "任务:[%s]定时任务未配置时间表达式，无法加载该任务";
    private static final String INIT_TASK = "任务:[%s][完成初始化][下次执行时间%s]";
    private static final String INIT_TASKS = "检测出定时任务%s条";

    private static final Map<Long, TaskInfo> TASK_INFO_MAP = new HashMap<>();
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
    public void init() throws NotFoundTaskException, CloneNotSupportedException {
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
                logger.error(String.format(NO_SUCH_TARGETS_ERROR, task.getCode()));
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
            logger.error(String.format(NO_SUCH_TARGETS_ERROR, task.getCode()));
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

        //取定时任务表达式
        String cronString = task.getCron();
        if (StringUtils.isEmpty(cronString)) {
            logger.error(String.format(NO_SUCH_CRON_ERROR, task.getCode()));
            return;
        }
        String[] crones = cronString.split(Constant.RegularAbout.SEMICOLON);

        // 任务信息集合
        List<TaskActuatorInterface> actuators = Lists.newArrayList();

        //新建任务
        TaskJob job = new TaskJob(taskManager, taskProxy, task, methods);
        if (task.getEnable() != null && task.getEnable()) {
            for (String cron : crones) {
                cron = cron.trim();
                ScheduledFuture<?> scheduledFuture;
                // 如果表达式位时间戳，则执行固定时间执行任务,否则执行周期任务
                if (NumberUtils.isCreatable(cron)) {
                    long executeTime = NumberUtils.toLong(cron);
                    Instant instant;

                    if (executeTime <= System.currentTimeMillis()) {
                        continue;
                    }
                    instant = Instant.ofEpochMilli(executeTime);

                    scheduledFuture = threadPoolTaskScheduler.schedule(job, instant);

                    actuators.add(new InstantActuator(scheduledFuture, instant, threadPoolTaskScheduler));
                } else {
                    //新建定时任务触发器
                    CronTrigger trigger = new CronTrigger(cron);

                    scheduledFuture = threadPoolTaskScheduler.schedule(job, trigger);

                    actuators.add(new TriggerActuator(scheduledFuture, trigger, threadPoolTaskScheduler));
                }
            }
        }

        TaskInfo taskInfo = new TaskInfo(actuators, job);
        //定时任务装入缓冲区
        TASK_INFO_MAP.put(task.getCode(), taskInfo);

        // 同步更新持久层数据
        taskManager.save(task, methods);

        if (taskInfo.nextExecutionTime() != null && logger.isDebugEnabled()) {
            logger.debug(String.format(INIT_TASK, task.getCode(),
                    DateUtil.convertToString(taskInfo.nextExecutionTime(),
                            "yyyy-MM-dd HH:mm:ss")
            ));
        }
    }

    /**
     * 获取任务下一次执行时间
     *
     * @param taskCode 定时任务标识
     * @return 标识
     */
    public static Date nextExecutionTimeByTaskCode(Long taskCode) {
        TaskInfo taskInfo = TASK_INFO_MAP.get(taskCode);
        if (taskInfo != null) {
            return taskInfo.nextExecutionTime();
        }
        return null;
    }

    /**
     * 启动定时任务
     *
     * @param taskCode 定时任务标识
     */
    public static void reStart(Long taskCode) {
        TaskInfo taskInfo = TASK_INFO_MAP.get(taskCode);
        if (taskInfo != null) {
            taskInfo.start();
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
        TaskInfo taskInfo = TASK_INFO_MAP.get(id);
        if (ObjectUtils.isEmpty(taskInfo)) {
            throw new NotFoundTaskException(String.format("未找到主键为%s的定时任务", id));
        }
        //任务取消
        taskInfo.stop();
    }

    public void startTask(long id) throws NotFoundTaskException {
        TaskInfo taskInfo = TASK_INFO_MAP.get(id);
        if (ObjectUtils.isEmpty(taskInfo)) {
            throw new NotFoundTaskException(String.format("未找到主键为%s的定时任务", id));
        }
        taskInfo.start();
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
