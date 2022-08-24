package com.value.buildingblocks.resilience;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallableWrapper<V, T> implements Callable<V> {
  private static final Logger log = LoggerFactory.getLogger(CallableWrapper.class);
  
  private final Callable<V> callable;
  
  private final Runnable clearContext;
  
  private final T context;
  
  private final Supplier<T> getContext;
  
  private final Consumer<T> setContext;
  
  public CallableWrapper(Callable<V> callable, T context, Supplier<T> getContext, Consumer<T> setContext, Runnable clearContext) {
    this.callable = callable;
    this.context = context;
    this.getContext = getContext;
    this.setContext = setContext;
    this.clearContext = clearContext;
  }
  
  public V call() throws Exception {
    T originalContext = this.getContext.get();
    try {
      log.trace("Set context {}", this.context);
      this.setContext.accept(this.context);
      return this.callable.call();
    } finally {
      if (originalContext != null) {
        log.trace("Restore context {}", this.context);
        this.setContext.accept(originalContext);
      } else {
        log.trace("Clear context {}", this.context);
        this.clearContext.run();
      } 
    } 
  }
  
  public String toString() {
    return String.format("CallableWrapper [callable=%s, clearContext=%s, context=%s, getContext=%s, setContext=%s]", new Object[] { this.callable, this.clearContext, this.context, this.getContext, this.setContext });
  }
}
