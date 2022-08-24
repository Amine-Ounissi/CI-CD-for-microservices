package com.value.buildingblocks.multitenancy;

import com.value.buildingblocks.multitenancy.datasource.ExtendedDataSourceProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

public class Tenant {
  private String id;
  
  private String schema;
  
  private String catalog;
  
  @NestedConfigurationProperty
  private ExtendedDataSourceProperties datasource;
  
  private Map<String, Object> liquibase = new HashMap<>();
  
  public String getId() {
    return this.id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getSchema() {
    return this.schema;
  }
  
  public void setSchema(String schema) {
    this.schema = schema;
  }
  
  public String getCatalog() {
    return this.catalog;
  }
  
  public void setCatalog(String catalog) {
    this.catalog = catalog;
  }
  
  public ExtendedDataSourceProperties getDatasource() {
    return this.datasource;
  }
  
  public void setDatasource(ExtendedDataSourceProperties datasource) {
    this.datasource = datasource;
  }
  
  @Deprecated
  @JsonIgnore
  public void setDatasource(DataSourceProperties datasource) {
    if (datasource instanceof ExtendedDataSourceProperties) {
      setDatasource((ExtendedDataSourceProperties)datasource);
    } else {
      this.datasource = ExtendedDataSourceProperties.from(datasource);
    } 
  }
  
  public Map<String, Object> getLiquibase() {
    return this.liquibase;
  }
  
  public void setLiquibase(Map<String, Object> liquibase) {
    this.liquibase = liquibase;
  }
  
  public String toString() {
    return String.format("Tenant [id=%s, schema=%s, catalog=%s]", new Object[] { this.id, this.schema, this.catalog });
  }
  
  public int hashCode() {
    int prime = 31;
    int result = 1;
    result = 31 * result + ((this.id == null) ? 0 : this.id.hashCode());
    return result;
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (obj == null)
      return false; 
    if (getClass() != obj.getClass())
      return false; 
    Tenant other = (Tenant)obj;
    return Objects.equals(this.id, other.id);
  }
}
