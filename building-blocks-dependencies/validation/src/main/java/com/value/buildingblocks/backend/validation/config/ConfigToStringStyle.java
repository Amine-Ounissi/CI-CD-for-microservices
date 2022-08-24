package com.value.buildingblocks.backend.validation.config;

import org.apache.commons.lang3.builder.ToStringStyle;

public class ConfigToStringStyle extends ToStringStyle {
  private static final long serialVersionUID = 1L;
  
  private static final ConfigToStringStyle instance = new ConfigToStringStyle();
  
  private ConfigToStringStyle() {
    setContentStart(System.lineSeparator() + "[");
    setFieldSeparator(System.lineSeparator() + "  ");
    setFieldSeparatorAtStart(true);
    setContentEnd(System.lineSeparator() + "]");
    setUseShortClassName(true);
    setUseIdentityHashCode(false);
    setUseFieldNames(true);
  }
  
  public static ConfigToStringStyle getInstance() {
    return instance;
  }
}
