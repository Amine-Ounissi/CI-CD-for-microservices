package com.value.platform.edge.filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

@ConfigurationProperties("gateway.token.converter")
public class TokenConversionProperties {

  private String url = "http://token-converter/convert";

  private HttpMethod method = HttpMethod.GET;

  private List<String> ignore = new ArrayList<>(
    Arrays.asList("/**/auth*/login", "/login", "/api/**/public-api/**"));

  private DownstreamRequestHeader downstreamRequestHeader = new DownstreamRequestHeader();

  private Map<String, String> headers = new HashMap<>();

  private Set<String> omitHeaders = new HashSet<>(
    Arrays.asList("host", "Content-Length", "Content-Type", "Transfer-Encoding"));

  private String authorizationHeader = "Authorization";

  private List<String> downstreamHeaders = new ArrayList<>(
    Collections.singletonList("Authorization"));

  private List<String> upstreamHeaders = new ArrayList<>(Arrays.asList("Set-Cookie",
    "Upstream-Authorization"));

  private String upstreamHeaderPrefix = "Upstream-";

  private boolean copyUpstreamOnFailure = true;

  private Strict strict = new Strict();

  public TokenConversionProperties() {
    this.headers.put("VDS_AUTH", "true");
  }

  public Strict getStrict() {
    return this.strict;
  }

  public void setStrict(Strict strict) {
    this.strict = strict;
  }

  public String getUrl() {
    return this.url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public HttpMethod getMethod() {
    return this.method;
  }

  public void setMethod(HttpMethod method) {
    this.method = method;
  }

  public List<String> getIgnore() {
    return this.ignore;
  }

  public void setIgnore(List<String> ignore) {
    this.ignore = ignore;
  }

  public String getAuthorizationHeader() {
    return authorizationHeader;
  }

  public void setAuthorizationHeader(String authrizationHeader) {
    this.authorizationHeader = authrizationHeader;
  }

  public DownstreamRequestHeader getDownstreamRequestHeader() {
    return this.downstreamRequestHeader;
  }

  public void setDownstreamRequestHeader(DownstreamRequestHeader downstreamRequestHeader) {
    this.downstreamRequestHeader = downstreamRequestHeader;
  }

  public Map<String, String> getHeaders() {
    return this.headers;
  }

  public void setHeaders(Map<String, String> headers) {
    this.headers = headers;
  }

  public Set<String> getOmitHeaders() {
    return this.omitHeaders;
  }

  public void setOmitHeaders(Set<String> omitHeaders) {
    this.omitHeaders = omitHeaders;
  }

  public List<String> getDownstreamHeaders() {
    return this.downstreamHeaders;
  }

  public void setDownstreamHeaders(List<String> downstreamHeaders) {
    this.downstreamHeaders = downstreamHeaders;
  }

  public List<String> getUpstreamHeaders() {
    return this.upstreamHeaders;
  }

  public void setUpstreamHeaders(List<String> upstreamHeaders) {
    this.upstreamHeaders = upstreamHeaders;
  }

  public String getUpstreamHeaderPrefix() {
    return this.upstreamHeaderPrefix;
  }

  public void setUpstreamHeaderPrefix(String upstreamHeaderPrefix) {
    this.upstreamHeaderPrefix = upstreamHeaderPrefix;
  }

  public boolean isCopyUpstreamOnFailure() {
    return this.copyUpstreamOnFailure;
  }

  public void setCopyUpstreamOnFailure(boolean copyUpstreamOnFailure) {
    this.copyUpstreamOnFailure = copyUpstreamOnFailure;
  }

  public static class DownstreamRequestHeader {

    private String name = "X-ORIGINAL-URI";

    private boolean enabled = true;

    public String getName() {
      return this.name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public boolean isEnabled() {
      return this.enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }
  }

  public static class Strict {

    private static final int DEFAULT_STATUS = HttpStatus.UNAUTHORIZED.value();

    private boolean enabled = true;

    private int inaccessibleStatus = DEFAULT_STATUS;

    public boolean isEnabled() {
      return this.enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public int getInaccessibleStatus() {
      return this.inaccessibleStatus;
    }

    public void setInaccessibleStatus(int inaccessibleStatus) {
      this.inaccessibleStatus = inaccessibleStatus;
    }
  }
}
