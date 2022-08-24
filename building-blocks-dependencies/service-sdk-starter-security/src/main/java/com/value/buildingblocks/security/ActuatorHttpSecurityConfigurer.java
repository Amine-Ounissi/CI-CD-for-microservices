package com.value.buildingblocks.security;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class ActuatorHttpSecurityConfigurer implements HttpSecurityConfigurer {

  @Value("${management.security.roles:ACTUATOR}")
  private List<String> actuatorRoles = Collections.singletonList("ACTUATOR");

  public void configure(HttpSecurity http) throws HttpSecurityConfigurationException {
    try {
      ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) http.authorizeRequests()
        .requestMatchers(new RequestMatcher[]{
          EndpointRequest.to(HealthEndpoint.class)})).permitAll();
      ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) http.authorizeRequests()
        .requestMatchers(new RequestMatcher[]{EndpointRequest.toAnyEndpoint()}))
        .hasAnyRole(this.actuatorRoles.toArray(new String[0]));
    } catch (Exception e) {
      throw new HttpSecurityConfigurationException("Error configuring access to actuator", e);
    }
  }

  public String toString() {
    return getClass().getSimpleName() + ": authorizeRequests() for roles " + this.actuatorRoles;
  }
}
