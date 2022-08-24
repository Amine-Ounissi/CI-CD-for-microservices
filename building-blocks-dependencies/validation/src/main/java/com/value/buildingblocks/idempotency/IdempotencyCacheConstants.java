package com.value.buildingblocks.idempotency;

public final class IdempotencyCacheConstants {
  public static final String CACHE_RESOLVER_NAME = "idempotencyCacheResolver";
  
  private IdempotencyCacheConstants() {
    throw new AssertionError("Private constructor");
  }
}
