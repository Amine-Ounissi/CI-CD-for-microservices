package com.value.authentication.tokenconverter.service.api.listener.client.v2.users;

public class GetIdentitiesQueryParameters {
  private String externalId;
  
  private Integer from;
  
  private String cursor;
  
  private Integer size;
  
  public String getExternalId() {
    return this.externalId;
  }
  
  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }
  
  public GetIdentitiesQueryParameters withExternalId(String externalId) {
    this.externalId = externalId;
    return this;
  }
  
  public Integer getFrom() {
    return this.from;
  }
  
  public void setFrom(Integer from) {
    this.from = from;
  }
  
  public GetIdentitiesQueryParameters withFrom(Integer from) {
    this.from = from;
    return this;
  }
  
  public String getCursor() {
    return this.cursor;
  }
  
  public void setCursor(String cursor) {
    this.cursor = cursor;
  }
  
  public GetIdentitiesQueryParameters withCursor(String cursor) {
    this.cursor = cursor;
    return this;
  }
  
  public Integer getSize() {
    return this.size;
  }
  
  public void setSize(Integer size) {
    this.size = size;
  }
  
  public GetIdentitiesQueryParameters withSize(Integer size) {
    this.size = size;
    return this;
  }
}
