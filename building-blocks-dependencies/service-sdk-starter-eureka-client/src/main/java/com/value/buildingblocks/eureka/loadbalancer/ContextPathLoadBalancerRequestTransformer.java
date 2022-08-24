package com.value.buildingblocks.eureka.loadbalancer;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequestTransformer;
import org.springframework.http.HttpRequest;

public class ContextPathLoadBalancerRequestTransformer implements LoadBalancerRequestTransformer {
  private static final String METADATA_KEY_CONTEXT_PATH = "contextPath";
  
  public HttpRequest transformRequest(HttpRequest request, ServiceInstance instance) {
    if (instance == null)
      return request; 
    String contextPath = (String)instance.getMetadata().get("contextPath");
    return (HttpRequest)new ContextPathHttpRequestWrapper(request, contextPath);
  }
}
