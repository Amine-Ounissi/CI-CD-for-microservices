package com.value.buildingblocks.backend.communication.http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.apache.http.client.config.RequestConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.oauth2.common.AuthenticationScheme;

@ConfigurationProperties("value.communication.http")
public class HttpCommunicationProperties {
  @NotNull
  @Pattern(regexp = "https?")
  private String defaultScheme = "http";
  
  private boolean verifyCertificateHostnames = true;
  
  private boolean discoverableAccessTokenService = true;
  
  private String accessTokenUri = "http://token-converter/oauth/token";
  
  private String clientId = "vds-client";
  
  private AuthenticationScheme clientAuthenticationScheme = AuthenticationScheme.form;
  
  private List<String> clientScope = new ArrayList<>(Collections.singletonList("api:service"));
  
  private boolean autowireObjectMapper = false;
  
  private final RequestConfig.Builder requestConfig = RequestConfig.custom()
    .setConnectionRequestTimeout(10000)
    .setConnectTimeout(10000)
    .setSocketTimeout(30000);
  
  private final InterServiceHttpClientProperties client = new InterServiceHttpClientProperties();
  
  @PostConstruct
  private void initialiseClientDefaults() {
    this.client.initialiseDefaults();
  }
  
  public InterServiceHttpClientProperties getClient() {
    return this.client;
  }
  
  public RequestConfig.Builder getRequestConfig() {
    return this.requestConfig;
  }
  
  public String getDefaultScheme() {
    return this.defaultScheme;
  }
  
  public void setDefaultScheme(String defaultScheme) {
    this.defaultScheme = defaultScheme;
  }
  
  public boolean isVerifyCertificateHostnames() {
    return this.verifyCertificateHostnames;
  }
  
  public void setVerifyCertificateHostnames(boolean verifyCertificateHostnames) {
    this.verifyCertificateHostnames = verifyCertificateHostnames;
  }
  
  public boolean isDiscoverableAccessTokenService() {
    return this.discoverableAccessTokenService;
  }
  
  public void setDiscoverableAccessTokenService(boolean discoverableAccessTokenService) {
    this.discoverableAccessTokenService = discoverableAccessTokenService;
  }
  
  public String getAccessTokenUri() {
    return this.accessTokenUri;
  }
  
  public void setAccessTokenUri(String accessTokenUri) {
    this.accessTokenUri = accessTokenUri;
  }
  
  public String getClientId() {
    return this.clientId;
  }
  
  public void setClientId(String clientId) {
    this.clientId = clientId;
  }
  
  public AuthenticationScheme getClientAuthenticationScheme() {
    return this.clientAuthenticationScheme;
  }
  
  public void setClientAuthenticationScheme(AuthenticationScheme clientAuthenticationScheme) {
    this.clientAuthenticationScheme = clientAuthenticationScheme;
  }
  
  public List<String> getClientScope() {
    return this.clientScope;
  }
  
  public void setClientScope(List<String> clientScope) {
    this.clientScope = clientScope;
  }
  
  public boolean isAutowireObjectMapper() {
    return this.autowireObjectMapper;
  }
  
  public void setAutowireObjectMapper(boolean autowireObjectMapper) {
    this.autowireObjectMapper = autowireObjectMapper;
  }
  
  public static class InterServiceHttpClientProperties {
    private static final int DEFAULT_MAX_CONNECTION_TOTAL = 200;
    
    private static final int DEFAULT_MAX_CONNECTION_PER_ROUTE = 100;
    
    private static final List<String> DEFAULT_BLACKLISTED_HEADERS = Arrays.asList(new String[] { 
          "cookie", "x-cxt-", "x-forwarded-", "x-b3-", "accept", "authorization", "b3", "user-agent", "cache-control", "host", 
          "x-xsrf-token", "X-Intercept-Errors" });
    
    private String userAgent;
    
    private boolean defaultUserAgentDisabled = false;
    
    private int maxConnTotal = 0;
    
    private int maxConnPerRoute = 0;
    
    private long connTimeToLive = -1L;
    
    private boolean evictExpiredConnections;
    
    private boolean evictIdleConnections;
    
    private long maxIdleTime;
    
    private boolean redirectHandlingDisabled = false;
    
    private boolean contentCompressionDisabled = false;
    
    private boolean automaticRetriesDisabled = false;
    
    private boolean useSystemProperties = true;
    
    private boolean propagateHeaders = true;
    
    private List<String> blacklistedHeaders = DEFAULT_BLACKLISTED_HEADERS;
    
    public void initialiseDefaults() {
      if (isUseSystemProperties()) {
        if (System.getProperty("http.maxConnections") == null) {
          this.maxConnPerRoute = 100;
          this.maxConnTotal = 200;
        } 
      } else {
        if (this.maxConnTotal == 0)
          this.maxConnTotal = 200; 
        if (this.maxConnPerRoute == 0)
          this.maxConnPerRoute = 100; 
      } 
    }
    
    public String getUserAgent() {
      return this.userAgent;
    }
    
    public void setUserAgent(String userAgent) {
      this.userAgent = userAgent;
    }
    
    public int getMaxConnPerRoute() {
      return this.maxConnPerRoute;
    }
    
    public void setMaxConnPerRoute(int maxConnPerRoute) {
      this.maxConnPerRoute = maxConnPerRoute;
    }
    
    public boolean isEvictExpiredConnections() {
      return this.evictExpiredConnections;
    }
    
    public void setEvictExpiredConnections(boolean evictExpiredConnections) {
      this.evictExpiredConnections = evictExpiredConnections;
    }
    
    public boolean isEvictIdleConnections() {
      return this.evictIdleConnections;
    }
    
    public void setEvictIdleConnections(boolean evictIdleConnections) {
      this.evictIdleConnections = evictIdleConnections;
    }
    
    public long getMaxIdleTime() {
      return this.maxIdleTime;
    }
    
    public void setMaxIdleTime(long maxIdleTime) {
      this.maxIdleTime = maxIdleTime;
    }
    
    public boolean isUseSystemProperties() {
      return this.useSystemProperties;
    }
    
    public void setUseSystemProperties(boolean useSystemProperties) {
      this.useSystemProperties = useSystemProperties;
    }
    
    public boolean isPropagateHeaders() {
      return this.propagateHeaders;
    }
    
    public void setPropagateHeaders(boolean propagateHeaders) {
      this.propagateHeaders = propagateHeaders;
    }
    
    public boolean isRedirectHandlingDisabled() {
      return this.redirectHandlingDisabled;
    }
    
    public void setRedirectHandlingDisabled(boolean redirectHandlingDisabled) {
      this.redirectHandlingDisabled = redirectHandlingDisabled;
    }
    
    public boolean isAutomaticRetriesDisabled() {
      return this.automaticRetriesDisabled;
    }
    
    public void setAutomaticRetriesDisabled(boolean automaticRetriesDisabled) {
      this.automaticRetriesDisabled = automaticRetriesDisabled;
    }
    
    public boolean isContentCompressionDisabled() {
      return this.contentCompressionDisabled;
    }
    
    public void setContentCompressionDisabled(boolean contentCompressionDisabled) {
      this.contentCompressionDisabled = contentCompressionDisabled;
    }
    
    public boolean isDefaultUserAgentDisabled() {
      return this.defaultUserAgentDisabled;
    }
    
    public void setDefaultUserAgentDisabled(boolean defaultUserAgentDisabled) {
      this.defaultUserAgentDisabled = defaultUserAgentDisabled;
    }
    
    public int getMaxConnTotal() {
      return this.maxConnTotal;
    }
    
    public void setMaxConnTotal(int maxConnTotal) {
      this.maxConnTotal = maxConnTotal;
    }
    
    public long getConnTimeToLive() {
      return this.connTimeToLive;
    }
    
    public void setConnTimeToLive(long connTimeToLive) {
      this.connTimeToLive = connTimeToLive;
    }
    
    public List<String> getBlacklistedHeaders() {
      return this.blacklistedHeaders;
    }
    
    public void setBlacklistedHeaders(List<String> blacklistedHeaders) {
      this.blacklistedHeaders = blacklistedHeaders;
    }
  }
}
