package com.value.buildingblocks.idempotency;

import org.springframework.cache.annotation.Cacheable;

public class RequestIdCacheImpl implements RequestIdCache {
  @Cacheable(cacheResolver = "idempotencyCacheResolver", key = "#requestId", sync = true)
  public String cacheRequestId(String requestId, CacheMissIndicator cacheMissedIndicator) {
    cacheMissedIndicator.setCacheMissed(true);
    return requestId;
  }
}
