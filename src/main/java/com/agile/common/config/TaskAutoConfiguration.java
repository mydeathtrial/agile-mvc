package com.agile.common.config;

import com.agile.common.factory.TaskFactory;
import com.agile.common.mvc.service.TaskService;
import com.agile.common.properties.TaskProperties;
import com.agile.common.task.TaskManager;
import com.agile.common.task.TaskProxy;
import com.agile.mvc.service.TaskManagerImpl;
import com.agile.mvc.service.TaskRevealService;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 描述：
 * <p>创建时间：2018/11/29<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties(value = {TaskProperties.class})
@ConditionalOnProperty(name = "enable", prefix = "agile.task", havingValue = "true")
@AutoConfigureAfter(TaskSchedulingAutoConfiguration.class)
public class TaskAutoConfiguration {
    @Bean
    public TaskService customTaskServer(ThreadPoolTaskScheduler threadPoolTaskScheduler, ApplicationContext applicationContext, TaskManager taskTargetService, TaskProxy taskProxy) {
        return new TaskService(threadPoolTaskScheduler, applicationContext, taskTargetService, taskProxy);
    }

    @Bean
    public TaskFactory taskFactory(ThreadPoolTaskScheduler threadPoolTaskScheduler) {
        return TaskFactory.after(threadPoolTaskScheduler);
    }

    @Bean
    public TaskProxy taskProxy() {
        return new TaskProxy();
    }

    @Bean
    public TaskRevealService taskRevealService(TaskService taskService) {
        return new TaskRevealService(taskService);
    }

    @Bean
    public TaskManagerImpl taskManager() {
        return new TaskManagerImpl();
    }
}
