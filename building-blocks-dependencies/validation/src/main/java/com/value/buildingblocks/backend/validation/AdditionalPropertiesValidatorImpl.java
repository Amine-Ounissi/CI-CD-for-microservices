package com.value.buildingblocks.backend.validation;

import com.value.buildingblocks.backend.validation.config.ApiExtensionConfig;
import com.value.buildingblocks.backend.validation.config.PropertyDefinition;
import com.value.buildingblocks.persistence.model.AdditionalPropertiesAware;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AdditionalPropertiesValidatorImpl implements AdditionalPropertiesValidator {
  private static final String ADDITIONS_NODE_NAME = "additions";
  
  public static final String ADDITIONAL_PROPERTIES_UNEXPECTED = "{additionalproperties.invalid}";
  
  public static final String ADDITIONALPROPERTIES_VALUE_LENGTH = "{additionalproperties.value.length}";
  
  public static final String ADDITIONALPROPERTIES_KEY_LENGTH = "{additionalproperties.key.length}";
  
  public static final String ADDITIONALPROPERTIES_KEY_BLANK = "{additionalproperties.key.blank}";
  
  public static final String ADDITIONALPROPERTIES_VALUE_TYPE = "{additionalproperties.value.type}";
  
  public static final String ADDITIONALPROPERTIES_ENTRY_SIZE = "{additionalproperties.entry.size}";
  
  public static final String ADDITIONALPROPERTIES_KEY_UNEXPECTED = "{additionalproperties.key.unexpected}";
  
  private static final Logger log = LoggerFactory.getLogger(AdditionalPropertiesValidatorImpl.class);
  
  @Autowired
  private AdditionalPropertiesUtil util;
  
  @Autowired
  private ApiExtensionConfig apiExtensionConfig;
  
  public void initialize(AdditionalProperties constraintAnnotation) {}
  
  public boolean isValid(AdditionalPropertiesAware value, ConstraintValidatorContext context) {
    if (hasNoAdditionalProperties(value))
      return true; 
    return (validateGlobalConstraints(context, value) && validateConfiguredProperties(context, value));
  }
  
  private boolean hasNoAdditionalProperties(AdditionalPropertiesAware value) {
    Map<String, String> additionalProperties = value.getAdditions();
    if (additionalProperties == null)
      return true; 
    Iterator<Map.Entry<String, String>> iterator = additionalProperties.entrySet().iterator();
    while (iterator.hasNext()) {
      String propertyValue = (String)((Map.Entry)iterator.next()).getValue();
      if (propertyValue == null || (propertyValue
        .isEmpty() && !this.apiExtensionConfig.isPersistEmptyStringValues()))
        iterator.remove(); 
    } 
    return additionalProperties.isEmpty();
  }
  
  protected boolean validateConfiguredProperties(ConstraintValidatorContext context, AdditionalPropertiesAware model) {
    Map<String, String> properties = model.getAdditions();
    List<PropertyDefinition> propertyDefinitions = this.util.getPropertySet(getModelName(model)).getProperties();
    boolean valid = true;
    for (Map.Entry<String, String> property : properties.entrySet()) {
      PropertyDefinition propertyDefinition = this.util.getPropertyDefinition(propertyDefinitions, property.getKey());
      log.trace("Validating \"{}\"=\"{}\" ({})", new Object[] { property.getKey(), property.getValue(), propertyDefinition });
      if (propertyDefinition == null) {
        buildAdditionalPropertiesViolation("{additionalproperties.key.unexpected}", context, property);
        valid = false;
        continue;
      } 
      switch (propertyDefinition.getType()) {
        case BOOLEAN:
        case DATE:
        case DATETIME:
        case DATETIMEONLY:
        case INTEGER:
        case NUMBER:
        case STRING:
        case TIMEONLY:
          continue;
      } 
      buildAdditionalPropertiesViolation("{additionalproperties.value.type}", context, property);
      valid = false;
    } 
    return valid;
  }
  
  private String getModelName(AdditionalPropertiesAware model) {
    if (model instanceof AdditionalPropertiesValidatorProxy.AdditionalPropertiesAwareProxy)
      return ((AdditionalPropertiesValidatorProxy.AdditionalPropertiesAwareProxy)model).getName(); 
    return model.getClass().getName();
  }
  
  protected ConstraintValidatorContext buildAdditionalPropertiesViolation(String template, ConstraintValidatorContext context, Map.Entry<String, String> property) {
    context.disableDefaultConstraintViolation();
    ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext builder = context.buildConstraintViolationWithTemplate(template).addPropertyNode("additions");
    if (property == null)
      return builder.addConstraintViolation(); 
    return builder.addPropertyNode(null).inIterable().atKey(property.getKey()).addConstraintViolation();
  }
  
  protected boolean validateGlobalConstraints(ConstraintValidatorContext context, AdditionalPropertiesAware model) {
    Map<String, String> properties = model.getAdditions();
    int propertyCount = properties.size();
    if (propertyCount > this.apiExtensionConfig.getMaxProperties()) {
      log.debug("Property Count {} exceeds max items of {}", Integer.valueOf(propertyCount), Integer.valueOf(this.apiExtensionConfig.getMaxProperties()));
      buildAdditionalPropertiesViolation("{additionalproperties.entry.size}", context, null);
      return false;
    } 
    boolean valid = true;
    for (Map.Entry<String, String> property : properties.entrySet()) {
      if (StringUtils.isBlank(property.getKey())) {
        buildAdditionalPropertiesViolation("{additionalproperties.key.blank}", context, property);
        valid = false;
      } 
      if (((String)property.getKey()).length() > this.apiExtensionConfig.getMaxKeyLength()) {
        log.debug("Key length {} exceeds max length of {}", Integer.valueOf(((String)property.getKey()).length()), Integer.valueOf(this.apiExtensionConfig.getMaxKeyLength()));
        buildAdditionalPropertiesViolation("{additionalproperties.key.length}", context, property);
        valid = false;
      } 
      String value = property.getValue();
      if (value != null && value.length() > this.apiExtensionConfig.getMaxValueLength()) {
        log.debug("Value length {} exceeds max length of {}", Integer.valueOf(value.length()), Integer.valueOf(this.apiExtensionConfig.getMaxValueLength()));
        buildAdditionalPropertiesViolation("{additionalproperties.value.length}", context, property);
        valid = false;
      } 
    } 
    return valid;
  }
}
