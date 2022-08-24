package com.value.buildingblocks.multitenancy;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

public interface TenantIdentifierStrategy {
  Optional<String> identifyTenant(HttpServletRequest paramHttpServletRequest);
}
