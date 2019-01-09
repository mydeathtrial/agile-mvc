package com.agile.common.config;

import com.agile.common.factory.TaskFactory;
import com.agile.common.mvc.service.TaskService;
import com.agile.common.util.PropertiesUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;
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
@Conditional(TaskConfig.class)
public class TaskConfig implements Condition {
    @Bean
    public TaskService customTaskServer(ThreadPoolTaskScheduler threadPoolTaskScheduler, ApplicationContext applicationContext) {
        return new TaskService(threadPoolTaskScheduler, applicationContext);
    }

    @Bean
    public TaskFactory taskFactory(ThreadPoolTaskScheduler threadPoolTaskScheduler) {
        return new TaskFactory(threadPoolTaskScheduler);
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return PropertiesUtil.getProperty("agile.task.enable", boolean.class);
    }
}
