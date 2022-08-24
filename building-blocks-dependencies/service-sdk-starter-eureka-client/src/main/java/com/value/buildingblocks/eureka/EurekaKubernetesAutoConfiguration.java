package com.value.buildingblocks.eureka;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;

@Configuration
public class EurekaKubernetesAutoConfiguration {
  private static final Logger log = LoggerFactory.getLogger(EurekaKubernetesAutoConfiguration.class);
  
  @Configuration
  @Conditional({OnRibbonAndKubernetesEnabledCondition.class})
  static class KubernetesDiscoveryRibbonWarningRibbonConfig {
    @PostConstruct
    public void info() {
      EurekaKubernetesAutoConfiguration.log.warn("Ribbon Load Balancer found on classpath, however it's not supported when using Kubernetes Discovery. To switch to Spring Cloud Load Balancer use \"spring.cloud.loadbalancer.ribbon.enabled=false\"");
    }
  }
  
  static class OnRibbonAndKubernetesEnabledCondition extends AllNestedConditions {
    OnRibbonAndKubernetesEnabledCondition() {
      super(ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
    }
    
    @ConditionalOnProperty(value = {"spring.cloud.loadbalancer.ribbon.enabled"}, havingValue = "true", matchIfMissing = true)
    @ConditionalOnClass(name = {"org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerClient"})
    static class OnRibbonEnabled {}
    
    @ConditionalOnProperty(value = {"spring.cloud.kubernetes.enabled"}, havingValue = "true")
    static class KubernetesDiscovery {}
  }
}
