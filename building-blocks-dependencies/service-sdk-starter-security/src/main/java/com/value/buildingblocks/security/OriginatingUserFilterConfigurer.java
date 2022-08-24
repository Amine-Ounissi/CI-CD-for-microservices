package com.value.buildingblocks.security;

import javax.servlet.Filter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

public class OriginatingUserFilterConfigurer implements HttpSecurityConfigurer {
  final OriginatingUserFilter filter;
  
  public OriginatingUserFilterConfigurer(OriginatingUserFilter filter) {
    this.filter = filter;
  }
  
  public void configure(HttpSecurity http) throws HttpSecurityConfigurationException {
    http.addFilterBefore((Filter)this.filter, FilterSecurityInterceptor.class);
  }
  
  public String toString() {
    return getClass().getSimpleName() + ": Adding " + this.filter + " before FilterSecurityInterceptor";
  }
}
