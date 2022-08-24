package com.value.authentication.tokenconverter.service.api.listener.client.v2.users;

public class GetUsersInBulkQueryParameters {
  private String id;
  
  private String query;
  
  private Integer from;
  
  private String cursor;
  
  private Integer size;
  
  public String getId() {
    return this.id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public GetUsersInBulkQueryParameters withId(String id) {
    this.id = id;
    return this;
  }
  
  public String getQuery() {
    return this.query;
  }
  
  public void setQuery(String query) {
    this.query = query;
  }
  
  public GetUsersInBulkQueryParameters withQuery(String query) {
    this.query = query;
    return this;
  }
  
  public Integer getFrom() {
    return this.from;
  }
  
  public void setFrom(Integer from) {
    this.from = from;
  }
  
  public GetUsersInBulkQueryParameters withFrom(Integer from) {
    this.from = from;
    return this;
  }
  
  public String getCursor() {
    return this.cursor;
  }
  
  public void setCursor(String cursor) {
    this.cursor = cursor;
  }
  
  public GetUsersInBulkQueryParameters withCursor(String cursor) {
    this.cursor = cursor;
    return this;
  }
  
  public Integer getSize() {
    return this.size;
  }
  
  public void setSize(Integer size) {
    this.size = size;
  }
  
  public GetUsersInBulkQueryParameters withSize(Integer size) {
    this.size = size;
    return this;
  }
}
