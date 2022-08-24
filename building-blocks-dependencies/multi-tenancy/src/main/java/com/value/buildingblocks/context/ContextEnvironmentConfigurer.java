package com.value.buildingblocks.context;

import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyNameException;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SystemEnvironmentPropertySource;

public class ContextEnvironmentConfigurer {
  private static final Logger log = LoggerFactory.getLogger(ContextEnvironmentConfigurer.class);
  
  private final ContextSupplier contextSupplier;
  
  private final ContextSetter contextSetter;
  
  private final ContextEnumerationSupplier contextEnumerationSupplier;
  
  private final ContextProperties contextProperties;
  
  public ContextEnvironmentConfigurer(ContextSupplier contextSupplier, ContextEnumerationSupplier contextEnumerationSupplier, ContextSetter contextSetter, ContextProperties contextProperties) {
    this.contextSupplier = contextSupplier;
    this.contextEnumerationSupplier = contextEnumerationSupplier;
    this.contextSetter = contextSetter;
    this.contextProperties = contextProperties;
  }
  
  @EventListener
  public void onEnvironmentChangeEvent(EnvironmentChangeEvent environmentChangeEvent) {
    log.info("EnvironmentChangeEvent issued; resetting ContextScoped beans");
    Object applicationContext = environmentChangeEvent.getSource();
    if (applicationContext instanceof ApplicationContext) {
      reset((ApplicationContext)applicationContext);
    } else {
      log.error("EnvironmentChangeEvent from unexpected source! {}", applicationContext);
    } 
  }
  
  @EventListener
  public void onApplicationEvent(ContextRefreshedEvent event) {
    log.debug("initialise");
    reset(event.getApplicationContext());
  }
  
  private void reset(ApplicationContext applicationContext) {
    if (applicationContext.getBeansWithAnnotation(ContextScoped.class).isEmpty()) {
      log.info("There are no ContextScoped beans. Not configuring the environment for ContextScope");
      return;
    } 
    ContextScope contextScope = (ContextScope)applicationContext.getBean(ContextScope.class);
    contextScope.clearBeans();
    ConfigurableEnvironment environment = (ConfigurableEnvironment)applicationContext.getBean(ConfigurableEnvironment.class);
    for (PropertySource<?> propertySource : (Iterable<PropertySource<?>>)environment.getPropertySources()) {
      validatePropertyNames(propertySource);
      environment.getPropertySources().replace(propertySource.getName(), 
          createContextPropertySource(propertySource, this.contextSupplier));
    } 
    try {
      for (ContextQualifier context : this.contextEnumerationSupplier.getContexts()) {
        this.contextSetter.setContext(context);
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(ContextScoped.class);
        if (log.isDebugEnabled())
          for (Map.Entry<String, Object> entry : beans.entrySet()) {
            log.debug("{} Initialise {}={}", new Object[] { context, entry.getKey(), entry.getValue() });
          }  
      } 
    } finally {
      this.contextSetter.setContext(ContextConstants.EMPTY_CONTEXT_QUALIFIER);
    } 
  }
  
  private void validatePropertyNames(PropertySource<?> propertySource) {
    if (propertySource instanceof EnumerablePropertySource) {
      EnumerablePropertySource enumerablePropertySource = (EnumerablePropertySource)propertySource;
      for (String name : enumerablePropertySource.getPropertyNames()) {
        try {
          ContextSupportUtil.checkName(name);
        } catch (InvalidConfigurationPropertyNameException e) {
          if (this.contextProperties.isErrorOnInvalidPropertyName()) {
            InvalidConfigurationPropertyNameException e2 = new InvalidConfigurationPropertyNameException(name, e.getInvalidCharacters());
            e2.initCause((Throwable)new BeanCreationException(getResourceDescription(enumerablePropertySource, name), enumerablePropertySource
                  .getName(), "Context qualified properties need to be in canonical format (lowercase, kebab)"));
            throw e2;
          } 
          log.warn("Unsupported context qualified property key '{}' in '{}' Canonical names should be kebab-case ('-' separated), lowercase alpha-numeric characters and must start with a letter", name, 

              
              getResourceDescription(enumerablePropertySource, name));
        } 
      } 
    } 
  }
  
  private String getResourceDescription(EnumerablePropertySource enumerablePropertySource, String name) {
    if (enumerablePropertySource instanceof OriginTrackedMapPropertySource) {
      OriginTrackedMapPropertySource originTrackedMapPropertySource = (OriginTrackedMapPropertySource)enumerablePropertySource;
      return Objects.toString(originTrackedMapPropertySource.getOrigin(name), "Unknown");
    } 
    return Objects.toString(enumerablePropertySource.getSource(), "Unknown");
  }
  
  private PropertySource<?> createContextPropertySource(PropertySource<?> originalPropertySource, ContextSupplier contextSupplier) {
    if (originalPropertySource instanceof ContextSystemEnvironmentPropertySource) {
      log.debug("Not replacing originalPropertySource {}", originalPropertySource.getName());
      return originalPropertySource;
    } 
    if (originalPropertySource instanceof ContextEnumerablePropertySource) {
      log.debug("Not replacing ContextEnumerablePropertySource {}", originalPropertySource.getName());
      return originalPropertySource;
    } 
    if (originalPropertySource instanceof ContextPropertySource) {
      log.debug("Not replacing ContextPropertySource {}", originalPropertySource.getName());
      return originalPropertySource;
    } 
    if (originalPropertySource instanceof ContextMapPropertySource) {
      log.debug("Not replacing ContextMapPropertySource {}", originalPropertySource.getName());
      return originalPropertySource;
    } 
    if (originalPropertySource instanceof SystemEnvironmentPropertySource) {
      log.debug("Replacing SystemEnvironmentPropertySource {}", originalPropertySource.getName());
      return (PropertySource<?>)new ContextSystemEnvironmentPropertySource((SystemEnvironmentPropertySource)originalPropertySource, contextSupplier);
    } 
    if (originalPropertySource instanceof MapPropertySource) {
      log.debug("Replacing MapPropertySource {}", originalPropertySource.getName());
      return (PropertySource<?>)new ContextMapPropertySource((MapPropertySource)originalPropertySource, contextSupplier);
    } 
    if (originalPropertySource instanceof EnumerablePropertySource) {
      log.debug("Replacing EnumerablePropertySource {}", originalPropertySource.getName());
      return (PropertySource<?>)new ContextEnumerablePropertySource((EnumerablePropertySource)originalPropertySource, contextSupplier);
    } 
    if ("configurationProperties".equals(originalPropertySource.getName())) {
      log.debug("Not replacing property source {}", originalPropertySource.getName());
      return originalPropertySource;
    } 
    log.debug("Replacing property source {}", originalPropertySource.getName());
    return new ContextPropertySource(originalPropertySource, contextSupplier);
  }
}
