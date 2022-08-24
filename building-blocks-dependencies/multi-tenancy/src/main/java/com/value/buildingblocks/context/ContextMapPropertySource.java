package com.value.buildingblocks.context;

import java.util.HashMap;
import java.util.Map;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

public class ContextMapPropertySource extends MapPropertySource {
  private final ContextSupplier contextSupplier;
  
  private final ContextPropertySource<Map<String, Object>> contextPropertySource;
  
  private final Map<ContextQualifier, String[]> propertyNames = (Map)new HashMap<>();
  
  private MapPropertySource mapPropertySource;
  
  public ContextMapPropertySource(MapPropertySource propertySource, ContextSupplier contextSupplier) {
    super(propertySource.getName(), (Map)propertySource.getSource());
    this.contextPropertySource = new ContextPropertySource<>((PropertySource<Map<String, Object>>)propertySource, contextSupplier);
    this.contextSupplier = contextSupplier;
    this.mapPropertySource = propertySource;
  }
  
  public boolean containsProperty(String name) {
    return (getProperty(name) != null);
  }
  
  public Object getProperty(String propertyName) {
    return this.contextPropertySource.getProperty(propertyName);
  }
  
  public String[] getPropertyNames() {
    return this.propertyNames.computeIfAbsent(this.contextSupplier.getContext(), c -> ContextSupportUtil.deContextualisePropertyNames(c, this.mapPropertySource.getPropertyNames()));
  }
  
  public Map<String, Object> getSource() {
    return (Map<String, Object>)this.mapPropertySource.getSource();
  }
  
  public String toString() {
    return this.mapPropertySource.toString();
  }
  
  public boolean equals(Object obj) {
    return this.mapPropertySource.equals(obj);
  }
  
  public int hashCode() {
    return this.mapPropertySource.hashCode();
  }
}
