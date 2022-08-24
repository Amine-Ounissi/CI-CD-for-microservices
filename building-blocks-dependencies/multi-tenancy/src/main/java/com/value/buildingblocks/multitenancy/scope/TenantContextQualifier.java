package com.value.buildingblocks.multitenancy.scope;

import com.value.buildingblocks.context.ContextQualifier;

public class TenantContextQualifier implements ContextQualifier {
  private final String[] context;
  
  private final String key;
  
  private final String tid;
  
  public TenantContextQualifier(String tid) {
    this.tid = tid;
    this.key = String.format("tid(%s)", new Object[] { tid });
    this.context = new String[] { this.key };
  }
  
  public String[] getContext() {
    return this.context;
  }
  
  public String getKey() {
    return this.key;
  }
  
  public String getTid() {
    return this.tid;
  }
  
  public String toString() {
    return this.key;
  }
  
  public int hashCode() {
    return (this.key == null) ? 0 : this.key.hashCode();
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (obj == null)
      return false; 
    if (getClass() != obj.getClass())
      return false; 
    TenantContextQualifier other = (TenantContextQualifier)obj;
    if (this.key == null) {
      if (other.key != null)
        return false; 
    } else if (!this.key.equals(other.key)) {
      return false;
    } 
    return true;
  }
}
