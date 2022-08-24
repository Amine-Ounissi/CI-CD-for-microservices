package com.value.buildingblocks.multitenancy.datasource.hibernate;

import javax.sql.DataSource;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;

class MultiTenancyConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {
  private final transient DataSource dataSource;
  
  public MultiTenancyConnectionProvider(DataSource dataSource) {
    this.dataSource = dataSource;
  }
  
  protected DataSource selectAnyDataSource() {
    return getDataSource();
  }
  
  protected DataSource selectDataSource(String tenantIdentifier) {
    return getDataSource();
  }
  
  private DataSource getDataSource() {
    return this.dataSource;
  }
}
