package com.value.buildingblocks.backend.security.auth.config;

import com.value.buildingblocks.jwt.internal.InternalJwtConsumer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(name = {"com.value.buildingblocks.jwt.internal.InternalJwtConsumer"})
@EnableConfigurationProperties({ServiceApiAuthenticationProperties.class})
@AutoConfigureAfter(name = {
  "com.value.buildingblocks.backend.security.auth.config.InternalJwtConsumerAutoConfiguration",
  "com.value.buildingblocks.backend.internalrequest.InternalRequestContextConfiguration"})
class SecurityContextUtilConfiguration {

  @Bean
  @ConditionalOnBean(type = {"com.value.buildingblocks.jwt.internal.InternalJwtConsumer"})
  public SecurityContextUtil securityContextUtil(InternalJwtConsumer internalJwtConsumer,
    ServiceApiAuthenticationProperties serviceApiAuthenticationProperties) {
    return new SecurityContextUtil(internalJwtConsumer,
      serviceApiAuthenticationProperties.getRequiredScope());
  }
}
