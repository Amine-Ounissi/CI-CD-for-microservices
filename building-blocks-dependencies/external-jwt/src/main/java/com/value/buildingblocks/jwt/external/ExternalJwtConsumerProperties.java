package com.value.buildingblocks.jwt.external;

import com.value.buildingblocks.jwt.core.util.JsonWebTokenUtils;
import java.util.Optional;
import org.springframework.context.ApplicationContext;

public class ExternalJwtConsumerProperties extends ExternalJwtProperties {
  public static final String EXTERNAL_JWT_CONSUMER_NAMESPACE = "sso.jwt.external.consumer";
  
  public static Optional<ExternalJwtConsumerProperties> create(ApplicationContext applicationContext) {
    ExternalJwtConsumerProperties properties = new ExternalJwtConsumerProperties();
    return JsonWebTokenUtils.bind(properties, applicationContext, "sso.jwt.external", "sso.jwt.external.consumer");
  }
}
