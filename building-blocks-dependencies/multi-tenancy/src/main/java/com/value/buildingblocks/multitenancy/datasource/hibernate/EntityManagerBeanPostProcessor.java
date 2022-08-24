package com.value.buildingblocks.multitenancy.datasource.hibernate;

import java.util.LinkedHashMap;
import java.util.Map;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Deprecated
class EntityManagerBeanPostProcessor implements BeanPostProcessor {
  private final MultiTenantConnectionProvider connectionProvider;
  
  public EntityManagerBeanPostProcessor(MultiTenantConnectionProvider connectionProvider) {
    this.connectionProvider = connectionProvider;
  }
  
  public Object postProcessBeforeInitialization(Object bean, String beanName) {
    if (bean instanceof LocalContainerEntityManagerFactoryBean) {
      Map<String, Object> hibernateProps = new LinkedHashMap<>();
      hibernateProps.put("hibernate.multiTenancy", MultiTenancyStrategy.SCHEMA);
      hibernateProps.put("hibernate.tenant_identifier_resolver", new CurrentTenantIdentifierResolverImpl());
      hibernateProps.put("hibernate.multi_tenant_connection_provider", this.connectionProvider);
      LocalContainerEntityManagerFactoryBean entityManager = (LocalContainerEntityManagerFactoryBean)bean;
      entityManager.getJpaPropertyMap().putAll(hibernateProps);
      return entityManager;
    } 
    return bean;
  }
  
  public Object postProcessAfterInitialization(Object bean, String beanName) {
    return bean;
  }
}
