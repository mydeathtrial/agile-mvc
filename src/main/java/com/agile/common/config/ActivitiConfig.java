package com.agile.common.config;

import com.agile.common.properties.ActivitiConfigProperties;
import com.agile.common.properties.MailConfigProperty;
import org.activiti.engine.DynamicBpmnService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * Created by 佟盟 on 2018/4/3
 */
@Configuration
public class ActivitiConfig {

    @Bean
    SpringProcessEngineConfiguration springProcessEngineConfiguration(PlatformTransactionManager transactionManager, DataSource dataSource) {
        SpringProcessEngineConfiguration springProcessEngineConfiguration = new SpringProcessEngineConfiguration();
        springProcessEngineConfiguration.setDataSource(dataSource);
        springProcessEngineConfiguration.setTransactionManager(transactionManager);
        springProcessEngineConfiguration.setDatabaseSchemaUpdate(ActivitiConfigProperties.getDatabaseSchemaUpdate());
        springProcessEngineConfiguration.setHistory(ActivitiConfigProperties.getHistory());
        springProcessEngineConfiguration.setMailServerHost(MailConfigProperty.getServerHost());
        springProcessEngineConfiguration.setMailServerPort(MailConfigProperty.getServerPort());
        springProcessEngineConfiguration.setMailServerDefaultFrom(MailConfigProperty.getServerDefaultFrom());
        springProcessEngineConfiguration.setMailServerUsername(MailConfigProperty.getServerUsername());
        springProcessEngineConfiguration.setMailServerPassword(MailConfigProperty.getServerPassword());
        springProcessEngineConfiguration.setAsyncExecutorActivate(true);
        return springProcessEngineConfiguration;
    }

    @Bean
    ProcessEngineFactoryBean processEngineFactoryBean(SpringProcessEngineConfiguration springProcessEngineConfiguration) {
        ProcessEngineFactoryBean processEngineFactoryBean = new ProcessEngineFactoryBean();
        processEngineFactoryBean.setProcessEngineConfiguration(springProcessEngineConfiguration);
        return processEngineFactoryBean;
    }

    @Bean
    RepositoryService repositoryService(ProcessEngineFactoryBean processEngineFactoryBean) throws Exception {
        return Objects.requireNonNull(processEngineFactoryBean.getObject()).getRepositoryService();
    }

    @Bean
    DynamicBpmnService dynamicBpmnService(ProcessEngineFactoryBean processEngineFactoryBean) throws Exception {
        return Objects.requireNonNull(processEngineFactoryBean.getObject()).getDynamicBpmnService();
    }

    @Bean
    HistoryService historyService(ProcessEngineFactoryBean processEngineFactoryBean) throws Exception {
        return Objects.requireNonNull(processEngineFactoryBean.getObject()).getHistoryService();
    }

    @Bean
    ManagementService managementService(ProcessEngineFactoryBean processEngineFactoryBean) throws Exception {
        return Objects.requireNonNull(processEngineFactoryBean.getObject()).getManagementService();
    }

    @Bean
    RuntimeService runtimeService(ProcessEngineFactoryBean processEngineFactoryBean) throws Exception {
        return Objects.requireNonNull(processEngineFactoryBean.getObject()).getRuntimeService();
    }

    @Bean
    TaskService taskService(ProcessEngineFactoryBean processEngineFactoryBean) throws Exception {
        return Objects.requireNonNull(processEngineFactoryBean.getObject()).getTaskService();
    }
}
