package com.value.buildingblocks.backend.security.auth.config;

import com.value.buildingblocks.security.HttpSecurityConfiguration;
import com.value.buildingblocks.security.HttpSecurityConfigurer;
import com.value.buildingblocks.security.csrf.AutoCsrfWebSecurityConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@AutoConfigureBefore({AutoCsrfWebSecurityConfiguration.class, HttpSecurityConfiguration.class})
@Import({CustomPermissionEvaluatorSecurityConfig.class})
@PropertySource({"classpath:value-auth-security.properties"})
@EnableConfigurationProperties({ServiceApiAuthenticationProperties.class,
  MutualTlsConfigurationProperties.class, PublicPathConfigurationProperties.class,
  ServiceApiConfigurationProperties.class})
class AuthSecurityAutoConfiguration {

  @Bean({"functionalAccessControlDefault"})
  @ConditionalOnMissingBean
  public FunctionalAccessControl functionalAccessControlDefault() {
    return new FunctionalAccessControlDefault();
  }

  @Bean({"peerServiceExpressionHandler"})
  @ConditionalOnMissingBean
  @ConditionalOnMissingClass({
    "com.value.buildingblocks.jwt.internal.authentication.InternalJwtAuthentication"})
  public PeerServiceExpressionHandler alwaysFalsePeerServiceExpressionHandler() {
    return new AlwaysFalsePeerServiceExpressionHandler();
  }

  @Bean({"peerServiceExpressionHandler"})
  @ConditionalOnMissingBean
  @ConditionalOnClass(name = {
    "com.value.buildingblocks.jwt.internal.authentication.InternalJwtAuthentication"})
  public PeerServiceExpressionHandler defaultPeerServiceExpressionHandler(
    ServiceApiAuthenticationProperties serviceApiAuthenticationProperties) {
    return new DefaultPeerServiceExpressionHandler(
      serviceApiAuthenticationProperties.getRequiredScope());
  }

  @Bean({"mutualTlsUserDetailsService"})
  @ConditionalOnMissingBean(name = {"mutualTlsUserDetailsService"})
  @ConditionalOnProperty(name = {
    "value.security.mtls.enabled"}, havingValue = "true", matchIfMissing = true)
  public UserDetailsService mutualTlsUserDetailsService() {
    return new MutualTlsUserDetailsService();
  }

  @Bean
  @Order(80)
  @ConditionalOnProperty(value = {
    "value.security.mtls.enabled"}, havingValue = "true", matchIfMissing = true)
  public HttpSecurityConfigurer mtlsHttpSecurityConfigurer() {
    return new MtlsHttpSecurityConfigurer();
  }

  @Bean
  @Order(81)
  @ConditionalOnProperty(value = {
    "value.security.service.enabled"}, havingValue = "true", matchIfMissing = true)
  public HttpSecurityConfigurer serviceApiHttpSecurityConfigurer() {
    return new ServiceApiHttpSecurityConfigurer();
  }
}
