package com.value.buildingblocks.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@FunctionalInterface
public interface HttpSecurityConfigurer {
  void configure(HttpSecurity paramHttpSecurity) throws HttpSecurityConfigurationException;
}
