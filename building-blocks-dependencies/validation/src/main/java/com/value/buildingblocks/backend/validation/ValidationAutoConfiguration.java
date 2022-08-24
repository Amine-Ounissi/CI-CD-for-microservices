package com.value.buildingblocks.backend.validation;

import com.value.buildingblocks.backend.validation.config.ApiExtensionConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ApiExtensionConfig.class})
class ValidationAutoConfiguration {
  @Bean
  public AdditionalPropertiesUtil additionalPropertiesUtil() {
    return new AdditionalPropertiesUtil();
  }
  
  @Bean
  @ConditionalOnMissingBean
  public AdditionalPropertiesValidator additionalPropertiesValidator() {
    return new AdditionalPropertiesValidatorImpl();
  }
  
  @Bean
  public IbanValidator ibanValidator() {
    return new IbanValidator();
  }
}
