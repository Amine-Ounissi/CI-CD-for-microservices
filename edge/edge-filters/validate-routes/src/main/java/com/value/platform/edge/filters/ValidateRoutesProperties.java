package com.value.platform.edge.filters;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("gateway")
public class ValidateRoutesProperties {
  private List<String> ignoredPatterns = new ArrayList<>();
  
  public List<String> getIgnoredPatterns() {
    return this.ignoredPatterns;
  }
  
  public void setIgnoredPatterns(List<String> ignoredPatterns) {
    this.ignoredPatterns = ignoredPatterns;
  }
}
