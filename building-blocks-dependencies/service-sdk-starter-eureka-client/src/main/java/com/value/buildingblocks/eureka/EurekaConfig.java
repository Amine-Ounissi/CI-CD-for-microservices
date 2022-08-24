package com.value.buildingblocks.eureka;

import com.value.buildingblocks.eureka.loadbalancer.ContextPathLoadBalancerRequestTransformer;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequestTransformer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnClass({RestTemplate.class})
@AutoConfigureBefore({LoadBalancerAutoConfiguration.class})
@PropertySource({"classpath:service-sdk-starter-eureka-client.properties"})
public class EurekaConfig {
  @ConditionalOnProperty(value = {"value.eureka.context-path-load-balancer.enabled"}, havingValue = "true", matchIfMissing = true)
  @Bean
  public LoadBalancerRequestTransformer loadBalancerRequestTransformer() {
    return new ContextPathLoadBalancerRequestTransformer();
  }
}
