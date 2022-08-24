package com.value.buildingblocks.multitenancy.datasource;

import com.value.buildingblocks.multitenancy.MultiTenancyConfigurationProperties;
import javax.sql.DataSource;
import org.aopalliance.aop.Advice;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SchemaSwitchingDataSourceBeanPostProcessor extends MultiTenancyDataSourceBeanPostProcessor {
  @Autowired
  private MultiTenancyConfigurationProperties properties;
  
  @Autowired
  private TenantSchemaProvider tenantSchemaProvider;
  
  public SchemaSwitchingDataSourceBeanPostProcessor() {}
  
  public SchemaSwitchingDataSourceBeanPostProcessor(MultiTenancyConfigurationProperties properties, TenantSchemaProvider tenantSchemaProvider) {
    this.properties = properties;
    this.tenantSchemaProvider = tenantSchemaProvider;
  }
  
  protected DataSource createTenantAwareDataSource(DataSource targetDataSource) {
    ProxyFactory proxyFactory = new ProxyFactory();
    proxyFactory.setProxyTargetClass(true);
    proxyFactory.setTargetClass(targetDataSource.getClass());
    proxyFactory.setTarget(targetDataSource);
    proxyFactory.addAdvice((Advice)new SchemaSwitchingMethodInterceptor(this.properties.getDatasource().getDefaultSchema(), this.tenantSchemaProvider));
    return (DataSource)proxyFactory.getProxy();
  }
}
