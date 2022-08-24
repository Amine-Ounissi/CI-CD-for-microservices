package com.value.buildingblocks.common;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.ResourcePropertySource;

public abstract class ResourcePropertiesApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
  private static final Logger log = LoggerFactory.getLogger(ResourcePropertiesApplicationContextInitializer.class);
  
  private final String location;
  
  private final boolean ignoreResourceNotFound;
  
  protected ResourcePropertiesApplicationContextInitializer(String location, boolean ignoreResourceNotFound) {
    this.location = location;
    this.ignoreResourceNotFound = ignoreResourceNotFound;
  }
  
  protected ResourcePropertiesApplicationContextInitializer(String location) {
    this(location, false);
  }
  
  public void initialize(ConfigurableApplicationContext applicationContext) {
    log.debug("loading [{}]", this.location);
    ConfigurableEnvironment environment = applicationContext.getEnvironment();
    try {
      environment.getPropertySources().addLast((PropertySource)new ResourcePropertySource(this.location));
    } catch (IOException e) {
      if (this.ignoreResourceNotFound) {
        log.info("Properties location [{}] not resolvable: {}", this.location, e.getMessage());
      } else {
        throw new IllegalStateException("Properties location [" + this.location + "] not resolvable.", e);
      } 
    } 
  }
}
