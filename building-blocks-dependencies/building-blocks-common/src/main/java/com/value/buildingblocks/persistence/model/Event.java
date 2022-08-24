package com.value.buildingblocks.persistence.model;

public class Event {
  private String aggregateId;
  
  private int version;
  
  @Deprecated
  public String getAggregateId() {
    return this.aggregateId;
  }
  
  @Deprecated
  public void setAggregateId(String aggregateId) {
    this.aggregateId = aggregateId;
  }
  
  @Deprecated
  public int getVersion() {
    return this.version;
  }
  
  @Deprecated
  public void setVersion(int version) {
    this.version = version;
  }
}
