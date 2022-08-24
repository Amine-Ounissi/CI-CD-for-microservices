package com.value.buildingblocks.backend.internalrequest;

import com.value.buildingblocks.jwt.internal.InternalJwtConsumer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(name = {"com.value.buildingblocks.jwt.internal.InternalJwtConsumer"})
@AutoConfigureAfter(name = {"com.value.buildingblocks.backend.security.auth.config.InternalJwtConsumerAutoConfiguration"})
class InternalRequestUtilConfiguration {
  @Bean
  @ConditionalOnBean(type = {"com.value.buildingblocks.jwt.internal.InternalJwtConsumer"})
  public InternalRequestUtil internalRequestUtil(InternalJwtConsumer internalJwtConsumer) {
    return new InternalRequestUtil(internalJwtConsumer);
  }
}
