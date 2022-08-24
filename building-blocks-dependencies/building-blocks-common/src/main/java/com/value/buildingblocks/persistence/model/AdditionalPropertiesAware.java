package com.value.buildingblocks.persistence.model;

import com.value.buildingblocks.backend.validation.AdditionalProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@AdditionalProperties
public interface AdditionalPropertiesAware {
  @JsonProperty("additions")
  Map<String, String> getAdditions();
  
  @JsonProperty("additions")
  void setAdditions(Map<String, String> paramMap);
  
  @JsonIgnore
  default void setAddition(String name, String value) {
    getAdditions().put(name, value);
  }
  
  default AdditionalPropertiesAware withAddition(String name, String value) {
    getAdditions().put(name, value);
    return this;
  }
}
