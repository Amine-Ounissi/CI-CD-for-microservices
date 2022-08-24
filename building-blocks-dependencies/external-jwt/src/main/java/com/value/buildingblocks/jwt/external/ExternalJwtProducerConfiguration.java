package com.value.buildingblocks.jwt.external;

import com.value.buildingblocks.jwt.core.JsonWebTokenProducerType;
import com.value.buildingblocks.jwt.core.exception.JsonWebTokenException;
import com.value.buildingblocks.jwt.core.type.JsonWebTokenTypeFactory;
import com.value.buildingblocks.jwt.external.exception.ExternalJwtException;
import com.value.buildingblocks.jwt.external.impl.ExternalJwtProducerImpl;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExternalJwtProducerConfiguration {
  private static final String TOKEN_PRODUCER_TYPE_BEAN = "externalJsonWebTokenProducerType";
  
  @Autowired(required = false)
  private ExternalJwtMapper tokenMapper;
  
  @Autowired(required = false)
  private ExternalJwtTenantMapper tenantMapper;
  
  @Bean
  @RefreshScope
  public ExternalJwtProducerProperties externalJwtProducerProperties(ApplicationContext applicationContext)
    throws ExternalJwtException {
    Optional<ExternalJwtProducerProperties> properties = ExternalJwtProducerProperties.create(applicationContext);
    return properties.orElseThrow(() -> new ExternalJwtException("Can't bind external token producer properties from the application context"));
  }
  
  @Bean(name = {"externalJsonWebTokenProducerType"})
  @RefreshScope
  public JsonWebTokenProducerType externalJsonWebTokenProducerType(ExternalJwtProducerProperties externalJwtProducerProperties) throws JsonWebTokenException {
    return JsonWebTokenTypeFactory.getProducer(externalJwtProducerProperties);
  }
  
  @Bean
  public ExternalJwtProducer externalTokenProducer(@Qualifier("externalJsonWebTokenProducerType") JsonWebTokenProducerType jsonWebTokenProducer, ExternalJwtProducerProperties externalJwtProducerProperties) {
    return new ExternalJwtProducerImpl(jsonWebTokenProducer, externalJwtProducerProperties, this.tokenMapper, this.tenantMapper);
  }
}
