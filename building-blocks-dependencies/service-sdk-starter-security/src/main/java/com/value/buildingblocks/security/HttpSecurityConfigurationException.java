package com.value.buildingblocks.security;

public class HttpSecurityConfigurationException extends Exception {
  public HttpSecurityConfigurationException() {
    super("Incorrect HttpSecurity configuration.");
  }
  
  public HttpSecurityConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public HttpSecurityConfigurationException(String message) {
    super(message);
  }
  
  public HttpSecurityConfigurationException(Throwable cause) {
    super("Incorrect HttpSecurity configuration.", cause);
  }
}
