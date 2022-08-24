package com.value.buildingblocks.backend.communication.http;

public class QueryParameter {
  private final String key;
  
  private final Object value;
  
  public QueryParameter(String key, Object value) {
    this.key = key;
    this.value = value;
  }
  
  public String getKey() {
    return this.key;
  }
  
  public Object getValue() {
    return this.value;
  }
  
  public String toString() {
    return "QueryParameter [key=" + this.key + ", value=" + this.value + "]";
  }
  
  public int hashCode() {
    int prime = 31;
    int result = 1;
    result = 31 * result + ((this.key == null) ? 0 : this.key.hashCode());
    result = 31 * result + ((this.value == null) ? 0 : this.value.hashCode());
    return result;
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (obj == null)
      return false; 
    if (getClass() != obj.getClass())
      return false; 
    QueryParameter other = (QueryParameter)obj;
    if (this.key == null) {
      if (other.key != null)
        return false; 
    } else if (!this.key.equals(other.key)) {
      return false;
    } 
    if (this.value == null) {
      if (other.value != null)
        return false; 
    } else if (!this.value.equals(other.value)) {
      return false;
    } 
    return true;
  }
}
