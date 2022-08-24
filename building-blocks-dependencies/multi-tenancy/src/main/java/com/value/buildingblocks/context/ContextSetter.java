package com.value.buildingblocks.context;

@FunctionalInterface
public interface ContextSetter {
  void setContext(ContextQualifier paramContextQualifier);
}
