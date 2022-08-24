package com.value.buildingblocks.context;

@FunctionalInterface
public interface ContextSupplier {
  ContextQualifier getContext();
}
