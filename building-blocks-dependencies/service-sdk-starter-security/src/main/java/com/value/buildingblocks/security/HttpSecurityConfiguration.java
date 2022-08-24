package com.value.buildingblocks.security;

import com.value.buildingblocks.backend.security.auth.config.BasicAuthenticationConfigurationProperties;
import com.value.buildingblocks.backend.security.auth.config.PublicPathConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@ConditionalOnProperty(value = {"value.security.http.enabled"}, havingValue = "true", matchIfMissing = true)
@EnableWebSecurity
@EnableConfigurationProperties({PublicPathConfigurationProperties.class, BasicAuthenticationConfigurationProperties.class})
@Import({CoreHttpSecurityConfigurerAdapter.class, CsrfDisabledConfiguration.class})
public class HttpSecurityConfiguration {
  public static final int ERROR_HTTP_SECURITY_CONFIGURER_ORDER = 10;
  
  public static final int STATELESS_HTTP_SECURITY_CONFIGURER_ORDER = 20;
  
  public static final int SCOPED_ACCESS_HTTP_SECURITY_CONFIGURER_ORDER = 25;
  
  public static final int PUBLIC_PATHS_HTTP_SECURITY_CONFIGURER_ORDER = 30;
  
  public static final int ACTUATOR_HTTP_SECURITY_CONFIGURER_ORDER = 41;
  
  public static final int ACTUATOR_BASIC_AUTH_CONFIGURER_ORDER = 40;
  
  public static final int ORIGINATING_USER_FILTER_CONFIGURER_ORDER = 50;
  
  public static final int AUTHORIZED_HTTP_SECURITY_CONFIGURER_ORDER = 100;
  
  @Bean
  @Order(10)
  @ConditionalOnProperty(value = {"value.security.http.error-configurer-enabled"}, havingValue = "true", matchIfMissing = true)
  public ErrorHttpSecurityConfigurer errorHttpSecurityConfigurer() {
    return new ErrorHttpSecurityConfigurer();
  }
  
  @Bean
  @Order(20)
  @ConditionalOnProperty(value = {"value.security.http.stateless-configurer-enabled"}, havingValue = "true", matchIfMissing = true)
  public StatelessHttpSecurityConfigurer statelessHttpSecurityConfigurer() {
    return new StatelessHttpSecurityConfigurer();
  }
  
  @Bean
  @Order(30)
  @ConditionalOnProperty(value = {"value.security.http.public-paths-configurer-enabled"}, havingValue = "true", matchIfMissing = true)
  public PublicPathsHttpSecurityConfigurer publicHttpSecurityConfigurer(PublicPathConfigurationProperties properties) {
    return new PublicPathsHttpSecurityConfigurer(properties.getPaths());
  }
  
  @Bean
  @Order(41)
  @ConditionalOnProperty(value = {"value.security.http.actuator-configurer-enabled"}, havingValue = "true", matchIfMissing = true)
  @ConditionalOnClass(name = {"org.springframework.boot.actuate.health.HealthEndpoint"})
  public ActuatorHttpSecurityConfigurer actuatorHttpSecurityConfigurer() {
    return new ActuatorHttpSecurityConfigurer();
  }
  
  @Bean
  @Order(40)
  @ConditionalOnClass(name = {"org.springframework.boot.actuate.metrics.export.prometheus.PrometheusScrapeEndpoint"})
  public PrometheusSecurityConfigurer actuatorPrometheusConfigurer() {
    return new PrometheusSecurityConfigurer();
  }
  
  @Bean
  @Order(100)
  @ConditionalOnProperty(value = {"value.security.http.authorized-configurer-enabled"}, havingValue = "true", matchIfMissing = true)
  public HttpSecurityConfigurer authorizedHttpSecurityConfigurer() {
    return new AuthorizedHttpSecurityConfigurer();
  }
  
  @Bean
  @ConditionalOnMissingBean({PasswordEncoder.class})
  public PasswordEncoder defaultPasswordEncoder() {
    DelegatingPasswordEncoder passwordEncoder = (DelegatingPasswordEncoder)PasswordEncoderFactories.createDelegatingPasswordEncoder();
    passwordEncoder.setDefaultPasswordEncoderForMatches(new DefaultIdPasswordEncoder());
    return (PasswordEncoder)passwordEncoder;
  }
}
