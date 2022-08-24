package com.value.buildingblocks.jwt.internal;

import com.value.buildingblocks.jwt.core.util.JsonWebTokenUtils;
import java.util.Optional;
import org.springframework.context.ApplicationContext;

public class InternalJwtProducerProperties extends InternalJwtProperties {
  public static final String INTERNAL_JWT_PRODUCER_PROPERTIES_NAMESPACE = "sso.jwt.internal.producer";
  
  public static Optional<InternalJwtProducerProperties> create(ApplicationContext applicationContext) {
    InternalJwtProducerProperties properties = new InternalJwtProducerProperties();
    return JsonWebTokenUtils.bind(properties, applicationContext, "sso.jwt.internal", "sso.jwt.internal.producer");
  }
}
