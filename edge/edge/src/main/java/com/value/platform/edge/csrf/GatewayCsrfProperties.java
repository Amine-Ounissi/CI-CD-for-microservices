package com.value.platform.edge.csrf;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "gateway.csrf")
public class GatewayCsrfProperties {
  private boolean enabled;
  
  private String cookieName;
  
  private String mobileUserAgent;
  
  private List<String> ignoredPaths;
  
  public boolean isEnabled() {
    return this.enabled;
  }
  
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
  
  public String getCookieName() {
    return this.cookieName;
  }
  
  public void setCookieName(String cookieName) {
    this.cookieName = cookieName;
  }
  
  public String getMobileUserAgent() {
    return this.mobileUserAgent;
  }
  
  public void setMobileUserAgent(String mobileUserAgent) {
    this.mobileUserAgent = mobileUserAgent;
  }
  
  public List<String> getIgnoredPaths() {
    return this.ignoredPaths;
  }
  
  public void setIgnoredPaths(List<String> ignoredPaths) {
    this.ignoredPaths = ignoredPaths;
  }
}
