package com.value.buildingblocks.logging;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.pattern.MessageConverter;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

public class ValueLoggingApplicationListener implements GenericApplicationListener {
  private static final Class<?>[] SOURCE_TYPES = new Class[] { SpringApplication.class, ApplicationContext.class };

  private static final Class<?>[] EVENT_TYPES = new Class[] { ApplicationEnvironmentPreparedEvent.class, ContextRefreshedEvent.class };

  public boolean supportsEventType(ResolvableType resolvableType) {
    return isAssignableFrom(resolvableType.getRawClass(), EVENT_TYPES);
  }

  public boolean supportsSourceType(Class<?> sourceType) {
    return isAssignableFrom(sourceType, SOURCE_TYPES);
  }

  private boolean isAssignableFrom(Class<?> type, Class<?>... supportedTypes) {
    if (type != null)
      for (Class<?> supportedType : supportedTypes) {
        if (supportedType.isAssignableFrom(type))
          return true;
      }
    return false;
  }

  public void onApplicationEvent(ApplicationEvent event) {
    if (isLogbackInUse())
      if (event instanceof ApplicationEnvironmentPreparedEvent) {
        onApplicationEnvironmentPreparedEvent((ApplicationEnvironmentPreparedEvent)event);
      } else if (event instanceof ContextRefreshedEvent) {
        onContextRefreshedEvent((ContextRefreshedEvent)event);
      }
  }

  private void onContextRefreshedEvent(ContextRefreshedEvent event) {
    configureCrlfMessageConverter(event.getApplicationContext().getEnvironment());
    configureLoggingContextName(event.getApplicationContext().getEnvironment());
  }

  private void onApplicationEnvironmentPreparedEvent(ApplicationEnvironmentPreparedEvent event) {
    configureCrlfMessageConverter((Environment)event.getEnvironment());
    configureLoggingContextName((Environment)event.getEnvironment());
  }

  private void configureCrlfMessageConverter(Environment environment) {
    LoggingProperties loggingProperties = new LoggingProperties();
    Binder binder = Binder.get(environment);
    binder.bind("value.logging", Bindable.ofInstance(loggingProperties));
    if (loggingProperties.getCrlfProtection().isEnabled()) {
      CrlfMessageConverter.setTarget(loggingProperties.getCrlfProtection().getTarget());
      CrlfMessageConverter.setReplacement(loggingProperties.getCrlfProtection().getReplacement());
      PatternLayout.defaultConverterMap.put("m", CrlfMessageConverter.class
          .getName());
      PatternLayout.defaultConverterMap.put("msg", CrlfMessageConverter.class
          .getName());
      PatternLayout.defaultConverterMap.put("message", CrlfMessageConverter.class
          .getName());
    } else {
      resetDefaultConverter("m");
      resetDefaultConverter("msg");
      resetDefaultConverter("message");
    }
  }

  private void resetDefaultConverter(String key) {
    if (CrlfMessageConverter.class.getName()
      .equals(PatternLayout.defaultConverterMap.get(key)))
      PatternLayout.defaultConverterMap.put(key, MessageConverter.class.getName());
  }

  private boolean isLogbackInUse() {
    return LoggingSystem.get(getClass().getClassLoader()) instanceof org.springframework.boot.logging.logback.LogbackLoggingSystem;
  }

  private void configureLoggingContextName(Environment environment) {
    ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
    if (loggerFactory instanceof LoggerContext) {
      LoggerContext loggerContext = (LoggerContext)loggerFactory;
      if (shouldOverrideLoggerContextName(loggerContext.getName())) {
        String appName = environment.getProperty("spring.zipkin.service.name");
        if (StringUtils.isEmpty(appName))
          appName = environment.getProperty("spring.application.name");
        if (!StringUtils.isEmpty(appName))
          loggerContext.setName(appName);
      }
    } else {
      LoggerFactory.getLogger(getClass()).info("Cannot set log context name to application name; Logback is not in use (ILoggerFactory implementation is {}).", loggerFactory
          .getClass());
    }
  }

  private boolean shouldOverrideLoggerContextName(String loggerContextName) {
    return (loggerContextName == null || loggerContextName.equals("default"));
  }

  public int getOrder() {
    return Integer.MAX_VALUE;
  }
}
