package com.value.buildingblocks.jwt.external;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface ExternalJwtTenantMapper {
  default Optional<String> tenantId(Authentication authentication, HttpServletRequest request) {
    return Optional.empty();
  }
}
