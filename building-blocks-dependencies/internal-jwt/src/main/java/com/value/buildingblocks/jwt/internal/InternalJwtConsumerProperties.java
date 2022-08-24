package com.value.buildingblocks.jwt.internal;

import com.value.buildingblocks.jwt.core.util.JsonWebTokenUtils;
import java.util.Optional;
import org.springframework.context.ApplicationContext;

public class InternalJwtConsumerProperties extends InternalJwtProperties {
  public static final String INTERNAL_JWT_CONSUMER_PROPERTIES_NAMESPACE = "sso.jwt.internal.consumer";
  
  public static Optional<InternalJwtConsumerProperties> create(ApplicationContext applicationContext) {
    InternalJwtConsumerProperties properties = new InternalJwtConsumerProperties();
    return JsonWebTokenUtils.bind(properties, applicationContext, "sso.jwt.internal", "sso.jwt.internal.consumer");
  }
}
