package com.value.platform.edge.configuration;

import java.util.Optional;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "gateway.actuator")
public class GatewayActuatorProperties {
  private String user;
  
  private String password;
  
  private Security security = new Security();
  
  public Optional<String> getUser() {
    return Optional.ofNullable(this.user);
  }
  
  public void setUser(String user) {
    this.user = user;
  }
  
  public Optional<String> getPassword() {
    return Optional.ofNullable(this.password);
  }
  
  public void setPassword(String password) {
    this.password = password;
  }
  
  public Security getSecurity() {
    return this.security;
  }
  
  public void setSecurity(Security security) {
    this.security = security;
  }

  public static final class Security {
    boolean enabled = true;

    public Security() {
    }

    public boolean isEnabled() {
      return this.enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }
  }
}
