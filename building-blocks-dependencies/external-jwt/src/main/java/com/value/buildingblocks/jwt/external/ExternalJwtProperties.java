package com.value.buildingblocks.jwt.external;

import com.value.buildingblocks.jwt.core.properties.JsonWebTokenInputTypeProperties;
import com.value.buildingblocks.jwt.core.properties.JsonWebTokenProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

abstract class ExternalJwtProperties extends JsonWebTokenProperties implements InitializingBean {
  private static final Logger logger = LoggerFactory.getLogger(ExternalJwtProperties.class);
  
  public static final String EXTERNAL_JWT_PROPERTY_NAMESPACE = "sso.jwt.external";
  
  private static final int DEFAULT_NOT_VALID_AFTER = 1800;
  
  private static final int DEFAULT_RENEW = 120;
  
  private int renew = 120;
  
  private int notValidAfter = 1800;
  
  private JsonWebTokenInputTypeProperties header = new JsonWebTokenInputTypeProperties("Authorization");
  
  private JsonWebTokenInputTypeProperties cookie = new JsonWebTokenInputTypeProperties("Authorization");
  
  private ExternalJwtRefreshProperties refresh = new ExternalJwtRefreshProperties();
  
  public ExternalJwtProperties() {
    setType("signedEncrypted");
  }
  
  public void afterPropertiesSet() {
    if (getExpiration() > getNotValidAfter()) {
      int configuredNotValidAfter = getNotValidAfter();
      setNotValidAfter(getExpiration());
      logger.warn("Not Valid After time is set same as expiration, because expiration time ({}) is greater that token Not Valid After time ({})", 

          
          Integer.valueOf(getExpiration()), Integer.valueOf(configuredNotValidAfter));
    } 
    if (getRenew() > getExpiration())
      logger.warn("Renew time ({}) is greater than the expiration time ({})", Integer.valueOf(getRenew()), Integer.valueOf(getExpiration())); 
  }
  
  public int getRenew() {
    return this.renew;
  }
  
  public void setRenew(int renew) {
    this.renew = renew;
  }
  
  public int getNotValidAfter() {
    return this.notValidAfter;
  }
  
  public void setNotValidAfter(int notValidAfter) {
    this.notValidAfter = notValidAfter;
  }
  
  public JsonWebTokenInputTypeProperties getHeader() {
    return this.header;
  }
  
  public void setHeader(JsonWebTokenInputTypeProperties header) {
    this.header = header;
  }
  
  public JsonWebTokenInputTypeProperties getCookie() {
    return this.cookie;
  }
  
  public void setCookie(JsonWebTokenInputTypeProperties cookie) {
    this.cookie = cookie;
  }
  
  public ExternalJwtRefreshProperties getRefresh() {
    return this.refresh;
  }
  
  public void setRefresh(ExternalJwtRefreshProperties refresh) {
    this.refresh = refresh;
  }
}
