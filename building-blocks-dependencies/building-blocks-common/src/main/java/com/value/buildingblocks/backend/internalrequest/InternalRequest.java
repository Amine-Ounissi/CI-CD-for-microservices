package com.value.buildingblocks.backend.internalrequest;

import javax.servlet.http.HttpServletRequest;

public class InternalRequest<T> {
  private T data;
  
  private InternalRequestContext internalRequestContext;
  
  public InternalRequest() {}
  
  public InternalRequest(T data, InternalRequestContext internalRequestContext) {
    this.data = data;
    this.internalRequestContext = internalRequestContext;
  }
  
  public InternalRequest(T data, HttpServletRequest httpServletRequest, String uuid) {
    this(data, DefaultInternalRequestContext.contextFrom(httpServletRequest, uuid));
  }
  
  public T getData() {
    return this.data;
  }
  
  public void setData(T data) {
    this.data = data;
  }
  
  public InternalRequestContext getInternalRequestContext() {
    return this.internalRequestContext;
  }
  
  public void setInternalRequestContext(InternalRequestContext internalRequestContext) {
    this.internalRequestContext = internalRequestContext;
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (o == null || getClass() != o.getClass())
      return false; 
    InternalRequest<?> request = (InternalRequest)o;
    if ((this.data != null) ? !this.data.equals(request.data) : (request.data != null))
      return false; 
    if ((this.internalRequestContext != null) ? !this.internalRequestContext.equals(request.internalRequestContext) : (request.internalRequestContext != null))
      return false;
    return true;
  }
  
  public int hashCode() {
    int result = (this.data != null) ? this.data.hashCode() : 0;
    result = 31 * result + ((this.internalRequestContext != null) ? this.internalRequestContext.hashCode() : 0);
    return result;
  }
  
  public String toString() {
    return String.format("InternalRequest{data=%s, internalRequestContext=%s}", new Object[] { this.data, this.internalRequestContext });
  }
}
