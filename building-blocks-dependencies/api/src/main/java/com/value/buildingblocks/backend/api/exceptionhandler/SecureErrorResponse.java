package com.value.buildingblocks.backend.api.exceptionhandler;

import com.value.buildingblocks.backend.api.config.ApiProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureBefore({ErrorMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
public class SecureErrorResponse {

  private static final Logger log = LoggerFactory.getLogger(SecureErrorResponse.class);

  @Bean
  public DefaultErrorAttributes getErrorAttributes(ApiProperties apiProperties) {
    if (apiProperties.isDisableSecureExceptions()) {
      log.info("Secure exceptions are disabled");
      return new DefaultErrorAttributes(true);
    }
    log.info("Secure exceptions are enabled");
    return new SecureErrorAttributes(apiProperties);
  }
}
