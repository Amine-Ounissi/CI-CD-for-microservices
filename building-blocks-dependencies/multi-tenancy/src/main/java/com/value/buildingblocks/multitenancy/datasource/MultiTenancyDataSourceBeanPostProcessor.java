package com.value.buildingblocks.multitenancy.datasource;

import javax.sql.DataSource;
import org.springframework.beans.factory.config.BeanPostProcessor;

public abstract class MultiTenancyDataSourceBeanPostProcessor implements BeanPostProcessor {
  public Object postProcessBeforeInitialization(Object bean, String beanName) {
    return bean;
  }
  
  public Object postProcessAfterInitialization(Object bean, String beanName) {
    if (bean instanceof DataSource)
      bean = createTenantAwareDataSource((DataSource)bean); 
    return bean;
  }
  
  protected abstract DataSource createTenantAwareDataSource(DataSource paramDataSource);
}
