package com.value.buildingblocks.access;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;

@ConfigurationProperties(prefix = "value.security")
public class ScopedAccessProperties {
  public static final String PREFIX_VALUE_SECURITY_ACCESS = "value.security";
  
  private LinkedHashMap<String, AccessRule> rules = new LinkedHashMap<>();
  
  private boolean rulesEnabled = false;
  
  public LinkedHashMap<String, AccessRule> getRules() {
    return this.rules;
  }
  
  public void setRules(LinkedHashMap<String, AccessRule> rules) {
    this.rules = rules;
  }
  
  public boolean isRulesEnabled() {
    return this.rulesEnabled;
  }
  
  public void setRulesEnabled(boolean rulesEnabled) {
    this.rulesEnabled = rulesEnabled;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
  }
  
  protected List<Map.Entry<String, AccessRule>> getSortedRules() {
    List<Map.Entry<String, AccessRule>> sortedRules = new ArrayList<>(getRules().entrySet());
    sortedRules.sort(Comparator.comparingInt(e -> ((AccessRule)e.getValue()).getOrder()));
    return sortedRules;
  }
  
  public static class AccessRule {
    private int order = 0;
    
    private String[] paths = new String[0];
    
    private HttpMethod[] methods;
    
    private String[] require = new String[0];
    
    private String[] deny = new String[0];
    
    private String expression;
    
    public String[] getPaths() {
      return this.paths;
    }
    
    public void setPaths(String... paths) {
      this.paths = paths;
    }
    
    public HttpMethod[] getMethods() {
      return this.methods;
    }
    
    public void setMethods(HttpMethod... methods) {
      this.methods = methods;
    }
    
    public String[] getRequire() {
      return this.require;
    }
    
    public void setRequire(String... require) {
      this.require = require;
    }
    
    public String[] getDeny() {
      return this.deny;
    }
    
    public void setDeny(String... deny) {
      this.deny = deny;
    }
    
    public String getExpression() {
      return this.expression;
    }
    
    public void setExpression(String expression) {
      this.expression = expression;
    }
    
    public int getOrder() {
      return this.order;
    }
    
    public void setOrder(int order) {
      this.order = order;
    }
    
    public String toString() {
      return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
  }
}
