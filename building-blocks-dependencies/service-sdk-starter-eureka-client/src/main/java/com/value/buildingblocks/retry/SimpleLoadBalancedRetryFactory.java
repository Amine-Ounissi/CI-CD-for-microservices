package com.value.buildingblocks.retry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryPolicy;
import org.springframework.cloud.client.loadbalancer.ServiceInstanceChooser;
import org.springframework.core.env.Environment;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.NoBackOffPolicy;

public class SimpleLoadBalancedRetryFactory implements LoadBalancedRetryFactory {
  private Map<String, SimpleLoadBalancedRetryProperties> serviceProperties = new ConcurrentHashMap<>();
  
  private final RetryBackOffPolicyFactory backOffPolicyFactory;
  
  private final Binder binder;
  
  public SimpleLoadBalancedRetryFactory(Environment environment) {
    this(environment, null);
  }
  
  public SimpleLoadBalancedRetryFactory(Environment environment, RetryBackOffPolicyFactory backOffPolicyFactory) {
    this.backOffPolicyFactory = backOffPolicyFactory;
    this.binder = Binder.get(environment);
  }
  
  public BackOffPolicy createBackOffPolicy(String service) {
    return (this.backOffPolicyFactory != null) ? this.backOffPolicyFactory.createBackOffPolicy(service) : (BackOffPolicy)new NoBackOffPolicy();
  }
  
  public LoadBalancedRetryPolicy createRetryPolicy(String service, ServiceInstanceChooser serviceInstanceChooser) {
    return new SimpleLoadBalancedRetryPolicy(service, this.serviceProperties
        
        .computeIfAbsent(service, this::createRetryProperties), serviceInstanceChooser);
  }
  
  public SimpleLoadBalancedRetryProperties createRetryProperties(String service) {
    SimpleLoadBalancedRetryProperties lbConfig = new SimpleLoadBalancedRetryProperties();
    Bindable bindable = Bindable.ofInstance(lbConfig);
    this.binder.bind("value.loadbalancer", bindable);
    if (service != null)
      this.binder.bind("value.loadbalancer." + service.toLowerCase(), bindable);
    return lbConfig;
  }
}
