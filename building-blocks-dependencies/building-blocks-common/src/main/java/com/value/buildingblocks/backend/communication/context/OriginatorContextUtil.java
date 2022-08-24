package com.value.buildingblocks.backend.communication.context;

import com.value.buildingblocks.backend.internalrequest.InternalRequestContext;

public class OriginatorContextUtil {
  public OriginatorContext create(InternalRequestContext internalRequestContext) {
    OriginatorContext originatorContext = new OriginatorContext();
    if (internalRequestContext != null) {
      originatorContext.setAuthenticationStatus(internalRequestContext.getAuthStatus());
      originatorContext.setCreationTime(Long.valueOf(internalRequestContext.getRequestTime()));
      originatorContext.setSecurityContext(internalRequestContext.getUserToken());
      originatorContext.setRequestUuid(internalRequestContext.getRequestUuid());
      originatorContext.setUserAgent(internalRequestContext.getUserAgent());
      originatorContext.setLocation(internalRequestContext.getRemoteAddress());
    } 
    return originatorContext;
  }
}
