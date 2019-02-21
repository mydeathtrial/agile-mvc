package com.agile.common.config;

import com.agile.common.factory.TaskFactory;
import com.agile.common.mvc.service.TaskService;
import com.agile.common.properties.TaskProperties;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.TaskManagementConfigUtils;

/**
 * 描述：
 * <p>创建时间：2018/11/29<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
@Configuration
@ConditionalOnBean(name = TaskManagementConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME)
@EnableConfigurationProperties(value = {TaskProperties.class})
@ConditionalOnProperty(name = "enable", prefix = "agile.task", havingValue = "true")
@AutoConfigureAfter(TaskSchedulingAutoConfiguration.class)
public class TaskAutoConfiguration {
    @Bean
    public TaskService customTaskServer(ThreadPoolTaskScheduler threadPoolTaskScheduler, ApplicationContext applicationContext) {
        return new TaskService(threadPoolTaskScheduler, applicationContext);
    }

    @Bean
    public TaskFactory taskFactory(ThreadPoolTaskScheduler threadPoolTaskScheduler) {
        return new TaskFactory(threadPoolTaskScheduler);
    }
}