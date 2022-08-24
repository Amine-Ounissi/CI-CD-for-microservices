package com.value.buildingblocks.jwt.external.token;

import com.value.buildingblocks.jwt.core.exception.JsonWebTokenException;
import com.value.buildingblocks.jwt.core.util.JsonWebTokenUtils;
import com.value.buildingblocks.jwt.external.ExternalJwtConsumer;
import com.value.buildingblocks.jwt.external.ExternalJwtProducer;
import com.value.buildingblocks.jwt.external.exception.ExternalJwtException;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;

public final class ExternalJwtHttpHeader {
  public static final String HEADER_NAME = "Authorization";
  
  public static final String HEADER_SCHEMA = "Bearer";
  
  private static final String HEADER_BASED_AUTHENTICATION_REQUEST_ATTRIBUTE = "external-jwt-token-is-header-based-authentication";
  
  private final ExternalJwt externalJwt;
  
  private final String headerName;
  
  private ExternalJwtHttpHeader(ExternalJwt externalJwt) {
    this(externalJwt, "Authorization");
  }
  
  private ExternalJwtHttpHeader(ExternalJwt externalJwt, String headerName) {
    this.externalJwt = externalJwt;
    this.headerName = headerName;
  }
  
  public static Builder create() {
    return new Builder();
  }
  
  public static Optional<ExternalJwt> load(ExternalJwtConsumer tokenConsumer, HttpServletRequest httpRequest, String headerName) throws ExternalJwtException, JsonWebTokenException {
    if (!JsonWebTokenUtils.isHeaderBasedAuth(headerName, httpRequest))
      return Optional.empty(); 
    Optional<String> header = JsonWebTokenUtils.getHeaderValue(headerName, httpRequest);
    if (!header.isPresent())
      throw new IllegalArgumentException("Expected header not found " + headerName); 
    String value = ((String)header.get()).substring("Bearer".length()).trim();
    setAuthenticationHeaderBased(httpRequest, true);
    return Optional.of(tokenConsumer.parseToken(value));
  }
  
  public static boolean isAuthenticationHeaderBased(HttpServletRequest request) {
    Object attribute = request.getAttribute("external-jwt-token-is-header-based-authentication");
    if (!(attribute instanceof Boolean))
      return false; 
    return ((Boolean)attribute).booleanValue();
  }
  
  private static void setAuthenticationHeaderBased(HttpServletRequest request, boolean isHeaderBased) {
    request.setAttribute("external-jwt-token-is-header-based-authentication", Boolean.valueOf(isHeaderBased));
  }
  
  public String getHeaderName() {
    return this.headerName;
  }
  
  public String getHeaderValue() {
    return "Bearer " + this.externalJwt.getSerializedToken();
  }
  
  public ExternalJwt getToken() {
    return this.externalJwt;
  }
  
  public static final class Builder {
    private ExternalJwtProducer tokenProducer;
    
    private String headerName = "Authorization";
    
    private Authentication authentication;
    
    private ExternalJwtClaimsSet claimsSet;
    
    private HttpServletRequest httpRequest;
    
    public Builder withProducer(ExternalJwtProducer tokenProducer) {
      this.tokenProducer = tokenProducer;
      return this;
    }
    
    public Builder withAuthentication(Authentication authentication) {
      this.authentication = authentication;
      return this;
    }
    
    public Builder withHeaderName(String headerName) {
      if (StringUtils.isNotBlank(headerName))
        this.headerName = headerName; 
      return this;
    }
    
    public Builder withRequest(HttpServletRequest httpRequest) {
      this.httpRequest = httpRequest;
      return this;
    }
    
    public Builder withClaimsSet(ExternalJwtClaimsSet claimsSet) {
      this.claimsSet = claimsSet;
      return this;
    }
    
    public ExternalJwtHttpHeader build() throws JsonWebTokenException, ExternalJwtException {
      if (this.authentication == null)
        this.authentication = JsonWebTokenUtils.getSecurityContextAuthentication(); 
      ExternalJwt token = this.tokenProducer.createToken(this.authentication, this.httpRequest, this.claimsSet);
      return new ExternalJwtHttpHeader(token, this.headerName);
    }
    
    private Builder() {}
  }
}
