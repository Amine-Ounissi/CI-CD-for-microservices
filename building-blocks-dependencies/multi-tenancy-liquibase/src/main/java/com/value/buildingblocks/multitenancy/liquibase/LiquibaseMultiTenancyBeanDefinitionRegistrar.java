package com.value.buildingblocks.multitenancy.liquibase;

import com.value.buildingblocks.multitenancy.MultiTenancyConfigurationProperties;
import com.value.buildingblocks.multitenancy.Tenant;
import com.value.buildingblocks.multitenancy.TenantProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

@Configuration
public class LiquibaseMultiTenancyBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, BeanFactoryAware, EnvironmentAware, ResourceLoaderAware {
  private static final Logger log = LoggerFactory.getLogger(LiquibaseMultiTenancyBeanDefinitionRegistrar.class);
  
  private BeanFactory beanFactory;
  
  private Environment environment;
  
  private ResourceLoader resourceLoader;
  
  private TenantProvider tenantProvider;
  
  public void setBeanFactory(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }
  
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }
  
  public void setResourceLoader(ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }
  
  public BeanFactory getBeanFactory() {
    return this.beanFactory;
  }
  
  public Environment getEnvironment() {
    return this.environment;
  }
  
  public ResourceLoader getResourceLoader() {
    return this.resourceLoader;
  }
  
  public TenantProvider getTenantProvider() {
    if (this.tenantProvider == null)
      this.tenantProvider = (TenantProvider)this.beanFactory.getBean(TenantProvider.class); 
    return this.tenantProvider;
  }
  
  public void setTenantProvider(TenantProvider tenantProvider) {
    this.tenantProvider = tenantProvider;
  }
  
  public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
    LiquibaseProperties liquibaseProperties = bindToConfigurationProperties(this.environment, "spring.liquibase", LiquibaseProperties.class);
    MultiTenancyConfigurationProperties.DatasourceProperties.Strategy strategy = resolveStrategy(this.environment);
    registerTenantLiquibaseBeans(registry, liquibaseProperties, strategy);
  }
  
  protected void registerTenantLiquibaseBeans(BeanDefinitionRegistry registry, LiquibaseProperties liquibaseProperties, MultiTenancyConfigurationProperties.DatasourceProperties.Strategy strategy) {
    for (Tenant tenant : getTenantProvider().getTenants())
      registerLiquibaseBeanDefinition(registry, liquibaseProperties, strategy, tenant); 
  }
  
  protected void registerLiquibaseBeanDefinition(BeanDefinitionRegistry registry, LiquibaseProperties liquibaseProperties, MultiTenancyConfigurationProperties.DatasourceProperties.Strategy strategy, Tenant tenant) {
    String beanName = getBeanNameForTenant(tenant);
    log.info("Registering Liquibase bean {} for {}", beanName, tenant);
    if (this.beanFactory.containsBean(beanName)) {
      log.info("SpringLiquibase bean definition already registered for tenant '{}'", beanName);
      return;
    } 
    AbstractBeanDefinition beanDefinition = LiquibaseBeanDefinitionBuilder.fromProperties(liquibaseProperties).withTenant(tenant).withStrategy(strategy).withDataSourceReference("dataSource").withResourceLoader(this.resourceLoader).withProperties(tenant.getLiquibase()).getBeanDefinition();
    registry.registerBeanDefinition(beanName, (BeanDefinition)beanDefinition);
  }
  
  protected String getBeanNameForTenant(Tenant tenant) {
    return "multitenancy.liquibase-" + tenant.getId();
  }
  
  protected <T> T bindToConfigurationProperties(Environment environment, String prefix, Class<T> configurationPropertiesClass) {
    try {
      return (T)Binder.get(environment).bindOrCreate(prefix, configurationPropertiesClass);
    } catch (Exception e) {
      throw new IllegalStateException("Cannot bind to " + configurationPropertiesClass, e);
    } 
  }
  
  protected MultiTenancyConfigurationProperties.DatasourceProperties.Strategy resolveStrategy(Environment environment) {
    return (MultiTenancyConfigurationProperties.DatasourceProperties.Strategy)environment.getRequiredProperty("value.multi-tenancy.datasource.strategy", MultiTenancyConfigurationProperties.DatasourceProperties.Strategy.class);
  }
}
