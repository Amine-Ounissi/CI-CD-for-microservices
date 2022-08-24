package com.value.buildingblocks.security;

import com.value.buildingblocks.jwt.internal.InternalJwtConsumer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@ConditionalOnClass({InternalJwtConsumer.class})
public class OriginatingUserFilterConfiguration {
  @Bean
  @Order(50)
  @ConditionalOnBean({InternalJwtConsumer.class})
  @ConditionalOnProperty(value = {"value.security.http.originating-user-configurer-enabled"}, havingValue = "true", matchIfMissing = true)
  public OriginatingUserFilterConfigurer originatingUserFilterConfigurer(InternalJwtConsumer internalJwtConsumer) {
    OriginatingUserFilter filter = new OriginatingUserFilter(internalJwtConsumer);
    return new OriginatingUserFilterConfigurer(filter);
  }
}
