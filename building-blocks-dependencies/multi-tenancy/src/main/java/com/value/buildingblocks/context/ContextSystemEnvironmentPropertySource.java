package com.value.buildingblocks.context;

import java.util.HashMap;
import java.util.Map;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SystemEnvironmentPropertySource;

public class ContextSystemEnvironmentPropertySource extends SystemEnvironmentPropertySource {
  private final ContextSupplier contextSupplier;
  
  private final ContextPropertySource<Map<String, Object>> contextPropertySource;
  
  private final Map<ContextQualifier, String[]> propertyNames = (Map)new HashMap<>();
  
  private SystemEnvironmentPropertySource systemEnvironmentPropertySource;
  
  public ContextSystemEnvironmentPropertySource(SystemEnvironmentPropertySource systemEnvironmentPropertySource, ContextSupplier contextSupplier) {
    super(systemEnvironmentPropertySource.getName(), (Map)systemEnvironmentPropertySource.getSource());
    this.contextSupplier = contextSupplier;
    this.systemEnvironmentPropertySource = systemEnvironmentPropertySource;
    this.contextPropertySource = new ContextPropertySource<>((PropertySource<Map<String, Object>>)systemEnvironmentPropertySource, contextSupplier);
  }
  
  public boolean containsProperty(String name) {
    return (getProperty(name) != null);
  }
  
  public Object getProperty(String propertyName) {
    return this.contextPropertySource.getProperty(propertyName);
  }
  
  public String[] getPropertyNames() {
    return this.propertyNames.computeIfAbsent(this.contextSupplier.getContext(), c -> ContextSupportUtil.deContextualisePropertyNames(c, this.systemEnvironmentPropertySource.getPropertyNames()));
  }
  
  public Map<String, Object> getSource() {
    return (Map<String, Object>)this.systemEnvironmentPropertySource.getSource();
  }
  
  public String toString() {
    return this.systemEnvironmentPropertySource.toString();
  }
  
  public boolean equals(Object obj) {
    return this.systemEnvironmentPropertySource.equals(obj);
  }
  
  public int hashCode() {
    return this.systemEnvironmentPropertySource.hashCode();
  }
}
