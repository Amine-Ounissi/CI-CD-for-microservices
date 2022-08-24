package com.value.buildingblocks.security;

import java.util.Map;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.ErrorPageFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterRegistrationBeanConfiguration {
  private static final Logger log = LoggerFactory.getLogger(FilterRegistrationBeanConfiguration.class);
  
  @Bean
  InitializingBean disableDefaultFilterRegistrationBean(ApplicationContext context) {
    return () -> {
        Map<String, FilterRegistrationBean> registrations = context.getBeansOfType(FilterRegistrationBean.class);
        FilterRegistrationBean errorPageFilterRegistration = registrations.get("errorPageFilterRegistration");
        if (errorPageFilterRegistration != null) {
          errorPageFilterRegistration.setEnabled(false);
          log.debug("disabled \"errorPageFilterRegistration\"");
        } 
      };
  }
  
  @Bean
  @ConditionalOnBean(name = {"errorPageFilterRegistration"})
  FilterRegistrationBean<ErrorPageFilter> highestPrecedenceErrorPageFilterRegistration(ErrorPageFilter errorPageFilter) {
    FilterRegistrationBean<ErrorPageFilter> registration = new FilterRegistrationBean((Filter)errorPageFilter, new org.springframework.boot.web.servlet.ServletRegistrationBean[0]);
    registration.setOrder(-2147483647);
    registration.setDispatcherTypes(DispatcherType.REQUEST, new DispatcherType[] { DispatcherType.ASYNC });
    return registration;
  }
}
