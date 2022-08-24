package com.value.buildingblocks.backend.internalrequest;

import com.value.buildingblocks.backend.security.auth.AuthStatus;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(as = DefaultInternalRequestContext.class)
@JsonSerialize(as = InternalRequestContext.class)
public interface InternalRequestContext {
  String getUserToken();
  
  String getRemoteUser();
  
  String getRemoteAddress();
  
  long getRequestTime();
  
  String getUserAgent();
  
  String getChannelId();
  
  String getRequestUuid();
  
  AuthStatus getAuthStatus();
  
  void setAuthStatus(AuthStatus paramAuthStatus);
}
