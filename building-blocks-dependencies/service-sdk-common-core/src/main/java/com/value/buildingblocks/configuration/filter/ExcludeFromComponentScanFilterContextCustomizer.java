package com.value.buildingblocks.configuration.filter;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

class ExcludeFromComponentScanFilterContextCustomizer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
  public void initialize(ConfigurableApplicationContext applicationContext) {
    applicationContext.getBeanFactory().registerSingleton(ExcludeFromComponentScanTypeExcludeFilter.class.getName(), new ExcludeFromComponentScanTypeExcludeFilter());
  }
}
