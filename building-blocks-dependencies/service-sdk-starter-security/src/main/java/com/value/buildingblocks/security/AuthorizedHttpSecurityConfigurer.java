package com.value.buildingblocks.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

public class AuthorizedHttpSecurityConfigurer implements HttpSecurityConfigurer {
  public void configure(HttpSecurity http) throws HttpSecurityConfigurationException {
    try {
      ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)http.authorizeRequests().anyRequest()).authenticated();
    } catch (Exception e) {
      throw new HttpSecurityConfigurationException("Error configuring authorizeRequests.anyRequest().authenticated(), use antMatchers(\"/**\") instead of anyRequest()", e);
    } 
  }
  
  public String toString() {
    return getClass().getSimpleName() + ": authorizeRequests.anyRequest().authenticated()";
  }
}
