package com.value.buildingblocks.jwt.internal.impl;

import com.value.buildingblocks.jwt.core.JsonWebTokenProducerType;
import com.value.buildingblocks.jwt.core.exception.JsonWebTokenException;
import com.value.buildingblocks.jwt.core.token.JsonWebTokenClaimsSet;
import com.value.buildingblocks.jwt.internal.InternalJwtMapper;
import com.value.buildingblocks.jwt.internal.InternalJwtProducer;
import com.value.buildingblocks.jwt.internal.InternalJwtTenantMapper;
import com.value.buildingblocks.jwt.internal.token.InternalJwt;
import com.value.buildingblocks.jwt.internal.token.InternalJwtClaimsSet;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public class InternalJwtProducerImpl implements InternalJwtProducer {
  private final JsonWebTokenProducerType<JsonWebTokenClaimsSet, String> tokenProducerType;
  
  private final InternalJwtMapper tokenMapper;
  
  private InternalJwtTenantMapper tenantMapper;
  
  public InternalJwtProducerImpl(JsonWebTokenProducerType<JsonWebTokenClaimsSet, String> tokenProducerType, InternalJwtMapper tokenMapper, InternalJwtTenantMapper tenantMapper) {
    this.tokenProducerType = tokenProducerType;
    this.tokenMapper = tokenMapper;
    this.tenantMapper = tenantMapper;
  }
  
  public InternalJwt createToken(Authentication authentication, InternalJwtClaimsSet claimsSet) throws JsonWebTokenException {
    InternalJwtClaimsSet authenticationClaimsSet = buildInternalJwtClaimsSet(authentication, null, claimsSet);
    return buildInternalJwt(authenticationClaimsSet);
  }
  
  public InternalJwt createToken(Authentication authentication, HttpServletRequest request, InternalJwtClaimsSet claimsSet) throws JsonWebTokenException {
    InternalJwtClaimsSet authenticationClaimsSet = buildInternalJwtClaimsSet(authentication, request, claimsSet);
    return buildInternalJwt(authenticationClaimsSet);
  }
  
  private InternalJwtClaimsSet buildInternalJwtClaimsSet(Authentication authentication, HttpServletRequest request, InternalJwtClaimsSet claimsSet) {
    return (new InternalJwtClaimsSet.Builder())
      .withTokenMapper(this.tokenMapper)
      .withTenantMapper(this.tenantMapper)
      .loadAuthentication(authentication)
      .addClaims(authentication)
      .addMappedClaims(authentication, request)
      .addClaims(new JsonWebTokenClaimsSet[] { claimsSet }).build();
  }
  
  private InternalJwt buildInternalJwt(InternalJwtClaimsSet authenticationClaimsSet) throws JsonWebTokenException {
    String serializedToken = (String)this.tokenProducerType.createToken(authenticationClaimsSet);
    return new InternalJwt(serializedToken, authenticationClaimsSet);
  }
}
