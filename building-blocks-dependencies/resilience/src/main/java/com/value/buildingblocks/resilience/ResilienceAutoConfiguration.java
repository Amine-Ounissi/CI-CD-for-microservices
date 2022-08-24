package com.value.buildingblocks.resilience;

import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Configuration
@PropertySource({"classpath:resilience.properties"})
@AutoConfigureAfter(name = {"org.springframework.cloud.netflix.hystrix.HystrixAutoConfiguration"})
@ConditionalOnClass({HystrixCommandAspect.class})
public class ResilienceAutoConfiguration {
  private static final Logger log = LoggerFactory.getLogger(ResilienceAutoConfiguration.class);
  
  @Bean
  @ConditionalOnMissingBean(name = {"callableWrapperHystrixConcurrencyStrategy"})
  @ConditionalOnBean({HystrixCommandAspect.class})
  public CallableWrapperHystrixConcurrencyStrategy callableWrapperHystrixConcurrencyStrategy(List<CallableWrapperSupplier<?>> callableWrapperSuppliers) {
    log.debug("callableWrapperSuppliers={}", callableWrapperSuppliers);
    return new CallableWrapperHystrixConcurrencyStrategy(callableWrapperSuppliers);
  }
  
  @Bean
  @ConditionalOnMissingBean(name = {"requestContextCallableWrapperSupplier"})
  @ConditionalOnBean({HystrixCommandAspect.class})
  public CallableWrapperSupplier<RequestAttributes> requestContextCallableWrapperSupplier() {
    return new CallableWrapperSupplier<>(RequestContextHolder::getRequestAttributes, RequestContextHolder::setRequestAttributes, RequestContextHolder::resetRequestAttributes);
  }
}
