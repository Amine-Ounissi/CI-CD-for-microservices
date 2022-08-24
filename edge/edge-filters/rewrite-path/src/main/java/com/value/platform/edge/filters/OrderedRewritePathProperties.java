package com.value.platform.edge.filters;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("gateway.filter.rewrite-path")
public class OrderedRewritePathProperties {
  private int order = 100;
  
  public int getOrder() {
    return this.order;
  }
  
  public void setOrder(int order) {
    this.order = order;
  }
}
