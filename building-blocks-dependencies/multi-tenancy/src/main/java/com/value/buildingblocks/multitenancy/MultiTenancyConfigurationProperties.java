package com.value.buildingblocks.multitenancy;

import com.value.buildingblocks.multitenancy.datasource.ExtendedDataSourceProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

@ConfigurationProperties("value.multi-tenancy")
public class MultiTenancyConfigurationProperties implements EnvironmentAware {
  public static final String DEFAULT_TID_HEADER = "X-TID";
  
  public static final String VALUE_MULTI_TENANCY_PREFIX = "value.multi-tenancy";
  
  private boolean enabled = false;
  
  private boolean httpHeaderStrategyEnabled = true;
  
  private boolean jwtClaimStrategyEnabled = true;
  
  private boolean serviceRequestStrategyEnabled = true;
  
  private String tenantIdHttpHeader = "X-TID";
  
  private final List<Tenant> tenants = new ArrayList<>();
  
  private List<String> headerPaths = new ArrayList<>();
  
  private DatasourceProperties datasource = new DatasourceProperties();
  
  private CachingProperties caching = new CachingProperties();
  
  private Environment environment;
  
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }
  
  @PostConstruct
  public void rebindTenantSpecificDataSources() {
    if (this.datasource.getStrategy() == DatasourceProperties.Strategy.SWITCH_DATASOURCE) {
      int tenantIndex = 0;
      for (Tenant tenant : this.tenants) {
        ExtendedDataSourceProperties defaultDataSourceProperties = new ExtendedDataSourceProperties();
        Binder binder = Binder.get(this.environment);
        Bindable bindable = Bindable.ofInstance(defaultDataSourceProperties);
        binder.bind("value.multi-tenancy.datasource.defaults", bindable);
        binder.bind("value.multi-tenancy.tenants[" + tenantIndex++ + "].datasource", bindable);
        tenant.setDatasource(defaultDataSourceProperties);
      } 
    } 
  }
  
  public boolean isEnabled() {
    return this.enabled;
  }
  
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
  
  public boolean isHttpHeaderStrategyEnabled() {
    return this.httpHeaderStrategyEnabled;
  }
  
  public void setHttpHeaderStrategyEnabled(boolean httpHeaderStrategyEnabled) {
    this.httpHeaderStrategyEnabled = httpHeaderStrategyEnabled;
  }
  
  public List<String> getHeaderPaths() {
    return this.headerPaths;
  }
  
  public void setHeaderPaths(List<String> headerPaths) {
    this.headerPaths = headerPaths;
  }
  
  public boolean isJwtClaimStrategyEnabled() {
    return this.jwtClaimStrategyEnabled;
  }
  
  public void setJwtClaimStrategyEnabled(boolean jwtClaimStrategyEnabled) {
    this.jwtClaimStrategyEnabled = jwtClaimStrategyEnabled;
  }
  
  public boolean isServiceRequestStrategyEnabled() {
    return this.serviceRequestStrategyEnabled;
  }
  
  public void setServiceRequestStrategyEnabled(boolean serviceRequestStrategyEnabled) {
    this.serviceRequestStrategyEnabled = serviceRequestStrategyEnabled;
  }
  
  public String getTenantIdHttpHeader() {
    return this.tenantIdHttpHeader;
  }
  
  public void setTenantIdHttpHeader(String tenantIdHttpHeader) {
    this.tenantIdHttpHeader = tenantIdHttpHeader;
  }
  
  public List<Tenant> getTenants() {
    return this.tenants;
  }
  
  public DatasourceProperties getDatasource() {
    return this.datasource;
  }
  
  public void setDatasource(DatasourceProperties datasource) {
    this.datasource = datasource;
  }
  
  public CachingProperties getCaching() {
    return this.caching;
  }
  
  public void setCaching(CachingProperties caching) {
    this.caching = caching;
  }
  
  public String toString() {
    return "MultiTenancyConfigurationProperties{enabled=" + this.enabled + ", httpHeaderStrategyEnabled=" + this.httpHeaderStrategyEnabled + ", jwtClaimStrategyEnabled=" + this.jwtClaimStrategyEnabled + ", serviceRequestStrategyEnabled=" + this.serviceRequestStrategyEnabled + ", tenantIdHttpHeader='" + this.tenantIdHttpHeader + '\'' + ", tenants=" + this.tenants + '}';
  }
  
  public static class DatasourceProperties {
    private Strategy strategy = Strategy.SWITCH_SCHEMA;
    
    private String defaultSchema;
    
    private String defaultCatalog;
    
    private String defaultTenantId;
    
    @NestedConfigurationProperty
    private ExtendedDataSourceProperties defaults;
    
    public Strategy getStrategy() {
      return this.strategy;
    }
    
    public void setStrategy(Strategy strategy) {
      this.strategy = strategy;
    }
    
    public String getDefaultSchema() {
      return this.defaultSchema;
    }
    
    public void setDefaultSchema(String defaultSchema) {
      this.defaultSchema = defaultSchema;
    }
    
    public String getDefaultCatalog() {
      return this.defaultCatalog;
    }
    
    public void setDefaultCatalog(String defaultCatalog) {
      this.defaultCatalog = defaultCatalog;
    }
    
    public String getDefaultTenantId() {
      return this.defaultTenantId;
    }
    
    public void setDefaultTenantId(String defaultTenantId) {
      this.defaultTenantId = defaultTenantId;
    }
    
    public ExtendedDataSourceProperties getDefaults() {
      return this.defaults;
    }
    
    @Deprecated
    public void setDefaults(DataSourceProperties defaults) {
      if (defaults instanceof ExtendedDataSourceProperties) {
        this.defaults = (ExtendedDataSourceProperties)defaults;
      } else {
        this.defaults = ExtendedDataSourceProperties.from(defaults);
      } 
    }
    
    public void setDefaults(ExtendedDataSourceProperties defaults) {
      this.defaults = defaults;
    }
    
    public enum Strategy {
      SWITCH_SCHEMA, SWITCH_CATALOG, SWITCH_DATASOURCE, NONE;
    }
  }
  
  public enum Strategy {
    SWITCH_SCHEMA, SWITCH_CATALOG, SWITCH_DATASOURCE, NONE;
  }
  
  public static class CachingProperties {
    private Map<String, Boolean> tenantAgnosticCacheResolvers = new HashMap<>();
    
    public Map<String, Boolean> getTenantAgnosticCacheResolvers() {
      return this.tenantAgnosticCacheResolvers;
    }
    
    public void setTenantAgnosticCacheResolvers(Map<String, Boolean> tenantAgnosticCacheResolvers) {
      this.tenantAgnosticCacheResolvers = tenantAgnosticCacheResolvers;
    }
  }
}
