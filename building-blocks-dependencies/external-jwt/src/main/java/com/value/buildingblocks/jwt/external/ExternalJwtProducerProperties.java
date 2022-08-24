package com.value.buildingblocks.jwt.external;

import com.value.buildingblocks.jwt.core.util.JsonWebTokenUtils;
import java.util.Optional;
import org.springframework.context.ApplicationContext;

public class ExternalJwtProducerProperties extends ExternalJwtProperties {
  public static final String EXTERNAL_JWT_PRODUCER_NAMESPACE = "sso.jwt.external.producer";
  
  public static Optional<ExternalJwtProducerProperties> create(ApplicationContext applicationContext) {
    ExternalJwtProducerProperties properties = new ExternalJwtProducerProperties();
    return JsonWebTokenUtils.bind(properties, applicationContext, "sso.jwt.external", "sso.jwt.external.producer");
  }
}
