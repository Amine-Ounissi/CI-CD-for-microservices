package com.value.buildingblocks.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public class CsrfDisabledHttpSecurityConfigurer implements HttpSecurityConfigurer {
  public void configure(HttpSecurity http) throws HttpSecurityConfigurationException {
    try {
      http.csrf().disable();
    } catch (Exception e) {
      throw new HttpSecurityConfigurationException("Unable to disable CSRF", e);
    } 
  }
  
  public String toString() {
    return "http.csrf().disable()";
  }
}
