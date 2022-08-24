package com.value.buildingblocks.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

public class ErrorHttpSecurityConfigurer implements HttpSecurityConfigurer {
  public void configure(HttpSecurity http) throws HttpSecurityConfigurationException {
    try {
      ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)http.authorizeRequests().antMatchers(new String[] { "/error" })).permitAll();
      http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setHeader("WWW-Authenticate", "Bearer error=\"invalid_token\"");
          });
    } catch (Exception e) {
      throw new HttpSecurityConfigurationException("Error configuring authenticationEntryPoint", e);
    } 
  }
  
  public String toString() {
    return getClass().getSimpleName() + " Allow anonymous access to /error endpoint, configures 401 response for invalid tokens";
  }
}
