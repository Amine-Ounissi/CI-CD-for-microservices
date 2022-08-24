package com.value.buildingblocks.jwt.internal;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface InternalJwtTenantMapper {
  default Optional<String> tenantId(Authentication authentication, HttpServletRequest request) {
    return Optional.empty();
  }
}
