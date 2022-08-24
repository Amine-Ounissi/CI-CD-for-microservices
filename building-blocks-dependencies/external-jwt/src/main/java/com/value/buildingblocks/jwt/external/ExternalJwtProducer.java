package com.value.buildingblocks.jwt.external;

import com.value.buildingblocks.jwt.core.exception.JsonWebTokenException;
import com.value.buildingblocks.jwt.external.exception.ExternalJwtException;
import com.value.buildingblocks.jwt.external.token.ExternalJwt;
import com.value.buildingblocks.jwt.external.token.ExternalJwtClaimsSet;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface ExternalJwtProducer {
  default ExternalJwt createToken(Authentication authentication) throws ExternalJwtException, JsonWebTokenException {
    return createToken(authentication, null, null);
  }
  
  ExternalJwt createToken(Authentication paramAuthentication,
    ExternalJwtClaimsSet paramExternalJwtClaimsSet) throws ExternalJwtException, JsonWebTokenException;
  
  ExternalJwt createToken(Authentication paramAuthentication,
    HttpServletRequest paramHttpServletRequest, ExternalJwtClaimsSet paramExternalJwtClaimsSet) throws ExternalJwtException, JsonWebTokenException;
}
