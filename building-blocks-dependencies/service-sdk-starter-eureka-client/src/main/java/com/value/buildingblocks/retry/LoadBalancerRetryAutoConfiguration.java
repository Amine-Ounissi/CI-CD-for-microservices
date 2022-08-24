package com.value.buildingblocks.retry;

import java.util.Optional;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration;
import org.springframework.cloud.client.loadbalancer.reactive.OnNoRibbonDefaultCondition;
import org.springframework.cloud.loadbalancer.config.BlockingLoadBalancerClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@AutoConfigureAfter({BlockingLoadBalancerClientAutoConfiguration.class})
@AutoConfigureBefore({LoadBalancerAutoConfiguration.class})
@ConditionalOnClass({RetryTemplate.class})
@EnableConfigurationProperties({SimpleLoadBalancedRetryProperties.class})
public class LoadBalancerRetryAutoConfiguration {
  @Bean
  @ConditionalOnMissingBean({LoadBalancedRetryFactory.class})
  @Conditional({OnNoRibbonDefaultCondition.class})
  @ConditionalOnProperty(name = {"spring.cloud.loadbalancer.retry.enabled"}, havingValue = "true", matchIfMissing = true)
  public SimpleLoadBalancedRetryFactory loadBalancedRetryFactory(Environment environment, Optional<RetryBackOffPolicyFactory> retryBackOffPolicyFactory) {
    return new SimpleLoadBalancedRetryFactory(environment, retryBackOffPolicyFactory.orElse(null));
  }
}
