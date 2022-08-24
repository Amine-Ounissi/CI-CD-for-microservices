package com.value.buildingblocks.retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryContext;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryPolicy;
import org.springframework.cloud.client.loadbalancer.ServiceInstanceChooser;
import org.springframework.http.HttpMethod;

public class SimpleLoadBalancedRetryPolicy implements LoadBalancedRetryPolicy {
  private static final Logger log = LoggerFactory.getLogger(SimpleLoadBalancedRetryPolicy.class);
  
  private int serverRetries = 0;
  
  private int serversCount = 0;
  
  private String serviceId;
  
  private SimpleLoadBalancedRetryProperties lbConfig;
  
  private ServiceInstanceChooser loadBalancer;
  
  public SimpleLoadBalancedRetryPolicy(String serviceId, SimpleLoadBalancedRetryProperties lbConfig, ServiceInstanceChooser loadBalancer) {
    this.serviceId = serviceId;
    this.lbConfig = lbConfig;
    this.loadBalancer = loadBalancer;
  }
  
  public boolean canRetrySameServer(LoadBalancedRetryContext context) {
    return (canRetryMethod(context) && this.serverRetries < this.lbConfig.getMaxRetries());
  }
  
  public boolean canRetryNextServer(LoadBalancedRetryContext context) {
    return (canRetryMethod(context) && this.serversCount <= this.lbConfig.getMaxServers());
  }
  
  public void close(LoadBalancedRetryContext context) {}
  
  public void registerThrowable(LoadBalancedRetryContext context, Throwable throwable) {
    if (!canRetrySameServer(context) && canRetryNextServer(context)) {
      ServiceInstance newInstance = this.loadBalancer.choose(this.serviceId);
      log.debug("{}: Reached limit of retries on {}, switching to {}", new Object[] { this.serviceId, instanceDetails(context.getServiceInstance()), instanceDetails(newInstance) });
      context.setServiceInstance(newInstance);
    } 
    if (this.serverRetries >= this.lbConfig.getMaxRetries() && canRetryMethod(context)) {
      this.serverRetries = 0;
      this.serversCount++;
      if (!canRetryNextServer(context))
        context.setExhaustedOnly(); 
    } else {
      this.serverRetries++;
    } 
    log.trace("{}: current retry {} out of {}, on server {} out of {}", new Object[] { this.serviceId, Integer.valueOf(this.serverRetries), Integer.valueOf(this.lbConfig.getMaxRetries()), Integer.valueOf(this.serversCount), Integer.valueOf(this.lbConfig.getMaxServers()) });
  }
  
  private String instanceDetails(ServiceInstance instance) {
    StringBuilder sb = new StringBuilder();
    if (instance == null) {
      sb.append("<unavailable>");
    } else {
      sb.append(instance.getHost()).append(":").append(instance.getPort());
    } 
    return sb.toString();
  }
  
  public boolean retryableStatusCode(int statusCode) {
    return this.lbConfig.getRetryStatusCodes().contains(Integer.valueOf(statusCode));
  }
  
  private boolean canRetryMethod(LoadBalancedRetryContext context) {
    HttpMethod method = context.getRequest().getMethod();
    return (HttpMethod.GET == method || this.lbConfig.isRetryAllOperations());
  }
  
  public SimpleLoadBalancedRetryProperties getLbConfig() {
    return this.lbConfig;
  }
}
