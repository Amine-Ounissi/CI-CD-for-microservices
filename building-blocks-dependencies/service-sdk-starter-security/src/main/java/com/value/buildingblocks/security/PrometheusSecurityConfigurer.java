package com.value.buildingblocks.security;

import com.value.buildingblocks.backend.security.auth.config.BasicAuthenticationConfigurationProperties;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusScrapeEndpoint;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class PrometheusSecurityConfigurer implements HttpSecurityConfigurer {
  private static final Logger log = LoggerFactory.getLogger(PrometheusSecurityConfigurer.class);
  
  @Value("${management.security.roles:ACTUATOR}")
  private List<String> actuatorRoles = Collections.singletonList("ACTUATOR");
  
  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth, BasicAuthenticationConfigurationProperties properties) throws Exception {
    if (properties.isUsernameDefaultValue() && properties.isPasswordDefaultValue()) {
      log.warn("No credentials set for Prometheus. Check community for supplying Basic Authentication credentials");
      return;
    } 
    PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    auth.inMemoryAuthentication()
      .withUser(properties.getUsername()).password(encoder.encode(properties.getPassword()))
      .roles(this.actuatorRoles.<String>toArray(new String[this.actuatorRoles.size()]));
  }
  
  public void configure(HttpSecurity http) throws HttpSecurityConfigurationException {
    try {
      ((HttpSecurity)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)http.authorizeRequests()
        .requestMatchers(new RequestMatcher[] { (RequestMatcher)EndpointRequest.to(new Class[] { PrometheusScrapeEndpoint.class }) })).hasAnyRole(this.actuatorRoles.<String>toArray(new String[this.actuatorRoles.size()]))
        .and())
        .httpBasic();
    } catch (Exception e) {
      throw new HttpSecurityConfigurationException(e);
    } 
  }
  
  public String toString() {
    return getClass().getSimpleName() + ": httpBasic() for roles " + this.actuatorRoles;
  }
}
