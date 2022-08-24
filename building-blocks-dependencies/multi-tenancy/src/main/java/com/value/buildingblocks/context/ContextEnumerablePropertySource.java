package com.value.buildingblocks.context;

import java.util.HashMap;
import java.util.Map;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

public class ContextEnumerablePropertySource<T> extends EnumerablePropertySource<T> {
  private final ContextSupplier contextSupplier;
  
  private final EnumerablePropertySource<T> enumerablePropertySource;
  
  private final ContextPropertySource<T> contextPropertySource;
  
  private final Map<ContextQualifier, String[]> propertyNames = (Map)new HashMap<>();
  
  public ContextEnumerablePropertySource(EnumerablePropertySource<T> propertySource, ContextSupplier contextSupplier) {
    super(propertySource.getName());
    this.enumerablePropertySource = propertySource;
    this.contextPropertySource = new ContextPropertySource<>((PropertySource<T>)propertySource, contextSupplier);
    this.contextSupplier = contextSupplier;
  }
  
  public boolean containsProperty(String name) {
    return (getProperty(name) != null);
  }
  
  public Object getProperty(String propertyName) {
    return this.contextPropertySource.getProperty(propertyName);
  }
  
  public String[] getPropertyNames() {
    return this.propertyNames.computeIfAbsent(this.contextSupplier.getContext(), c -> ContextSupportUtil.deContextualisePropertyNames(c, this.enumerablePropertySource.getPropertyNames()));
  }
  
  public T getSource() {
    return (T)this.enumerablePropertySource.getSource();
  }
  
  public String toString() {
    return this.enumerablePropertySource.toString();
  }
  
  public boolean equals(Object obj) {
    return this.enumerablePropertySource.equals(obj);
  }
  
  public int hashCode() {
    return this.enumerablePropertySource.hashCode();
  }
}
