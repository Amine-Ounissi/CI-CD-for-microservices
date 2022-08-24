package com.value.buildingblocks.webclient;

import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.web.util.UriComponentsBuilder;

public class ContextPathAwareReactorLoadBalancerExchangeFilterFunction extends ReactorLoadBalancerExchangeFilterFunction {
  private static final Logger logger = LoggerFactory.getLogger(com.value.buildingblocks.webclient.ContextPathAwareReactorLoadBalancerExchangeFilterFunction.class);
  
  public ContextPathAwareReactorLoadBalancerExchangeFilterFunction(ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerFactory) {
    super(loadBalancerFactory);
  }
  
  static URI reconstructURIWithContextPath(String contextPath, URI original) {
    if (contextPath == null)
      return original; 
    logger.debug("Original request URL: {}", original);
    String pathWithContext = contextPath.concat(original.getPath());
    try {
      URI uri = UriComponentsBuilder.fromUri(original).replacePath(pathWithContext).build(true).toUri();
      logger.debug("Transformed request URL: {}", uri);
      return uri;
    } catch (Exception e) {
      logger.warn("An error occurred while building the load balanced URI with service context: {}. Check the service configuration.", pathWithContext);
      return original;
    } 
  }
  
  protected URI reconstructURI(ServiceInstance instance, URI original) {
    String contextPath = (String)instance.getMetadata().get("contextPath");
    return super.reconstructURI(instance, reconstructURIWithContextPath(contextPath, original));
  }
}
