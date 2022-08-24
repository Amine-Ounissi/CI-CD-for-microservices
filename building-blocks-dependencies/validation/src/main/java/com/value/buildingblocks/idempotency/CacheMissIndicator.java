package com.value.buildingblocks.idempotency;

public class CacheMissIndicator {
  private boolean cacheMissed = false;
  
  public final boolean isCacheMissed() {
    return this.cacheMissed;
  }
  
  public final void setCacheMissed(boolean cacheMissed) {
    this.cacheMissed = cacheMissed;
  }
}
