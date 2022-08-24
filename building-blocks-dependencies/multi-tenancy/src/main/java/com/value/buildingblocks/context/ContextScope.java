package com.value.buildingblocks.context;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.cloud.context.scope.GenericScope;

public class ContextScope implements Scope, DisposableBean {
  private static final Logger log = LoggerFactory.getLogger(ContextScope.class);
  
  private final Map<ContextQualifier, GenericScope> beans = new ConcurrentHashMap<>();
  
  private final ContextSupplier contextSupplier;
  
  public ContextScope(ContextSupplier contextSupplier) {
    this.contextSupplier = contextSupplier;
  }
  
  public Object get(String beanName, ObjectFactory<?> objectFactory) {
    ContextQualifier context = this.contextSupplier.getContext();
    log.debug("{} get:{}", context, beanName);
    try {
      GenericScope contextScope = this.beans.computeIfAbsent(context, v -> new GenericScope());
      return contextScope.get(beanName, objectFactory);
    } catch (Exception e) {
      log.warn("Failed to create a Context Scoped Bean {} for {}", beanName, context);
      throw new BeanCreationException(beanName, "Cannot create bean for context " + context, e);
    } 
  }
  
  public void destroy() {
    clearBeans();
  }
  
  public void clearBeans() {
    Collection<GenericScope> oldScopes = this.beans.values();
    this.beans.clear();
    oldScopes.forEach(GenericScope::destroy);
  }
  
  public Object remove(String name) {
    return null;
  }
  
  public void registerDestructionCallback(String name, Runnable callback) {}
  
  public Object resolveContextualObject(String key) {
    return null;
  }
  
  public String getConversationId() {
    return this.contextSupplier.getContext().getKey();
  }
}
