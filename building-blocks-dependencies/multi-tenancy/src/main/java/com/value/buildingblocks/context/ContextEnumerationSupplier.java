package com.value.buildingblocks.context;

import java.util.List;

@FunctionalInterface
public interface ContextEnumerationSupplier {
  List<ContextQualifier> getContexts();
}
