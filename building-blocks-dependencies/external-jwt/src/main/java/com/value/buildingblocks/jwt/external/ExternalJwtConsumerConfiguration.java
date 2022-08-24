package com.value.buildingblocks.jwt.external;

import com.value.buildingblocks.jwt.core.JsonWebTokenConsumerType;
import com.value.buildingblocks.jwt.core.exception.JsonWebTokenException;
import com.value.buildingblocks.jwt.core.type.JsonWebTokenTypeFactory;
import com.value.buildingblocks.jwt.external.exception.ExternalJwtException;
import com.value.buildingblocks.jwt.external.impl.ExternalJwtConsumerImpl;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExternalJwtConsumerConfiguration {
  private static final String TOKEN_CONSUMER_TYPE_BEAN = "externalJsonWebTokenConsumerType";
  
  private static final Logger logger = LoggerFactory.getLogger(ExternalJwtConsumerConfiguration.class);
  
  @Bean
  @RefreshScope
  public ExternalJwtConsumerProperties externalJwtConsumerProperties(ApplicationContext applicationContext)
    throws ExternalJwtException {
    Optional<ExternalJwtConsumerProperties> properties = ExternalJwtConsumerProperties.create(applicationContext);
    return properties.orElseThrow(() -> new ExternalJwtException("Can't bind external token consumer properties from the application context"));
  }
  
  @Bean(name = {"externalJsonWebTokenConsumerType"})
  @ConditionalOnProperty(prefix = "sso.jwt.external", value = {"consumer.enabled", "enabled"}, havingValue = "true", matchIfMissing = true)
  @RefreshScope
  public JsonWebTokenConsumerType externalJsonWebTokenConsumerType(ExternalJwtConsumerProperties externalJwtConsumerProperties) throws JsonWebTokenException {
    return JsonWebTokenTypeFactory.getConsumer(externalJwtConsumerProperties);
  }
  
  @Bean(name = {"externalJsonWebTokenConsumerType"})
  @ConditionalOnMissingBean(name = {"externalJsonWebTokenConsumerType"})
  @RefreshScope
  public JsonWebTokenConsumerType externalJsonWebTokenConsumerTypeDefault() {
    logger.debug("External JWT consumer is disabled");
    return null;
  }
  
  @Bean
  public ExternalJwtConsumer externalTokenConsumer(@Qualifier("externalJsonWebTokenConsumerType") JsonWebTokenConsumerType jsonWebTokenConsumerType) {
    return new ExternalJwtConsumerImpl(jsonWebTokenConsumerType);
  }
}
