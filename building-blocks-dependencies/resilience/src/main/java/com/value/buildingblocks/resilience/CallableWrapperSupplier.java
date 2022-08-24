package com.value.buildingblocks.resilience;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CallableWrapperSupplier<T> {
  private final Supplier<T> getContext;
  
  private final Consumer<T> setContext;
  
  private final Runnable clearContext;
  
  public CallableWrapperSupplier(Supplier<T> getContext, Consumer<T> setContext, Runnable clearContext) {
    this.getContext = getContext;
    this.setContext = setContext;
    this.clearContext = clearContext;
  }
  
  public <V> Callable<V> wrapCallable(Callable<V> callable) {
    return new CallableWrapper<>(callable, this.getContext.get(), this.getContext, this.setContext, this.clearContext);
  }
  
  public String toString() {
    return String.format("CallableWrapperSupplier [getContext=%s, setContext=%s, clearContext=%s]",
      this.getContext, this.setContext, this.clearContext);
  }
}
