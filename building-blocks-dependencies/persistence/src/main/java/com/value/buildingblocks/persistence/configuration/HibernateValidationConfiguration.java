package com.value.buildingblocks.persistence.configuration;

import java.util.Map;
import javax.validation.Validator;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;

public class HibernateValidationConfiguration implements HibernatePropertiesCustomizer {
  private final Validator validator;
  
  public HibernateValidationConfiguration(Validator validator) {
    this.validator = validator;
  }
  
  public void customize(Map<String, Object> hibernateProperties) {
    hibernateProperties.put("javax.persistence.validation.factory", this.validator);
  }
}
