package com.value.buildingblocks.backend.internalrequest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.util.IdGenerator;
import org.springframework.util.JdkIdGenerator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
class InternalRequestContextConfiguration {
  @ConditionalOnMissingBean({IdGenerator.class})
  @Bean
  public IdGenerator idGenerator() {
    return (IdGenerator)new JdkIdGenerator();
  }
  
  @Bean(name = {"internalRequestContext"})
  @ConditionalOnMissingBean({InternalRequestContext.class})
  @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
  public InternalRequestContext getInternalRequestContext(IdGenerator idGenerator) {
    RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
    if (attributes instanceof ServletRequestAttributes) {
      ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)attributes;
      return DefaultInternalRequestContext.contextFrom(servletRequestAttributes.getRequest(), idGenerator
          .generateId().toString());
    } 
    throw new IllegalStateException("There are no RequestAttributes.");
  }
}
