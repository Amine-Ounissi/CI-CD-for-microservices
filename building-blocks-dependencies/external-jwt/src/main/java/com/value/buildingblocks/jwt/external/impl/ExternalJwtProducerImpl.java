package com.value.buildingblocks.jwt.external.impl;

import com.value.buildingblocks.jwt.core.JsonWebTokenProducerType;
import com.value.buildingblocks.jwt.core.exception.JsonWebTokenException;
import com.value.buildingblocks.jwt.core.token.JsonWebTokenClaimsSet;
import com.value.buildingblocks.jwt.external.ExternalJwtMapper;
import com.value.buildingblocks.jwt.external.ExternalJwtProducer;
import com.value.buildingblocks.jwt.external.ExternalJwtProducerProperties;
import com.value.buildingblocks.jwt.external.ExternalJwtTenantMapper;
import com.value.buildingblocks.jwt.external.token.ExternalJwt;
import com.value.buildingblocks.jwt.external.token.ExternalJwtClaimsSet;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public class ExternalJwtProducerImpl implements ExternalJwtProducer {
  private final JsonWebTokenProducerType<JsonWebTokenClaimsSet, String> tokenProducerType;
  
  private final ExternalJwtProducerProperties properties;
  
  private final ExternalJwtMapper tokenMapper;
  
  private ExternalJwtTenantMapper tenantMapper;
  
  public ExternalJwtProducerImpl(JsonWebTokenProducerType<JsonWebTokenClaimsSet, String> tokenProducerType, ExternalJwtProducerProperties properties, ExternalJwtMapper tokenMapper, ExternalJwtTenantMapper tenantMapper) {
    this.tokenProducerType = tokenProducerType;
    this.properties = properties;
    this.tokenMapper = tokenMapper;
    this.tenantMapper = tenantMapper;
  }
  
  public ExternalJwt createToken(Authentication authentication, ExternalJwtClaimsSet claimsSet) throws JsonWebTokenException {
    return createToken(authentication, null, claimsSet);
  }
  
  public ExternalJwt createToken(Authentication authentication, HttpServletRequest request, ExternalJwtClaimsSet claimsSet) throws JsonWebTokenException {
    ExternalJwtClaimsSet authenticationClaimsSet = buildExternalJwtClaimsSet(authentication, request, claimsSet);
    return buildInternalJwt(authenticationClaimsSet);
  }
  
  private ExternalJwtClaimsSet buildExternalJwtClaimsSet(Authentication authentication, HttpServletRequest request, ExternalJwtClaimsSet claimsSet) {
    return (new ExternalJwtClaimsSet.Builder())
      .withTokenMapper(this.tokenMapper)
      .withTenantMapper(this.tenantMapper)
      .loadAuthentication(authentication)
      .addClaims(authentication)
      .addTimeValidationClaims(this.properties.getExpiration())
      .addMappedClaims(authentication, request)
      .addClaims(new JsonWebTokenClaimsSet[] { claimsSet }).addNotValidAfterIfNotPresent(this.properties.getNotValidAfter())
      .addUniqueTokenId()
      .build();
  }
  
  private ExternalJwt buildInternalJwt(ExternalJwtClaimsSet authenticationClaimsSet) throws JsonWebTokenException {
    String serializedToken = (String)this.tokenProducerType.createToken(authenticationClaimsSet);
    return new ExternalJwt(serializedToken, authenticationClaimsSet);
  }
}
