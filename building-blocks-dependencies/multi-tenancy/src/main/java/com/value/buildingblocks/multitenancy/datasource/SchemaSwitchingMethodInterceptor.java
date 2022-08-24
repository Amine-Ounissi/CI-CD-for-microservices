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

public class SchemaSwitchingMethodInterceptor implements MethodInterceptor {
  private static final Logger log = LoggerFactory.getLogger(SchemaSwitchingMethodInterceptor.class);
  
  private final String defaultSchema;
  
  private final TenantSchemaProvider tenantSchemaProvider;
  
  public SchemaSwitchingMethodInterceptor(String defaultSchema, TenantSchemaProvider tenantSchemaProvider) {
    this.defaultSchema = defaultSchema;
    this.tenantSchemaProvider = tenantSchemaProvider;
  }
  
  public Object invoke(MethodInvocation invocation) throws Throwable {
    if ("getConnection".equals(invocation.getMethod().getName())) {
      Optional<Tenant> tenant = TenantContext.getTenant();
      String schema = tenant.<String>map(this::getSchema).orElse(this.defaultSchema);
      if (StringUtils.isEmpty(schema)) {
        String error = tenant.<String>map(t -> "No schema configured for tenant " + t.getId()).orElse("No tenant bound to the current thread and no default schema configured.");
        throw new IllegalStateException(error);
      } 
      Connection connection = (Connection)invocation.proceed();
      try {
        connection.setSchema(schema);
        return connection;
      } catch (Exception getConnectionException) {
        try {
          connection.close();
        } catch (Exception closeConnectionException) {
          log.warn("Connection cannot be returned to pool for tenant {} and schema {}", new Object[] { tenant, schema, closeConnectionException });
        } 
        log.warn("Connection cannot be retrieved from pool for tenant {} and schema {}", tenant, schema);
        throw getConnectionException;
      } 
    } 
    return invocation.proceed();
  }
  
  private String getSchema(Tenant tenant) {
    String schema = this.tenantSchemaProvider.getSchemaForTenant(tenant);
    if (StringUtils.isEmpty(schema))
      throw new IllegalStateException("No schema configured for tenant " + tenant.getId()); 
    return schema;
  }
}
