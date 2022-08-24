package com.value.buildingblocks.backend.validation;

import com.value.buildingblocks.backend.validation.config.ApiExtensionConfig;
import com.value.buildingblocks.backend.validation.config.PropertyDefinition;
import com.value.buildingblocks.backend.validation.config.PropertySet;
import com.value.buildingblocks.persistence.model.AdditionalPropertiesAware;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AdditionalPropertiesUtil {
  private static final Logger log = LoggerFactory.getLogger(AdditionalPropertiesUtil.class);
  
  @Autowired
  private ApiExtensionConfig config;
  
  public PropertyDefinition getPropertyDefinition(AdditionalPropertiesAware additionalPropertiesAware, String propertyKey) {
    String className = additionalPropertiesAware.getClass().getName();
    return getPropertyDefinition(className, propertyKey);
  }
  
  public PropertyDefinition getPropertyDefinition(List<PropertyDefinition> propertyDefinitions, String propertyName) {
    for (PropertyDefinition propertyDefinition : propertyDefinitions) {
      if (propertyDefinition.getPropertyName().equals(propertyName))
        return propertyDefinition; 
    } 
    return null;
  }
  
  public PropertyDefinition getPropertyDefinition(PropertySet propertySet, String propertyName) {
    return getPropertyDefinition(propertySet.getProperties(), propertyName);
  }
  
  public PropertyDefinition getPropertyDefinition(String className, String propertyKey) {
    PropertySet propertySet = getPropertySet(className);
    return getPropertyDefinition(propertySet, propertyKey);
  }
  
  public PropertySet getPropertySet(String className) {
    String propertySetName = this.config.getClasses().get(className);
    PropertySet propertySet = this.config.getPropertySets().get(propertySetName);
    if (propertySet == null) {
      log.debug("No properties configured for {} so using blank", className);
      propertySet = new PropertySet();
    } 
    return propertySet;
  }
}
