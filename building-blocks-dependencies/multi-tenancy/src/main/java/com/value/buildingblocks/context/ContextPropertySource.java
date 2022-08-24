package com.value.buildingblocks.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.PropertySource;

public class ContextPropertySource<T> extends PropertySource<T> {
  private static final Logger log = LoggerFactory.getLogger(ContextPropertySource.class);
  
  private final ContextSupplier contextSupplier;
  
  private final PropertySource<T> propertySource;
  
  public ContextPropertySource(PropertySource<T> propertySource, ContextSupplier contextSupplier) {
    super(propertySource.getName());
    this.propertySource = propertySource;
    this.contextSupplier = contextSupplier;
  }
  
  public String getName() {
    return this.propertySource.getName();
  }
  
  public Object getProperty(String propertyName) {
    Object value = getPropertyValue(propertyName);
    if (log.isTraceEnabled())
      log.trace("{} getProperty: {}={} from {}", new Object[] { this.contextSupplier.getContext(), propertyName, value, getName() }); 
    return value;
  }
  
  private Object getPropertyValue(String propertyName) {
    for (String qualifier : this.contextSupplier.getContext().getContext()) {
      Object value = this.propertySource.getProperty(propertyName + "@" + qualifier);
      if (value != null)
        return value; 
    } 
    return this.propertySource.getProperty(propertyName);
  }
  
  public T getSource() {
    return (T)this.propertySource.getSource();
  }
  
  public String toString() {
    return this.propertySource.toString();
  }
  
  public boolean equals(Object obj) {
    return this.propertySource.equals(obj);
  }
  
  public int hashCode() {
    return this.propertySource.hashCode();
  }
}
