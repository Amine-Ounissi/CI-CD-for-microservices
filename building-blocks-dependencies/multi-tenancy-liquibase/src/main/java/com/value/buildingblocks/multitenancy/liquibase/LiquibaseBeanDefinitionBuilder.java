package com.value.buildingblocks.multitenancy.liquibase;

import com.value.buildingblocks.multitenancy.MultiTenancyConfigurationProperties;
import com.value.buildingblocks.multitenancy.Tenant;
import com.value.buildingblocks.multitenancy.TenantContext;
import java.io.File;
import java.util.Map;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.core.io.ResourceLoader;

final class LiquibaseBeanDefinitionBuilder {
  private final BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(SpringLiquibase.class, () -> new MultiTenantSpringLiquibase())
    .setRole(2)
    .setScope("singleton");
  
  private Tenant tenant;
  
  private MultiTenancyConfigurationProperties.DatasourceProperties.Strategy strategy;
  
  private static LiquibaseBeanDefinitionBuilder instance() {
    return new LiquibaseBeanDefinitionBuilder();
  }
  
  static LiquibaseBeanDefinitionBuilder fromProperties(LiquibaseProperties liquibaseProperties) {
    return instance()
      .withChangeLog(liquibaseProperties.getChangeLog())
      .withChangeLogParameters(liquibaseProperties.getParameters())
      .withContexts(liquibaseProperties.getContexts())
      .withDatabaseChangeLogLockTable(liquibaseProperties.getDatabaseChangeLogLockTable())
      .withDatabaseChangeLogTable(liquibaseProperties.getDatabaseChangeLogTable())
      .withDefaultSchema(liquibaseProperties.getDefaultSchema())
      .withDropFirst(liquibaseProperties.isDropFirst())
      .withLabels(liquibaseProperties.getLabels())
      .withLiquibaseSchema(liquibaseProperties.getLiquibaseSchema())
      .withLiquibaseTablespace(liquibaseProperties.getLiquibaseTablespace())
      .withRollbackFile(liquibaseProperties.getRollbackFile())
      .withShouldRun(liquibaseProperties.isEnabled())
      .withTestRollbackOnUpdate(liquibaseProperties.isTestRollbackOnUpdate());
  }
  
  LiquibaseBeanDefinitionBuilder withChangeLog(String changeLog) {
    this.beanDefinitionBuilder.addPropertyValue("changeLog", changeLog);
    return this;
  }
  
  LiquibaseBeanDefinitionBuilder withChangeLogParameters(Map<String, String> parameters) {
    this.beanDefinitionBuilder.addPropertyValue("changeLogParameters", parameters);
    return this;
  }
  
  LiquibaseBeanDefinitionBuilder withContexts(String contexts) {
    this.beanDefinitionBuilder.addPropertyValue("contexts", contexts);
    return this;
  }
  
  LiquibaseBeanDefinitionBuilder withDatabaseChangeLogLockTable(String databaseChangeLogLockTable) {
    this.beanDefinitionBuilder.addPropertyValue("databaseChangeLogLockTable", databaseChangeLogLockTable);
    return this;
  }
  
  LiquibaseBeanDefinitionBuilder withDatabaseChangeLogTable(String databaseChangeLogTable) {
    this.beanDefinitionBuilder.addPropertyValue("databaseChangeLogTable", databaseChangeLogTable);
    return this;
  }
  
  LiquibaseBeanDefinitionBuilder withDataSourceReference(String beanName) {
    this.beanDefinitionBuilder.addPropertyReference("dataSource", beanName);
    return this;
  }
  
  LiquibaseBeanDefinitionBuilder withDefaultSchema(String defaultSchema) {
    this.beanDefinitionBuilder.addPropertyValue("defaultSchema", defaultSchema);
    return this;
  }
  
  LiquibaseBeanDefinitionBuilder withDropFirst(boolean dropFirst) {
    this.beanDefinitionBuilder.addPropertyValue("dropFirst", Boolean.valueOf(dropFirst));
    return this;
  }
  
  LiquibaseBeanDefinitionBuilder withLabels(String labels) {
    this.beanDefinitionBuilder.addPropertyValue("labels", labels);
    return this;
  }
  
  LiquibaseBeanDefinitionBuilder withLiquibaseSchema(String liquibaseSchema) {
    this.beanDefinitionBuilder.addPropertyValue("liquibaseSchema", liquibaseSchema);
    return this;
  }
  
  LiquibaseBeanDefinitionBuilder withLiquibaseTablespace(String liquibaseTablespace) {
    this.beanDefinitionBuilder.addPropertyValue("liquibaseTablespace", liquibaseTablespace);
    return this;
  }
  
  LiquibaseBeanDefinitionBuilder withResourceLoader(ResourceLoader resourceLoader) {
    this.beanDefinitionBuilder.addPropertyValue("resourceLoader", resourceLoader);
    return this;
  }
  
  LiquibaseBeanDefinitionBuilder withRollbackFile(File rollbackFile) {
    this.beanDefinitionBuilder.addPropertyValue("rollbackFile", rollbackFile);
    return this;
  }
  
  LiquibaseBeanDefinitionBuilder withShouldRun(boolean shouldRun) {
    this.beanDefinitionBuilder.addPropertyValue("shouldRun", Boolean.valueOf(shouldRun));
    return this;
  }
  
  LiquibaseBeanDefinitionBuilder withTestRollbackOnUpdate(boolean testRollbackOnUpdate) {
    this.beanDefinitionBuilder.addPropertyValue("testRollbackOnUpdate", Boolean.valueOf(testRollbackOnUpdate));
    return this;
  }
  
  LiquibaseBeanDefinitionBuilder withTenant(Tenant tenant) {
    this.tenant = tenant;
    this.beanDefinitionBuilder.addPropertyValue("tenant", tenant);
    return this;
  }
  
  LiquibaseBeanDefinitionBuilder withStrategy(MultiTenancyConfigurationProperties.DatasourceProperties.Strategy strategy) {
    this.strategy = strategy;
    return this;
  }
  
  LiquibaseBeanDefinitionBuilder withProperties(Map<String, Object> properties) {
    properties.forEach(this.beanDefinitionBuilder::addPropertyValue);
    return this;
  }
  
  AbstractBeanDefinition getBeanDefinition() {
    if (this.strategy == null)
      throw new BeanDefinitionValidationException("Datasource strategy is not specified"); 
    if (this.tenant == null)
      throw new BeanDefinitionValidationException("Tenant is not specified"); 
    AbstractBeanDefinition beanDefinition = this.beanDefinitionBuilder.getBeanDefinition();
    beanDefinition.setSynthetic(true);
    return beanDefinition;
  }
  
  private static class MultiTenantSpringLiquibase extends SpringLiquibase {
    Tenant tenant;
    
    private MultiTenantSpringLiquibase() {}
    
    public Tenant getTenant() {
      return this.tenant;
    }
    
    public void setTenant(Tenant tenant) {
      this.tenant = tenant;
    }
    
    public void afterPropertiesSet() throws LiquibaseException {
      try {
        TenantContext.setTenant(this.tenant);
        super.afterPropertiesSet();
      } finally {
        TenantContext.clear();
      } 
    }
  }
}
