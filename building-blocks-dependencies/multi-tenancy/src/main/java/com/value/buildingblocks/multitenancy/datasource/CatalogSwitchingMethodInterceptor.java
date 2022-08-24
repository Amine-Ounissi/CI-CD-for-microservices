package com.value.buildingblocks.multitenancy.datasource;

import com.value.buildingblocks.multitenancy.Tenant;
import com.value.buildingblocks.multitenancy.TenantContext;
import java.sql.Connection;
import java.util.Optional;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class CatalogSwitchingMethodInterceptor implements MethodInterceptor {
  private static final Logger log = LoggerFactory.getLogger(CatalogSwitchingMethodInterceptor.class);
  
  private final String defaultCatalog;
  
  private final TenantCatalogProvider catalogProvider;
  
  public CatalogSwitchingMethodInterceptor(String defaultCatalog, TenantCatalogProvider catalogProvider) {
    this.defaultCatalog = defaultCatalog;
    this.catalogProvider = catalogProvider;
  }
  
  public Object invoke(MethodInvocation invocation) throws Throwable {
    if ("getConnection".equals(invocation.getMethod().getName())) {
      Optional<Tenant> tenant = TenantContext.getTenant();
      String catalog = tenant.<String>map(this::getCatalog).orElse(this.defaultCatalog);
      if (StringUtils.isEmpty(catalog))
        throw new IllegalStateException("No tenant bound to the current thread and no default catalog configured."); 
      Connection connection = (Connection)invocation.proceed();
      try {
        connection.setCatalog(catalog);
        return connection;
      } catch (Exception getConnectionException) {
        try {
          connection.close();
        } catch (Exception closeConnectionException) {
          log.warn("Connection cannot be returned to pool for tenant {} and catalog {}", new Object[] { tenant, catalog, closeConnectionException });
        } 
        log.warn("Connection cannot be retrieved from pool for tenant {} and catalog {}", tenant, catalog);
        throw getConnectionException;
      } 
    } 
    return invocation.proceed();
  }
  
  private String getCatalog(Tenant tenant) {
    String catalog = this.catalogProvider.getCatalogForTenant(tenant);
    if (StringUtils.isEmpty(catalog))
      throw new IllegalStateException("No catalog configured for tenant " + tenant.getId()); 
    return catalog;
  }
}
