package com.value.buildingblocks.access;

import com.value.buildingblocks.security.HttpSecurityConfigurationException;
import com.value.buildingblocks.security.HttpSecurityConfigurer;
import javax.servlet.Filter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

public class ScopedAccessHttpSecurityFilterConfigurer implements HttpSecurityConfigurer {
  private final ScopedAccessSecurityFilter filter;
  
  public ScopedAccessHttpSecurityFilterConfigurer(ScopedAccessSecurityFilter filter) {
    this.filter = filter;
  }
  
  public void configure(HttpSecurity http) throws HttpSecurityConfigurationException {
    http.addFilterAfter((Filter)this.filter, FilterSecurityInterceptor.class);
  }
  
  public String toString() {
    return getClass().getSimpleName() + ": Adding " + this.filter + " after FilterSecurityInterceptor";
  }
}
