package com.value.buildingblocks.jwt.internal;

import com.value.buildingblocks.jwt.core.JsonWebTokenProducerType;
import com.value.buildingblocks.jwt.core.exception.JsonWebTokenException;
import com.value.buildingblocks.jwt.core.type.JsonWebTokenTypeFactory;
import com.value.buildingblocks.jwt.internal.exception.InternalJwtException;
import com.value.buildingblocks.jwt.internal.impl.InternalJwtProducerImpl;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InternalJwtProducerConfiguration {
  private static final String TOKEN_PRODUCER_TYPE_BEAN = "internalJsonWebTokenProducerType";
  
  @Autowired(required = false)
  private InternalJwtMapper tokenMapper;
  
  @Autowired(required = false)
  private InternalJwtTenantMapper tenantMapper;
  
  @Bean
  @RefreshScope
  public InternalJwtProducerProperties internalJwtProducerProperties(ApplicationContext applicationContext)
    throws Throwable {
    Optional<InternalJwtProducerProperties> properties = InternalJwtProducerProperties.create(applicationContext);
    return properties.<Throwable>orElseThrow(() -> new InternalJwtException("Can't bind external token producer properties from the application context"));
  }
  
  @Bean(name = {"internalJsonWebTokenProducerType"})
  @RefreshScope
  public JsonWebTokenProducerType internalJsonWebTokenProducerType(InternalJwtProducerProperties properties) throws JsonWebTokenException {
    return JsonWebTokenTypeFactory.getProducer(properties);
  }
  
  @Bean
  public InternalJwtProducer internalTokenProducer(@Qualifier("internalJsonWebTokenProducerType") JsonWebTokenProducerType jsonWebTokenProducer) {
    return new InternalJwtProducerImpl(jsonWebTokenProducer, this.tokenMapper, this.tenantMapper);
  }
}
