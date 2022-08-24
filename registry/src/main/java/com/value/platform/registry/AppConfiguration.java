package com.value.platform.registry;

import com.value.platform.registry.security.auth.BasicAuthenticationConfigurationProperties;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusScrapeEndpoint;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties({BasicAuthenticationConfigurationProperties.class})
public class AppConfiguration extends WebSecurityConfigurerAdapter {
  private static final Logger log = LoggerFactory.getLogger(AppConfiguration.class);
  
  @Value("${management.security.roles:ACTUATOR}")
  private List<String> actuatorRoles = Collections.singletonList("ACTUATOR");
  
  protected void configure(HttpSecurity http) throws Exception {
    ((HttpSecurity)((HttpSecurity)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)((HttpSecurity)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)((HttpSecurity)http.csrf().disable())
      .requestMatcher((RequestMatcher)EndpointRequest.toAnyEndpoint())
      .authorizeRequests()
      .requestMatchers(new RequestMatcher[] { (RequestMatcher)EndpointRequest.to(new Class[] { PrometheusScrapeEndpoint.class }) })).hasAnyRole(this.actuatorRoles.<String>toArray(new String[this.actuatorRoles.size()]))
      .and())
      .authorizeRequests()
      .anyRequest())
      .permitAll()
      .and())
      .httpBasic()
      .and()).sessionManagement()
      .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }
  
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
}
