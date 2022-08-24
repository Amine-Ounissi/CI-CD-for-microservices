package com.value.authentication.tokenconverter.service.api.listener.client.v2.users;

public class GetUsersQueryParameters {
  private String entityId;
  
  private String query;
  
  private Integer from;
  
  private String cursor;
  
  private Integer size;
  
  public String getEntityId() {
    return this.entityId;
  }
  
  public void setEntityId(String entityId) {
    this.entityId = entityId;
  }
  
  public GetUsersQueryParameters withEntityId(String entityId) {
    this.entityId = entityId;
    return this;
  }
  
  public String getQuery() {
    return this.query;
  }
  
  public void setQuery(String query) {
    this.query = query;
  }
  
  public GetUsersQueryParameters withQuery(String query) {
    this.query = query;
    return this;
  }
  
  public Integer getFrom() {
    return this.from;
  }
  
  public void setFrom(Integer from) {
    this.from = from;
  }
  
  public GetUsersQueryParameters withFrom(Integer from) {
    this.from = from;
    return this;
  }
  
  public String getCursor() {
    return this.cursor;
  }
  
  public void setCursor(String cursor) {
    this.cursor = cursor;
  }
  
  public GetUsersQueryParameters withCursor(String cursor) {
    this.cursor = cursor;
    return this;
  }
  
  public Integer getSize() {
    return this.size;
  }
  
  public void setSize(Integer size) {
    this.size = size;
  }
  
  public GetUsersQueryParameters withSize(Integer size) {
    this.size = size;
    return this;
  }
}
