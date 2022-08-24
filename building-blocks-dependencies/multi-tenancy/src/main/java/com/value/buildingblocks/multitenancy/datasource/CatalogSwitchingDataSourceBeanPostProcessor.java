package com.value.buildingblocks.multitenancy.datasource;

import com.value.buildingblocks.multitenancy.MultiTenancyConfigurationProperties;
import javax.sql.DataSource;
import org.aopalliance.aop.Advice;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class CatalogSwitchingDataSourceBeanPostProcessor extends MultiTenancyDataSourceBeanPostProcessor {
  @Autowired
  private MultiTenancyConfigurationProperties properties;
  
  @Autowired
  private TenantCatalogProvider tenantCatalogProvider;
  
  public CatalogSwitchingDataSourceBeanPostProcessor() {}
  
  public CatalogSwitchingDataSourceBeanPostProcessor(MultiTenancyConfigurationProperties properties, TenantCatalogProvider tenantCatalogProvider) {
    this.properties = properties;
    this.tenantCatalogProvider = tenantCatalogProvider;
  }
  
  protected DataSource createTenantAwareDataSource(DataSource targetDataSource) {
    ProxyFactory proxyFactory = new ProxyFactory();
    proxyFactory.setProxyTargetClass(true);
    proxyFactory.setTargetClass(targetDataSource.getClass());
    proxyFactory.setTarget(targetDataSource);
    proxyFactory.addAdvice((Advice)new CatalogSwitchingMethodInterceptor(this.properties.getDatasource().getDefaultCatalog(), this.tenantCatalogProvider));
    return (DataSource)proxyFactory.getProxy();
  }
}
