package com.value.buildingblocks.jwt.internal;

import com.value.buildingblocks.jwt.core.JsonWebTokenConsumerType;
import com.value.buildingblocks.jwt.core.exception.JsonWebTokenException;
import com.value.buildingblocks.jwt.core.type.JsonWebTokenTypeFactory;
import com.value.buildingblocks.jwt.internal.exception.InternalJwtException;
import com.value.buildingblocks.jwt.internal.impl.InternalJwtConsumerImpl;
import java.util.Optional;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;

@Configuration
public class InternalJwtConsumerConfiguration {
  private static final Logger logger = LoggerFactory.getLogger(InternalJwtConsumerConfiguration.class);
  
  private static final String TOKEN_CONSUMER_TYPE_BEAN = "internalJsonWebTokenConsumerType";
  
  private static final String PROPERTY_SOURCE_NAME = "internalConsumerTokenPropertySource";
  
  @Autowired(required = false)
  private ApplicationContext applicationContext;
  
  @Bean
  @RefreshScope
  public InternalJwtConsumerProperties internalJwtConsumerProperties(ApplicationContext applicationContext)
    throws Throwable {
    Optional<InternalJwtConsumerProperties> properties = InternalJwtConsumerProperties.create(applicationContext);
    return properties.<Throwable>orElseThrow(() -> new InternalJwtException("Can't bind external token consumer properties from the application context"));
  }
  
  @Bean(name = {"internalJsonWebTokenConsumerType"})
  @RefreshScope
  public JsonWebTokenConsumerType internalJsonWebTokenConsumerType(InternalJwtConsumerProperties properties) throws JsonWebTokenException {
    return JsonWebTokenTypeFactory.getConsumer(properties);
  }
  
  @Bean
  public InternalJwtConsumer internalTokenConsumer(@Qualifier("internalJsonWebTokenConsumerType") JsonWebTokenConsumerType jsonWebTokenConsumerType) {
    return new InternalJwtConsumerImpl(jsonWebTokenConsumerType);
  }
  
  public void setProperties(Properties properties) {
    importPropertiesToEnvironment(properties);
  }
  
  private void importPropertiesToEnvironment(Properties importProperties) {
    if (importProperties == null)
      return; 
    if (this.applicationContext == null || this.applicationContext.getEnvironment() == null) {
      logger.warn("Can't load properties, Environment is not available");
      return;
    } 
    PropertiesPropertySource propertySource = new PropertiesPropertySource("internalConsumerTokenPropertySource", importProperties);
    ConfigurableEnvironment environment = (ConfigurableEnvironment)getEnvironment();
    if (environment != null) {
      environment.getPropertySources().addFirst((PropertySource)propertySource);
    } else {
      throw new IllegalStateException("Required value for Application get environment");
    } 
  }
  
  private Environment getEnvironment() {
    return (this.applicationContext != null) ? this.applicationContext.getEnvironment() : null;
  }
}
