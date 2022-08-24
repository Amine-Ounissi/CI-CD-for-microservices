package com.value.buildingblocks.backend.security.auth.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "value.security.service")
public class ServiceApiConfigurationProperties {

  public static final String DEFAULT_SERVICE_API_PATH = "/service-api/**";

  private boolean enabled = true;

  private List<String> paths = new ArrayList<>();

  public ServiceApiConfigurationProperties() {
    this.paths.add("/service-api/**");
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public List<String> getPaths() {
    return this.paths;
  }

  public void setPaths(List<String> paths) {
    this.paths = paths;
  }
}
