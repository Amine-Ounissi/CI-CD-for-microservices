package com.value.buildingblocks.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

public class StatelessHttpSecurityConfigurer implements HttpSecurityConfigurer {
  public void configure(HttpSecurity http) throws HttpSecurityConfigurationException {
    try {
      http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    } catch (Exception e) {
      throw new HttpSecurityConfigurationException("Error configuring HTTP Session to be stateless", e);
    } 
  }
  
  public String toString() {
    return getClass().getSimpleName() + ": sessionCreationPolicy(SessionCreationPolicy.STATELESS)";
  }
}
