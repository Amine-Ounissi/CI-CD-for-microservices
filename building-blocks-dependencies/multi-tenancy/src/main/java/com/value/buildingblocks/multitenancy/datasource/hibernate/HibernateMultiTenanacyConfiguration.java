package com.value.buildingblocks.multitenancy.datasource.hibernate;

import java.util.Map;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;

public class HibernateMultiTenanacyConfiguration implements HibernatePropertiesCustomizer {
  private MultiTenantConnectionProvider multiTenancyConnectionProvider;
  
  public HibernateMultiTenanacyConfiguration(MultiTenantConnectionProvider multiTenancyConnectionProvider) {
    this.multiTenancyConnectionProvider = multiTenancyConnectionProvider;
  }
  
  public void customize(Map<String, Object> hibernateProperties) {
    hibernateProperties.put("hibernate.multiTenancy", MultiTenancyStrategy.SCHEMA);
    hibernateProperties.put("hibernate.tenant_identifier_resolver", new CurrentTenantIdentifierResolverImpl());
    hibernateProperties.put("hibernate.multi_tenant_connection_provider", this.multiTenancyConnectionProvider);
  }
}
