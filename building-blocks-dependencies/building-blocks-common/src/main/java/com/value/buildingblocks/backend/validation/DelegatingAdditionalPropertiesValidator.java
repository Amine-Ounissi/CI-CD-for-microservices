package com.value.buildingblocks.backend.validation;

import com.value.buildingblocks.persistence.model.AdditionalPropertiesAware;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DelegatingAdditionalPropertiesValidator implements AdditionalPropertiesValidator {
  private static final Logger log = LoggerFactory.getLogger(DelegatingAdditionalPropertiesValidator.class);
  
  private static final String ADDITIONS_NODE_NAME = "additions";
  
  private static final String ADDITIONAL_PROPERTIES_UNEXPECTED = "{additionalproperties.invalid}";
  
  @Autowired(required = false)
  private ConstraintValidator<AdditionalProperties, AdditionalPropertiesAware> implementation;
  
  public void initialize(AdditionalProperties constraintAnnotation) {
    if (this.implementation == null) {
      log.info("No implementation of AdditionalPropertiesValidator available.");
    } else {
      this.implementation.initialize(constraintAnnotation);
    } 
  }
  
  public boolean isValid(AdditionalPropertiesAware value, ConstraintValidatorContext context) {
    if (this.implementation == null) {
      if (value.getAdditions() != null && value.getAdditions().size() > 0) {
        buildAdditionalPropertiesViolation("{additionalproperties.invalid}", context);
        return false;
      } 
      return true;
    } 
    return this.implementation.isValid(value, context);
  }
  
  protected ConstraintValidatorContext buildAdditionalPropertiesViolation(String template, ConstraintValidatorContext context) {
    context.disableDefaultConstraintViolation();
    ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext builder = context.buildConstraintViolationWithTemplate(template).addPropertyNode("additions");
    return builder.addConstraintViolation();
  }
  
  public ConstraintValidator<AdditionalProperties, AdditionalPropertiesAware> getImplementation() {
    return this.implementation;
  }
  
  public void setImplementation(ConstraintValidator<AdditionalProperties, AdditionalPropertiesAware> implementation) {
    this.implementation = implementation;
  }
}
