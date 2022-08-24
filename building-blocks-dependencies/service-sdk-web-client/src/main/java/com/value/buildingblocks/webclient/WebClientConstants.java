package com.value.buildingblocks.webclient;

public final class WebClientConstants {
  public static final String XSRF_TOKEN_COOKIE_NAME = "XSRF-TOKEN";
  
  public static final String XSRF_TOKEN_HEADER_NAME = "X-XSRF-TOKEN";
  
  public static final String INTER_SERVICE_WEB_CLIENT_NAME = "interServiceWebClient";
  
  public static final String DEFAULT_REGISTRATION_ID = "vds";
  
  public static final String ACCESS_PROVIDER_WEB_CLIENT_NAME = "accessProviderWebClient";
  
  public static final String INTERCEPTORS_ENABLED_HEADER = "X-Intercept-Errors";
  
  public static final String DEFAULT_SERVICE_SCOPE = "api:service";
  
  public static final String DEFAULT_CLIENT_ID = "vds-client";
  
  public static final String DEFALT_TOKEN_URI = "http://token-converter/oauth/token";
  
  public static final String DEFAULT_SECRET = "vds-secret";
  
  public static final int ORDER_OAUTH2_CUSTOMIZER = 10;
  
  public static final int ORDER_EXCHANGESTRATEGIES_CUSTOMIZER = 20;
  
  public static final int ORDER_COPYHEADER_CUSTOMIZER = 30;
  
  public static final int ORDER_CSRF_CUSTOMIZER = 40;
  
  public static final int ORDER_LOADBALANCING_CUSTOMIZER = 50;
  
  private WebClientConstants() {
    throw new AssertionError("No WebClientConstants instances for you!");
  }
}
