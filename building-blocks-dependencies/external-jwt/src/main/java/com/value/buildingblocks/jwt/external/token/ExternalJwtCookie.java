package com.value.buildingblocks.jwt.external.token;

import com.value.buildingblocks.jwt.core.exception.JsonWebTokenException;
import com.value.buildingblocks.jwt.core.util.JsonWebTokenUtils;
import com.value.buildingblocks.jwt.external.ExternalJwtConsumer;
import com.value.buildingblocks.jwt.external.ExternalJwtProducer;
import com.value.buildingblocks.jwt.external.exception.ExternalJwtException;
import java.time.ZonedDateTime;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;

public final class ExternalJwtCookie extends Cookie {
  public static final String COOKIE_NAME = "Authorization";
  
  private final transient ExternalJwt externalJwt;
  
  private ExternalJwtCookie(ExternalJwt externalJwt) {
    this(externalJwt, "Authorization", ExternalJwtUtils.getCurrentUtc());
  }
  
  private ExternalJwtCookie(ExternalJwt externalJwt, String cookieName, ZonedDateTime currentTime) {
    super(cookieName, externalJwt.getSerializedToken());
    this.externalJwt = externalJwt;
    setMaxAge(getDefaultMaxAge(currentTime));
    setHttpOnly(true);
    setPath("/");
  }
  
  public static Builder create() {
    return new Builder();
  }
  
  public static Optional<ExternalJwt> load(ExternalJwtConsumer tokenConsumer, HttpServletRequest httpRequest, String cookieName) throws ExternalJwtException, JsonWebTokenException {
    Optional<Cookie> cookie = JsonWebTokenUtils.getCookie(cookieName, httpRequest);
    if (cookie.isPresent()) {
      String token = ((Cookie)cookie.get()).getValue();
      return Optional.of(tokenConsumer.parseToken(token));
    } 
    return Optional.empty();
  }
  
  private int getDefaultMaxAge(ZonedDateTime currentTime) {
    Optional<Integer> expiresInSec = ExternalJwtUtils.getCookieExpiresInSec(this.externalJwt.getClaimsSet(), currentTime);
    if (expiresInSec.isPresent())
      return ((Integer)expiresInSec.get()).intValue(); 
    return getMaxAge();
  }
  
  public ExternalJwt getToken() {
    return this.externalJwt;
  }
  
  public static final class Builder {
    private ExternalJwtProducer tokenProducer;
    
    private String cookieName = "Authorization";
    
    private Authentication authentication;
    
    private ExternalJwtClaimsSet claimsSet;
    
    private HttpServletRequest httpRequest;
    
    private ZonedDateTime currentTime;
    
    public Builder withProducer(ExternalJwtProducer tokenProducer) {
      this.tokenProducer = tokenProducer;
      return this;
    }
    
    public Builder withAuthentication(Authentication authentication) {
      this.authentication = authentication;
      return this;
    }
    
    public Builder withCookieName(String cookieName) {
      if (StringUtils.isNotBlank(cookieName))
        this.cookieName = cookieName; 
      return this;
    }
    
    public Builder withRequest(HttpServletRequest httpRequest) {
      this.httpRequest = httpRequest;
      return this;
    }
    
    public Builder withCurrentTime(ZonedDateTime currentTime) {
      this.currentTime = currentTime;
      return this;
    }
    
    public Builder withClaimsSet(ExternalJwtClaimsSet claimsSet) {
      this.claimsSet = claimsSet;
      return this;
    }
    
    public ExternalJwtCookie build() throws JsonWebTokenException, ExternalJwtException {
      if (this.authentication == null)
        this.authentication = JsonWebTokenUtils.getSecurityContextAuthentication(); 
      ExternalJwt token = this.tokenProducer.createToken(this.authentication, this.httpRequest, this.claimsSet);
      return new ExternalJwtCookie(token, this.cookieName, this.currentTime);
    }
    
    private Builder() {}
  }
}
