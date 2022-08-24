package com.value.buildingblocks.idempotency;

public interface RequestIdCache {
  String cacheRequestId(String paramString, CacheMissIndicator paramCacheMissIndicator);
}
