package com.value.buildingblocks.backend.validation;

import com.value.buildingblocks.persistence.model.AdditionalPropertiesAware;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.Map;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class AdditionalPropertiesValidatorProxy implements ConstraintValidator<AdditionalProperties, Object> {
  private static final Logger log = LoggerFactory.getLogger(AdditionalPropertiesValidatorProxy.class);
  
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
  
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    AdditionalPropertiesAwareProxy proxy = new AdditionalPropertiesAwareProxy(value);
    if (this.implementation == null) {
      if (proxy.getAdditions() != null && proxy.getAdditions().size() > 0) {
        buildAdditionalPropertiesViolation("{additionalproperties.invalid}", context);
        return false;
      } 
      return true;
    } 
    return this.implementation.isValid(proxy, context);
  }
  
  protected ConstraintValidatorContext buildAdditionalPropertiesViolation(String template, ConstraintValidatorContext context) {
    context.disableDefaultConstraintViolation();
    ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext builder = context.buildConstraintViolationWithTemplate(template).addPropertyNode("additions");
    return builder.addConstraintViolation();
  }
  
  public static class AdditionalPropertiesAwareProxy implements AdditionalPropertiesAware {
    private final Object target;
    
    AdditionalPropertiesAwareProxy(Object target) {
      this.target = target;
    }
    
    public Map<String, String> getAdditions() {
      if (this.target == null)
        return null; 
      PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(this.target.getClass(), "additions");
      if (propertyDescriptor == null)
        throw new NullPointerException(
            String.format("Class %s has @AdditionalProperties but doesn't have the %s field", new Object[] { this.target.getClass(), "additions" })); 
      try {
        return (Map<String, String>)propertyDescriptor.getReadMethod().invoke(this.target, new Object[0]);
      } catch (ReflectiveOperationException e) {
        throw new IllegalStateException("Couldn't get additions field value of " + this.target
            .getClass(), e);
      } 
    }
    
    public void setAdditions(Map<String, String> additions) {
      throw new UnsupportedOperationException("Cannot change value from validator");
    }
    
    public String getName() {
      return this.target.getClass().getName();
    }
  }
  
  public ConstraintValidator<AdditionalProperties, AdditionalPropertiesAware> getImplementation() {
    return this.implementation;
  }
  
  public void setImplementation(ConstraintValidator<AdditionalProperties, AdditionalPropertiesAware> implementation) {
    this.implementation = implementation;
  }
}
