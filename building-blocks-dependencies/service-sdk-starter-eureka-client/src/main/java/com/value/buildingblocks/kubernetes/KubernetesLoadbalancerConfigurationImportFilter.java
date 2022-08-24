package com.value.buildingblocks.kubernetes;

import com.value.buildingblocks.configuration.filter.AbstractAutoConfigurationImportFilter;

public class KubernetesLoadbalancerConfigurationImportFilter extends AbstractAutoConfigurationImportFilter {
  private static final String EXCLUDED_CONFIG = "org.springframework.cloud.kubernetes.loadbalancer.KubernetesLoadBalancerAutoConfiguration";
  
  public KubernetesLoadbalancerConfigurationImportFilter() {
    super("org.springframework.cloud.kubernetes.loadbalancer.KubernetesLoadBalancerAutoConfiguration");
  }
  
  protected boolean filterCondition() {
    return !"SERVICE".equalsIgnoreCase(this.environment.getProperty("spring.cloud.kubernetes.loadbalancer.mode"));
  }
}
