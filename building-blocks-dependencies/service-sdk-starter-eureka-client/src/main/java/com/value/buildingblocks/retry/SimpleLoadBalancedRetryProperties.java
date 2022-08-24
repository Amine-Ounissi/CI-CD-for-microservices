package com.value.buildingblocks.retry;

import java.util.Collections;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("value.loadbalancer")
public class SimpleLoadBalancedRetryProperties {
  private int maxRetries = 0;
  
  private int maxServers = 1;
  
  private boolean retryAllOperations = false;
  
  private List<Integer> retryStatusCodes = Collections.emptyList();
  
  public int getMaxRetries() {
    return this.maxRetries;
  }
  
  public void setMaxRetries(int maxRetries) {
    this.maxRetries = maxRetries;
  }
  
  public int getMaxServers() {
    return this.maxServers;
  }
  
  public void setMaxServers(int maxServers) {
    this.maxServers = maxServers;
  }
  
  public boolean isRetryAllOperations() {
    return this.retryAllOperations;
  }
  
  public void setRetryAllOperations(boolean retryAllOperations) {
    this.retryAllOperations = retryAllOperations;
  }
  
  public List<Integer> getRetryStatusCodes() {
    return this.retryStatusCodes;
  }
  
  public void setRetryStatusCodes(List<Integer> retryStatusCodes) {
    this.retryStatusCodes = retryStatusCodes;
  }
}
