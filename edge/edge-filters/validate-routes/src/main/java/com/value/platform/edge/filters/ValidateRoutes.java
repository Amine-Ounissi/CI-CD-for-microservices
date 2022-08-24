package com.value.platform.edge.filters;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

@Component
public class ValidateRoutes extends AbstractGatewayFilterFactory {
  private static final Logger log = LoggerFactory.getLogger(ValidateRoutes.class);
  
  private final PathMatcher pathMatcher = new AntPathMatcher();
  
  private ValidateRoutesProperties properties;
  
  public ValidateRoutes(ValidateRoutesProperties properties) {
    this.properties = properties;
  }
  
  public GatewayFilter apply(Object config) {
    return (exchange, chain) -> {
        String path = exchange.getRequest().getPath().pathWithinApplication().value();
        if (matchesIgnoredPatterns(path, this.properties.getIgnoredPatterns()))
          throw NotFoundException.create(true, "Not Found"); 
        return chain.filter(exchange);
      };
  }
  
  private boolean matchesIgnoredPatterns(String path, List<String> patterns) {
    for (String pattern : patterns) {
      if (this.pathMatcher.match(pattern, path)) {
        log.debug("Path {} matches ignored pattern {}", path, pattern);
        return true;
      } 
    } 
    return false;
  }
}
