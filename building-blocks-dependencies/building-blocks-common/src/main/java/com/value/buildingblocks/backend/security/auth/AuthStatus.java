package com.value.buildingblocks.backend.security.auth;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = DefaultAuthStatus.class)
public interface AuthStatus {
  AuthLevel getAuthLevel();
  
  String getMessage();
}
