package com.value.buildingblocks.access;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.core.convert.converter.Converter;

public class AccessRuleConverter implements Converter<String, ScopedAccessProperties.AccessRule> {
  private static final Logger log = LoggerFactory.getLogger(AccessRuleConverter.class);
  
  private final ObjectMapper mapper = (new ObjectMapper())
    .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, new DeserializationFeature[] { DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES });
  
  public ScopedAccessProperties.AccessRule convert(String s) {
    try {
      Map<String, String> map = (Map<String, String>)this.mapper.readValue(s, Map.class);
      MapConfigurationPropertySource mapConfigurationPropertySource = new MapConfigurationPropertySource(map);
      Binder binder = new Binder(new ConfigurationPropertySource[] { (ConfigurationPropertySource)mapConfigurationPropertySource });
      return (ScopedAccessProperties.AccessRule)binder.bind("", ScopedAccessProperties.AccessRule.class).get();
    } catch (JsonProcessingException e) {
      log.error("Unable to convert: {} {}", e, s);
      throw new AccessRuleConversionException("Unable to convert: " + s, e);
    } 
  }
}
