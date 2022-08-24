package com.value.buildingblocks.persistence.configuration;

import javax.validation.Validator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
public class PersistenceConfiguration {
  @ConditionalOnProperty(prefix = "value.persistence", name = {"enabled"}, havingValue = "true", matchIfMissing = true)
  @PropertySource({"classpath:persistence.properties"})
  @Profile({"!mock"})
  @EnableJpaRepositories
  @EntityScan
  static class Enabled {}
  
  @Configuration
  @ConditionalOnProperty(prefix = "value.persistence", name = {"enabled"}, havingValue = "false", matchIfMissing = false)
  @Profile({"!mock"})
  @PropertySource({"classpath:no-persistence.properties"})
  static class Disabled {}
  
  @ConditionalOnProperty(prefix = "value.persistence", name = {"enabled"}, havingValue = "true", matchIfMissing = true)
  @Bean
  public HibernatePropertiesCustomizer hibernateValidationConfiguration(Validator validator) {
    return new HibernateValidationConfiguration(validator);
  }
}
