package com.value.buildingblocks.jwt.internal.token;

import com.value.buildingblocks.jwt.core.exception.JsonWebTokenException;
import com.value.buildingblocks.jwt.core.util.JsonWebTokenUtils;
import com.value.buildingblocks.jwt.internal.InternalJwtConsumer;
import com.value.buildingblocks.jwt.internal.InternalJwtProducer;
import com.value.buildingblocks.jwt.internal.exception.InternalJwtException;
import java.util.Enumeration;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;

public final class InternalJwtHttpHeader {
  public static final String HEADER_NAME = "Authorization";
  
  public static final String HEADER_SCHEMA = "Bearer";
  
  private final String value;
  
  private final InternalJwt internalJwt;
  
  private final String headerName;
  
  private InternalJwtHttpHeader(InternalJwt internalJwt) {
    this(internalJwt, "Authorization");
  }
  
  private InternalJwtHttpHeader(InternalJwt internalJwt, String headerName) {
    this.internalJwt = internalJwt;
    this.value = internalJwt.getSerializedToken();
    this.headerName = headerName;
  }
  
  public static Builder create() {
    return new Builder();
  }
  
  public static Optional<InternalJwtHttpHeader> load(InternalJwtConsumer tokenConsumer, HttpServletRequest httpRequest, String headerName) throws JsonWebTokenException, InternalJwtException {
    Optional<String> header = getHeader(headerName, httpRequest);
    if (header.isPresent()) {
      String value = header.get();
      if (!value.startsWith("Bearer"))
        return Optional.empty(); 
      value = value.substring("Bearer".length());
      if (StringUtils.isEmpty(value.trim()))
        return Optional.empty(); 
      InternalJwtHttpHeader internalJwtHttpHeader = new InternalJwtHttpHeader(tokenConsumer.parseToken(value.trim()));
      return Optional.of(internalJwtHttpHeader);
    } 
    return Optional.empty();
  }
  
  private static Optional<String> getHeader(String headerName, HttpServletRequest httpRequest) {
    Enumeration<String> headerNames = httpRequest.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String header = headerNames.nextElement();
      if (headerName.equalsIgnoreCase(header))
        return Optional.ofNullable(httpRequest.getHeader(header)); 
    } 
    return Optional.empty();
  }
  
  public String getHeaderName() {
    return this.headerName;
  }
  
  public String getHeaderValue() {
    return "Bearer " + this.value;
  }
  
  public InternalJwt getToken() {
    return this.internalJwt;
  }
  
  public static final class Builder {
    private InternalJwtProducer tokenProducer;
    
    private String headerName = "Authorization";
    
    private Authentication authentication;
    
    private InternalJwtClaimsSet claimsSet;
    
    private HttpServletRequest httpRequest;
    
    public Builder withProducer(InternalJwtProducer tokenProducer) {
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
    
    public Builder withClaimsSet(InternalJwtClaimsSet claimsSet) {
      this.claimsSet = claimsSet;
      return this;
    }
    
    public InternalJwtHttpHeader build() throws JsonWebTokenException, InternalJwtException {
      if (this.authentication == null)
        this.authentication = JsonWebTokenUtils.getSecurityContextAuthentication(); 
      InternalJwt token = this.tokenProducer.createToken(this.authentication, this.httpRequest, this.claimsSet);
      return new InternalJwtHttpHeader(token, this.headerName);
    }
    
    private Builder() {}
  }
}
