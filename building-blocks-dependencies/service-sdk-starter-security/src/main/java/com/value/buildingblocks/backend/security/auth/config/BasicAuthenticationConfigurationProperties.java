package com.value.buildingblocks.backend.security.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "value.basic-authentication")
public class BasicAuthenticationConfigurationProperties {
  private String username;
  
  private String password;
  
  public String getUsername() {
    return this.username;
  }
  
  public void setUsername(String username) {
    this.username = username;
  }
  
  public String getPassword() {
    return this.password;
  }
  
  public void setPassword(String password) {
    this.password = password;
  }
  
  public boolean isUsernameDefaultValue() {
    return (this.username == null);
  }
  
  public boolean isPasswordDefaultValue() {
    return (this.password == null);
  }
}
