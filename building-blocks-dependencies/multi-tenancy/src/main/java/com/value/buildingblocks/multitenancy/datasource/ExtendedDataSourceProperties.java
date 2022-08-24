package com.value.buildingblocks.multitenancy.datasource;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

public class ExtendedDataSourceProperties extends DataSourceProperties {
  private Map<String, String> tomcat = new HashMap<>();
  
  private Map<String, String> hikari = new HashMap<>();
  
  private Map<String, String> dbcp2 = new HashMap<>();
  
  public Map<String, String> getTomcat() {
    return this.tomcat;
  }
  
  public void setTomcat(Map<String, String> tomcat) {
    this.tomcat = tomcat;
  }
  
  public Map<String, String> getHikari() {
    return this.hikari;
  }
  
  public void setHikari(Map<String, String> hikari) {
    this.hikari = hikari;
  }
  
  public Map<String, String> getDbcp2() {
    return this.dbcp2;
  }
  
  public void setDbcp2(Map<String, String> dbcp2) {
    this.dbcp2 = dbcp2;
  }
  
  public static ExtendedDataSourceProperties from(DataSourceProperties datasource) {
    ExtendedDataSourceProperties target = new ExtendedDataSourceProperties();
    BeanUtils.copyProperties(datasource, target);
    return target;
  }
}
