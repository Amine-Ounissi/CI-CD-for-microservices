package com.value.buildingblocks.jwt.internal.token;

import com.value.buildingblocks.jwt.core.exception.JsonWebTokenException;
import com.value.buildingblocks.jwt.core.util.JsonWebTokenUtils;
import com.value.buildingblocks.jwt.internal.InternalJwtConsumer;
import com.value.buildingblocks.jwt.internal.InternalJwtProducer;
import com.value.buildingblocks.jwt.internal.exception.InternalJwtException;
import java.util.Optional;
import javax.jms.JMSException;
import javax.jms.Message;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

public final class InternalJwtMessageHeader {
  public static final String HEADER_NAME = "Authorization";
  
  private final String value;
  
  private final InternalJwt internalJwt;
  
  private InternalJwtMessageHeader(InternalJwt internalJwt) {
    this.internalJwt = internalJwt;
    this.value = internalJwt.getSerializedToken();
  }
  
  public static Builder create() {
    return new Builder();
  }
  
  @Deprecated
  public static Optional<InternalJwtMessageHeader> load(InternalJwtConsumer tokenConsumer, Message message) throws JsonWebTokenException, InternalJwtException {
    Optional<String> header = getHeader("Authorization", message);
    if (header.isPresent()) {
      String value = header.get();
      InternalJwtMessageHeader internalJwtMessageHeader = new InternalJwtMessageHeader(tokenConsumer.parseToken(value.trim()));
      return Optional.of(internalJwtMessageHeader);
    } 
    return Optional.empty();
  }
  
  public static Optional<InternalJwtMessageHeader> load(InternalJwtConsumer tokenConsumer, String headerValue) throws JsonWebTokenException, InternalJwtException {
    if (!StringUtils.isEmpty(headerValue)) {
      InternalJwtMessageHeader internalJwtMessageHeader = new InternalJwtMessageHeader(tokenConsumer.parseToken(headerValue.trim()));
      return Optional.of(internalJwtMessageHeader);
    } 
    return Optional.empty();
  }
  
  @Deprecated
  private static Optional<String> getHeader(String headerName, Message message) {
    String token = null;
    try {
      token = message.getStringProperty(headerName);
    } catch (JMSException ignore) {
      return Optional.empty();
    } 
    if (StringUtils.isEmpty(token))
      return Optional.empty(); 
    return Optional.of(token);
  }
  
  public String getHeaderName() {
    return "Authorization";
  }
  
  public String getHeaderValue() {
    return this.value;
  }
  
  public InternalJwt getToken() {
    return this.internalJwt;
  }
  
  public static final class Builder {
    private InternalJwtProducer tokenProducer;
    
    private Authentication authentication;
    
    private InternalJwtClaimsSet claimsSet;
    
    private Builder() {}
    
    public Builder withProducer(InternalJwtProducer tokenProducer) {
      this.tokenProducer = tokenProducer;
      return this;
    }
    
    public Builder withAuthentication(Authentication authentication) {
      this.authentication = authentication;
      return this;
    }
    
    public Builder withClaimsSet(InternalJwtClaimsSet claimsSet) {
      this.claimsSet = claimsSet;
      return this;
    }
    
    public InternalJwtMessageHeader build() throws JsonWebTokenException, InternalJwtException {
      if (this.authentication == null)
        this.authentication = JsonWebTokenUtils.getSecurityContextAuthentication(); 
      return new InternalJwtMessageHeader(this.tokenProducer.createToken(this.authentication, this.claimsSet));
    }
  }
}
