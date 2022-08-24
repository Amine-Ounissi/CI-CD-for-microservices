package com.value.buildingblocks.eureka;

import com.value.buildingblocks.configuration.filter.AbstractAutoConfigurationImportFilter;

public class EurekaKubernetesConfigurationImportFilter extends AbstractAutoConfigurationImportFilter {
  private static final String EXCLUDED_CONFIG = "org.springframework.cloud.netflix.eureka.loadbalancer.LoadBalancerEurekaAutoConfiguration";
  
  public EurekaKubernetesConfigurationImportFilter() {
    super("org.springframework.cloud.netflix.eureka.loadbalancer.LoadBalancerEurekaAutoConfiguration");
  }
  
  protected boolean filterCondition() {
    return ("true".equalsIgnoreCase(this.environment.getProperty("spring.cloud.kubernetes.enabled")) && "false".equalsIgnoreCase(this.environment.getProperty("eureka.client.enabled")));
  }
}
