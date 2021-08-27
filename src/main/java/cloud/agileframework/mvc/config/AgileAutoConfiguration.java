package cloud.agileframework.mvc.config;

import cloud.agileframework.mvc.container.CustomBeanDefinitionRegistryPostProcessor;
import cloud.agileframework.mvc.container.CustomBeanPostProcessor;
import cloud.agileframework.mvc.container.ResetService;
import cloud.agileframework.mvc.exception.SpringExceptionHandler;
import cloud.agileframework.mvc.listener.ListenerContainerInit;
import cloud.agileframework.mvc.listener.ListenerContainerRefreshed;
import cloud.agileframework.mvc.listener.ListenerSpringApplicationFailed;
import cloud.agileframework.mvc.listener.ListenerSpringApplicationStarted;
import cloud.agileframework.mvc.mvc.controller.MainController;
import cloud.agileframework.mvc.properties.CorsFilterProperties;
import cloud.agileframework.spring.properties.ApplicationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 佟盟 on 2017/9/26
 */
@Configuration
@EnableConfigurationProperties({ApplicationProperties.class, CorsFilterProperties.class})
public class AgileAutoConfiguration {

    @Bean
    static CustomBeanDefinitionRegistryPostProcessor customBeanDefinitionRegistryPostProcessor() {
        return new CustomBeanDefinitionRegistryPostProcessor();
    }

    @Bean
    static CustomBeanPostProcessor customBeanPostProcessor() {
        return new CustomBeanPostProcessor();
    }

    @Bean
    ResetService resetService() {
        return new ResetService();
    }

    @Bean
    ListenerContainerInit listenerContainerInit() {
        return new ListenerContainerInit();
    }

    @Bean
    ListenerContainerRefreshed listenerContainerRefreshed() {
        return new ListenerContainerRefreshed();
    }

    @Bean
    ListenerSpringApplicationFailed listenerSpringApplicationFailed() {
        return new ListenerSpringApplicationFailed();
    }

    @Bean
    ListenerSpringApplicationStarted listenerSpringApplicationStarted() {
        return new ListenerSpringApplicationStarted();
    }

    @Bean
    @ConditionalOnMissingBean(MainController.class)
    MainController mainController() {
        return new MainController();
    }

    @Bean
    SpringExceptionHandler springExceptionHandler() {
        return new SpringExceptionHandler();
    }
}
