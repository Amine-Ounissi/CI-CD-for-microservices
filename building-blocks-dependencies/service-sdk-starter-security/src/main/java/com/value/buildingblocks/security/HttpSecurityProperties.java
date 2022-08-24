package com.value.buildingblocks.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("value.security.http")
class HttpSecurityProperties {
  private boolean enabled = true;
  
  private boolean adapterEnabled = true;
  
  private boolean errorConfigurerEnabled = true;
  
  private boolean statelessConfigurerEnabled = true;
  
  private boolean actuatorConfigurerEnabled = true;
  
  private boolean publicPathsConfigurerEnabled = true;
  
  private boolean authorizedConfigurerEnabled = true;
  
  private boolean originatingUserConfigurerEnabled = true;
  
  public boolean isEnabled() {
    return this.enabled;
  }
  
  public void setEnabled(boolean configurersEnabled) {
    this.enabled = configurersEnabled;
  }
  
  public boolean isAdapterEnabled() {
    return this.adapterEnabled;
  }
  
  public void setAdapterEnabled(boolean adapterEnabled) {
    this.adapterEnabled = adapterEnabled;
  }
  
  public boolean isErrorConfigurerEnabled() {
    return this.errorConfigurerEnabled;
  }
  
  public void setErrorConfigurerEnabled(boolean errorConfigurerEnabled) {
    this.errorConfigurerEnabled = errorConfigurerEnabled;
  }
  
  public boolean isStatelessConfigurerEnabled() {
    return this.statelessConfigurerEnabled;
  }
  
  public void setStatelessConfigurerEnabled(boolean statelessConfigurerEnable) {
    this.statelessConfigurerEnabled = statelessConfigurerEnable;
  }
  
  public boolean isActuatorConfigurerEnabled() {
    return this.actuatorConfigurerEnabled;
  }
  
  public void setActuatorConfigurerEnabled(boolean actuatorConfigurerEnabled) {
    this.actuatorConfigurerEnabled = actuatorConfigurerEnabled;
  }
  
  public boolean isPublicPathsConfigurerEnabled() {
    return this.publicPathsConfigurerEnabled;
  }
  
  public void setPublicPathsConfigurerEnabled(boolean publicPathsConfigurerEnabled) {
    this.publicPathsConfigurerEnabled = publicPathsConfigurerEnabled;
  }
  
  public boolean isAuthorizedConfigurerEnabled() {
    return this.authorizedConfigurerEnabled;
  }
  
  public void setAuthorizedConfigurerEnabled(boolean authorizedConfigurerEnabled) {
    this.authorizedConfigurerEnabled = authorizedConfigurerEnabled;
  }
  
  public boolean isOriginatingUserConfigurerEnabled() {
    return this.originatingUserConfigurerEnabled;
  }
  
  public void setOriginatingUserConfigurerEnabled(boolean originatingUserConfigurerEnabled) {
    this.originatingUserConfigurerEnabled = originatingUserConfigurerEnabled;
  }
}
