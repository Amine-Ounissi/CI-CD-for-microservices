package com.value.buildingblocks.logging;

import java.util.HashMap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.core.Ordered;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

public class ValueLoggingEnvironmentProcessor implements EnvironmentPostProcessor, Ordered {

  private static final String DEFAULT_PROPERTY_SOURCE_NAME = "defaultProperties";

  public void postProcessEnvironment(ConfigurableEnvironment environment,
    SpringApplication application) {
    MapPropertySource defaultPropertiesSource = getDefaultPropertiesSource(environment);
    setDefaultLoggingProperties(defaultPropertiesSource);
    LoggingSystem loggingSystem = LoggingSystem.get(LoggingSystem.class.getClassLoader());
    if (loggingSystem instanceof org.springframework.boot.logging.logback.LogbackLoggingSystem) {
      boolean sleuthIsAvailable = false;
      try {
        Class.forName("org.springframework.cloud.sleuth.autoconfig.TraceEnvironmentPostProcessor");
        sleuthIsAvailable = true;
      } catch (ClassNotFoundException ignored) {
      }
      if (sleuthIsAvailable) {
        setDefaultLoggingLevelPattern(defaultPropertiesSource);
      }
    }
  }

  private MapPropertySource getDefaultPropertiesSource(ConfigurableEnvironment environment) {
    MapPropertySource targetPropertySource;
    MapPropertySource mapPropertySource1 = new MapPropertySource("defaultProperties", new HashMap<>());
    environment.getPropertySources().addLast(mapPropertySource1);
    targetPropertySource = mapPropertySource1;
    return targetPropertySource;
  }

  private void setDefaultLoggingProperties(MapPropertySource defaultPropertiesSource) {
    defaultPropertiesSource.getSource().put(
      "logging.level.org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver",
      "ERROR");
  }

  private void setDefaultLoggingLevelPattern(MapPropertySource defaultPropertiesSource) {
    defaultPropertiesSource.getSource().put("logging.pattern.level",
      "%5p [%cn,%X{X-B3-TraceId:-},%X{X-B3-SpanId:-},%X{X-Span-Export:-}]");
  }

  public int getOrder() {
    return Integer.MIN_VALUE;
  }
}
