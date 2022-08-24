package com.value.buildingblocks.jwt.internal;

import com.value.buildingblocks.jwt.core.exception.JsonWebTokenException;
import com.value.buildingblocks.jwt.internal.exception.InternalJwtException;
import com.value.buildingblocks.jwt.internal.token.InternalJwt;
import com.value.buildingblocks.jwt.internal.token.InternalJwtClaimsSet;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface InternalJwtProducer {
  default InternalJwt createToken(Authentication authentication) throws InternalJwtException, JsonWebTokenException {
    return createToken(authentication, null, null);
  }
  
  InternalJwt createToken(Authentication paramAuthentication,
    InternalJwtClaimsSet paramInternalJwtClaimsSet) throws InternalJwtException, JsonWebTokenException;
  
  InternalJwt createToken(Authentication paramAuthentication,
    HttpServletRequest paramHttpServletRequest, InternalJwtClaimsSet paramInternalJwtClaimsSet) throws InternalJwtException, JsonWebTokenException;
}
