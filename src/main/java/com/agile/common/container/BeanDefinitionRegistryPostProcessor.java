package com.agile.common.container;

import com.agile.common.annotation.AnnotationProcessor;
import com.agile.common.annotation.ParsingBeanBefore;
import com.agile.common.util.PropertiesUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author 佟盟 on 2018/1/19
 * bean定义过程
 */
@Component
public class BeanDefinitionRegistryPostProcessor implements org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor, ApplicationContextAware {
    private ApplicationContext applicationContext;
    private String[] securitys = new String[]{"securityConfig", "corsConfigurationSource", "corsConfiguration", "reloadableResourceBundleMessageSource", "jwtAuthenticationProvider",
            "loginFilter", "logoutHandler", "securityUserDetailsService", "tokenFilter", "tokenStrategy", "org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration",
            "org.springframework.security.config.annotation.web.configuration.WebMvcSecurityConfiguration", "org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration",
            "methodSecurityInterceptor", "methodSecurityMetadataSource", "autowiredWebSecurityConfigurersIgnoreParents", "springSecurityFilterChain", "webSecurityExpressionHandler",
            "org.springframework.security.config.annotation.configuration.ObjectPostProcessorConfiguration", "objectPostProcessor", "org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration",
            "authenticationManagerBuilder", "enableGlobalAuthenticationAutowiredConfigurer", "initializeUserDetailsBeanManagerConfigurer", "initializeAuthenticationProviderBeanManagerConfigurer",
            "delegatingApplicationListener", "privilegeEvaluator", "preInvocationAuthorizationAdvice", "metaDataSourceAdvisor", "requestDataValueProcessor", "securityFilterChainRegistration",
            "org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration", "spring.security-org.springframework.boot.autoconfigure.security.SecurityProperties",
            "org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration", "securityFilterChainRegistration"};
    private String[] activitis = new String[]{"activitiConfig", "springProcessEngineConfiguration", "processEngineFactoryBean", "repositoryService", "dynamicBpmnService", "historyService",
            "managementService", "runtimeService", "taskService"};
    private String[] rediss = new String[]{"redisConfig", "redisCacheManager", "redisTemplate", "jedisConnectionFactory", "redisPool", "redisRegionFactory",
            "org.springframework.boot.autoconfigure.data.redis.LettuceConnectionConfiguration", "org.springframework.boot.autoconfigure.data.redis.JedisConnectionConfiguration",
            "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration", "stringRedisTemplate", "spring.redis-org.springframework.boot.autoconfigure.data.redis.RedisProperties",
            "org.springframework.boot.actuate.autoconfigure.redis.RedisHealthIndicatorConfiguration", "redisHealthIndicator", "org.springframework.boot.actuate.autoconfigure.redis.RedisHealthIndicatorAutoConfiguration",
            "org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration", "org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration", "redisCustomConversions",
            "redisReferenceResolver", "redisConverter", "redisKeyValueAdapter", "redisKeyValueTemplate"};
    private String[] ehcaches = new String[]{"ehCacheConfig", "ehCacheCacheManager", "ehCacheManagerFactoryBean"};
    private String[] tasks = new String[]{"customTaskServer", "taskFactory"};
    private String[] es = new String[]{"esClient"};
    private String[] kafkaConsumer = new String[]{"consumerFactory", "kafkaListenerContainerFactory"};
    private String[] kafkaProducer = new String[]{"producerFactory", "kafkaTemplate"};
    private String[] kafka = new String[]{"kafkaBootstrapConfiguration", "org.springframework.kafka.config.internalKafkaListenerAnnotationProcessor", "org.springframework.kafka.config.internalKafkaListenerEndpointRegistry"};

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        isUse("agile.task.enable", tasks, beanDefinitionRegistry);
        isUse("agile.security.enable", securitys, beanDefinitionRegistry);
        isUse("agile.activiti.enable", activitis, beanDefinitionRegistry);
        isUse("agile.elasticsearch.enable", es, beanDefinitionRegistry);
        isUse("agile.kafka.consumer.enable", kafkaConsumer, beanDefinitionRegistry);
        isUse("agile.kafka.producer.enable", kafkaProducer, beanDefinitionRegistry);
        isUse(!PropertiesUtil.getProperty("agile.kafka.consumer.enable", boolean.class) && !PropertiesUtil.getProperty("agile.kafka.producer.enable", boolean.class), kafkaProducer, beanDefinitionRegistry);
        this.cacheManagerProcessor(beanDefinitionRegistry);
        this.beanAnnotationProcessor();
    }

    private void isUse(String handlerName, String[] beanNames, BeanDefinitionRegistry beanDefinitionRegistry) {
        Boolean enable = PropertiesUtil.getProperty(handlerName, boolean.class);
        isUse(enable, beanNames, beanDefinitionRegistry);
    }

    private void isUse(boolean is, String[] beanNames, BeanDefinitionRegistry beanDefinitionRegistry) {
        if (!is) {
            for (String beanName : beanNames) {
                try {
                    beanDefinitionRegistry.removeBeanDefinition(beanName);
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * 解决多个cacheManager导致spring无法成功获取缓存控制器问题
     *
     * @param beanDefinitionRegistry bean注册器
     */
    private void cacheManagerProcessor(BeanDefinitionRegistry beanDefinitionRegistry) {
        String cacheProxy = PropertiesUtil.getProperty("agile.cache.proxy").toLowerCase();

        switch (cacheProxy) {
            case "redis":
                for (int i = 0; i < ehcaches.length; i++) {
                    try {
                        beanDefinitionRegistry.removeBeanDefinition(ehcaches[i]);
                    } catch (Exception ignored) {
                    }
                }
                break;
            case "ehcache":
                for (int i = 0; i < rediss.length; i++) {
                    try {
                        beanDefinitionRegistry.removeBeanDefinition(rediss[i]);
                    } catch (Exception ignored) {
                    }
                }
                break;
            default:
        }
    }

    /**
     * 1：处理自定义注解
     */
    private void beanAnnotationProcessor() {
        AnnotationProcessor.beanAnnotationProcessor(applicationContext, ParsingBeanBefore.class);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
