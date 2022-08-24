package com.value.buildingblocks.persistence.context;

import com.value.buildingblocks.backend.communication.context.OriginatorContext;

public class OriginatorContextHolder {
  protected static final ThreadLocal<OriginatorContext> threadLocal = new ThreadLocal<>();
  
  protected static Class<? extends OriginatorContext> contextClass = OriginatorContext.class;
  
  public static OriginatorContext getCurrentContext() {
    return threadLocal.get();
  }
  
  public static void setCurrentContext(OriginatorContext context) {
    threadLocal.set(context);
  }
  
  public static void clear() {
    threadLocal.remove();
  }
}
