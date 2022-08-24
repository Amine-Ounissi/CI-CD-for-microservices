package com.value.buildingblocks.multitenancy.datasource;

import com.value.buildingblocks.multitenancy.DefaultTenantProvider;
import com.value.buildingblocks.multitenancy.MultiTenancyConfigurationProperties;
import com.value.buildingblocks.multitenancy.Tenant;
import com.value.buildingblocks.multitenancy.TenantContext;
import com.value.buildingblocks.multitenancy.TenantProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.CompositeHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.OrderedHealthAggregator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@Configuration
@EnableConfigurationProperties({MultiTenancyConfigurationProperties.class})
@AutoConfigureBefore({DataSourceAutoConfiguration.class})
@ConditionalOnProperty(name = {
  "value.multi-tenancy.enabled"}, havingValue = "true", matchIfMissing = false)
@PropertySource({"classpath:multi-tenancy.properties"})
public class MultiTenancyDataSourceAutoConfiguration implements EnvironmentAware {

  private static final Logger log = LoggerFactory
    .getLogger(MultiTenancyDataSourceAutoConfiguration.class);

  private ConfigurableEnvironment environment;

  public void setEnvironment(Environment environment) {
    this.environment = (ConfigurableEnvironment) environment;
  }

  @Bean(name = {"multiTenantDataSourceHealthIndicator"})
  @ConditionalOnBean(name = {"tenantDataSources"})
  public HealthIndicator multiTenantSwitchDataSourceHealthIndicator(
    @Qualifier("tenantDataSources") Map<Object, Object> tenantSpecificDataSources) {
    Map<String, HealthIndicator> indicators = (Map<String, HealthIndicator>) tenantSpecificDataSources
      .entrySet().stream().collect(Collectors.toMap(entry -> String.valueOf(entry.getKey()),
        entry -> (HealthIndicator) new DataSourceHealthIndicator((DataSource) entry.getValue())));
    OrderedHealthAggregator healthAggregator = new OrderedHealthAggregator();
    healthAggregator.setStatusOrder(new Status[]{Status.UNKNOWN});
    return (HealthIndicator) new CompositeHealthIndicator((HealthAggregator) healthAggregator,
      indicators);
  }

  @Bean
  @ConditionalOnProperty(name = {
    "value.multi-tenancy.datasource.strategy"}, havingValue = "SWITCH_CATALOG")
  @ConditionalOnMissingBean({TenantCatalogProvider.class})
  public TenantCatalogProvider tenantCatalogProvider() {
    return Tenant::getCatalog;
  }

  @Bean({"multiTenancyDataSourceBeanPostProcessor"})
  @ConditionalOnProperty(name = {
    "value.multi-tenancy.datasource.strategy"}, havingValue = "SWITCH_CATALOG")
  @ConditionalOnMissingBean({MultiTenancyDataSourceBeanPostProcessor.class})
  public MultiTenancyDataSourceBeanPostProcessor catalogSwitchingDataSourceBeanPostProcessor() {
    return new CatalogSwitchingDataSourceBeanPostProcessor();
  }

  @ConditionalOnMissingBean(name = {"tenantDataSources"})
  @Bean(name = {"multiTenantDataSourceHealthIndicator"})
  public HealthIndicator multiTenantSwitchCatalogOrSchemaDataSourceHealthIndicator(
    Optional<DataSource> dataSource, TenantProvider tenantProvider) {
    if (!dataSource.isPresent()) {
      return () -> Health.unknown().build();
    }
    Map<String, HealthIndicator> indicators = (Map<String, HealthIndicator>) tenantProvider
      .getTenants().stream().collect(
        Collectors.toMap(Tenant::getId,
          tenant -> (HealthIndicator) new DataSourceHealthIndicator(dataSource.get()) {
            protected void doHealthCheck(Health.Builder builder) throws Exception {
              try {
                TenantContext.setTenant(tenant);
                super.doHealthCheck(builder);
              } finally {
                TenantContext.clear();
              }
            }
          }));
    OrderedHealthAggregator healthAggregator = new OrderedHealthAggregator();
    healthAggregator.setStatusOrder(new Status[]{Status.UNKNOWN});
    return (HealthIndicator) new CompositeHealthIndicator((HealthAggregator) healthAggregator,
      indicators);
  }

  @Bean(name = {"multiTenancyRoutingDataSource", "dataSource"})
  @ConditionalOnProperty(name = {
    "value.multi-tenancy.datasource.strategy"}, havingValue = "SWITCH_DATASOURCE")
  @ConditionalOnMissingBean
  public DataSource multiTenancyRoutingDataSource(MultiTenancyConfigurationProperties properties,
    DataSourceProperties defaultDataSourceProperties,
    @Qualifier("tenantDataSources") Map<Object, Object> tenantSpecificDataSources) {
    MultiTenancyRoutingDataSource dataSource = new MultiTenancyRoutingDataSource();
    dataSource.setTargetDataSources(tenantSpecificDataSources);
    String defaultTenantId = properties.getDatasource().getDefaultTenantId();
    DataSource defaultDataSource = (DataSource) tenantSpecificDataSources.get(defaultTenantId);
    if (defaultDataSource != null) {
      dataSource.setDefaultTargetDataSource(defaultDataSource);
    } else if (StringUtils.isEmpty(defaultTenantId)) {
      try {
        defaultDataSource = defaultDataSourceProperties.initializeDataSourceBuilder().build();
        String implementation = getDataSourceImplementationPropertiesNamespace(defaultDataSource);
        if (implementation != null) {
          Binder binder = Binder.get((Environment) this.environment);
          binder
            .bind("spring.datasource." + implementation, Bindable.ofInstance(defaultDataSource));
        }
        dataSource.setDefaultTargetDataSource(defaultDataSource);
      } catch (Exception ex) {
        String message = "Error creating default DataSource";
        throw new BeanInstantiationException(MultiTenancyRoutingDataSource.class, message, ex);
      }
    } else {
      String message = "Default tenant ID '" + defaultTenantId
        + "\" does not exist in the list of configured tenants";
      throw new BeanInstantiationException(MultiTenancyRoutingDataSource.class, message);
    }
    return (DataSource) dataSource;
  }

  @Bean
  @ConditionalOnProperty(name = {
    "value.multi-tenancy.datasource.strategy"}, havingValue = "SWITCH_SCHEMA", matchIfMissing = true)
  @ConditionalOnMissingBean({TenantSchemaProvider.class})
  public TenantSchemaProvider tenantSchemaProvider() {
    return Tenant::getSchema;
  }

  @Bean({"multiTenancyDataSourceBeanPostProcessor"})
  @ConditionalOnProperty(name = {
    "value.multi-tenancy.datasource.strategy"}, havingValue = "SWITCH_SCHEMA", matchIfMissing = true)
  @ConditionalOnMissingBean({MultiTenancyDataSourceBeanPostProcessor.class})
  public MultiTenancyDataSourceBeanPostProcessor schemaSwitchingDataSourceBeanPostProcessor() {
    return new SchemaSwitchingDataSourceBeanPostProcessor();
  }

  @Deprecated
  public Map<Object, Object> createTenantSpecificDataSources(
    MultiTenancyConfigurationProperties properties) {
    return createTenantSpecificDataSources(new DefaultTenantProvider(properties.getTenants()));
  }

  @ConditionalOnProperty(name = {
    "value.multi-tenancy.datasource.strategy"}, havingValue = "SWITCH_DATASOURCE")
  @Bean({"tenantDataSources"})
  public Map<Object, Object> createTenantSpecificDataSources(TenantProvider tenants) {
    Map<Object, Object> datasourceMap = new HashMap<>();
    ExtendedDataSourceProperties defaultDataSourceProperties = new ExtendedDataSourceProperties();
    Binder binder = Binder.get((Environment) this.environment);
    binder.bind("value.multi-tenancy.datasource.defaults",
      Bindable.ofInstance(defaultDataSourceProperties));
    tenants.getTenants()
      .forEach(
        t -> datasourceMap.put(t.getId(), createTenantDataSource(t, defaultDataSourceProperties)));
    return datasourceMap;
  }

  private DataSource createTenantDataSource(Tenant tenant,
    ExtendedDataSourceProperties defaultDataSourceProperties) {
    ExtendedDataSourceProperties tenantDataSourceProperties = tenant.getDatasource();
    if (tenantDataSourceProperties == null) {
      throw new BeanCreationException(
        "No datasource configuration set for tenant " + tenant.getId());
    }
    try {
      log.debug("Creating DataSource for tenant {}", tenant);
      if (tenantDataSourceProperties.getType() == null) {
        tenantDataSourceProperties.setType(getConfiguredDatasourceTypeClass());
      }
      DataSource datasource = tenantDataSourceProperties.initializeDataSourceBuilder().build();
      Map<String, String> defaultImplementationProperties = getDataSourceImplementationProperties(
        datasource, defaultDataSourceProperties);
      Map<String, String> tenantImplementationProperties = getDataSourceImplementationProperties(
        datasource, tenantDataSourceProperties);
      log.debug("Custom DataSource type for tenant {}: {}", tenant.getId(),
        datasource.getClass().getName());
      initPoolSpecificProperties(datasource, defaultImplementationProperties,
        tenantImplementationProperties, tenant);
      return datasource;
    } catch (Exception ex) {
      throw new BeanCreationException("Error creating datasource for tenant " + tenant.getId(), ex);
    }
  }

  private String getDataSourceImplementationPropertiesNamespace(DataSource datasource) {
    switch (datasource.getClass().getName()) {
      case "org.apache.tomcat.jdbc.pool.DataSource":
        return "tomcat";
      case "com.zaxxer.hikari.HikariDataSource":
        return "hikari";
      case "org.apache.commons.dbcp2.BasicDataSource":
        return "dbcp2";
    }
    return null;
  }

  private Map<String, String> getDataSourceImplementationProperties(DataSource datasource,
    ExtendedDataSourceProperties dataSourceProperties) {
    switch (datasource.getClass().getName()) {
      case "org.apache.tomcat.jdbc.pool.DataSource":
        return dataSourceProperties.getTomcat();
      case "com.zaxxer.hikari.HikariDataSource":
        return dataSourceProperties.getHikari();
      case "org.apache.commons.dbcp2.BasicDataSource":
        return dataSourceProperties.getDbcp2();
    }
    return Collections.emptyMap();
  }

  private Class getConfiguredDatasourceTypeClass() {
    String springDataSourceTypeClass = this.environment.getProperty("spring.datasource.type");
    if (!StringUtils.isEmpty(springDataSourceTypeClass)) {
      try {
        return Class.forName(springDataSourceTypeClass);
      } catch (ClassNotFoundException e) {
        log.warn("Configured property spring.datasource.type class '{}' could not be found",
          springDataSourceTypeClass);
      }
    }
    return null;
  }

  private void initPoolSpecificProperties(DataSource dataSource,
    Map<String, String> defaultProperties, Map<String, String> tenantProperties, Tenant tenant) {
    log.debug("Setting {} DataSource properties for tenant[{}]", tenantProperties, tenant);
    Bindable<DataSource> bindable = Bindable.ofInstance(dataSource);
    MapConfigurationPropertySource defaultSource = new MapConfigurationPropertySource(
      defaultProperties);
    MapConfigurationPropertySource mapConfigurationPropertySource1 = new MapConfigurationPropertySource(
      tenantProperties);
    Binder tenantBinder = new Binder(new ConfigurationPropertySource[]{
      (ConfigurationPropertySource) mapConfigurationPropertySource1,
      (ConfigurationPropertySource) defaultSource});
    tenantBinder.bind(ConfigurationPropertyName.EMPTY, bindable);
  }
}
