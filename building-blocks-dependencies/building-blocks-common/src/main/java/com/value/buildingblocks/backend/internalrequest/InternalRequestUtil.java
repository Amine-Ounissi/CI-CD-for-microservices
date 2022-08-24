package com.value.buildingblocks.backend.internalrequest;

import com.value.buildingblocks.backend.communication.context.UserContext;
import com.value.buildingblocks.jwt.core.exception.JsonWebTokenException;
import com.value.buildingblocks.jwt.internal.InternalJwtConsumer;
import com.value.buildingblocks.jwt.internal.authentication.InternalJwtAuthentication;
import com.value.buildingblocks.jwt.internal.exception.InternalJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

public class InternalRequestUtil {
  public static final String USER_CONTEXT_SERVICE_AGREEMENT_CLAIM_KEY = "said";
  
  public static final String USER_CONTEXT_LEGAL_ENTITY_CLAIM_KEY = "leid";
  
  private static final String PREFIX = "bearer ";
  
  private static final Logger log = LoggerFactory.getLogger(InternalRequestUtil.class);
  
  private final InternalJwtConsumer internalJwtConsumer;
  
  public InternalRequestUtil(InternalJwtConsumer internalJwtConsumer) {
    this.internalJwtConsumer = internalJwtConsumer;
  }
  
  private InternalJwtAuthentication getInternalJwtAuthentication(String securityContext) throws JsonWebTokenException, InternalJwtException {
    String token = getToken(securityContext);
    return new InternalJwtAuthentication(this.internalJwtConsumer.parseToken(token));
  }
  
  private String getToken(String securityContext) {
    if (securityContext.toLowerCase().startsWith("bearer "))
      return securityContext.substring("bearer ".length()); 
    return securityContext;
  }
  
  public String getInternalId(String securityContext) {
    String userId = null;
    if (!StringUtils.isEmpty(securityContext))
      try {
        InternalJwtAuthentication internalJwtAuthentication = getInternalJwtAuthentication(securityContext);
        userId = (String) internalJwtAuthentication.getDetails().getClaim("inuid").orElse(null);
      } catch (JsonWebTokenException|InternalJwtException|RuntimeException e) {
        log.warn("Error extracting user id from JWT", e);
      }  
    return userId;
  }
  
  public String getInternalId(InternalRequest<?> request) {
    if (request != null) {
      InternalRequestContext internalRequestContext = request.getInternalRequestContext();
      return getInternalId(internalRequestContext);
    } 
    return null;
  }
  
  public String getInternalId(InternalRequestContext internalRequestContext) {
    String userId = null;
    if (internalRequestContext != null && !StringUtils.isEmpty(internalRequestContext.getUserToken()))
      try {
        InternalJwtAuthentication internalJwtAuthentication = getInternalJwtAuthentication(internalRequestContext.getUserToken());
        userId = (String) internalJwtAuthentication.getDetails().getClaim("inuid").orElse(null);
      } catch (JsonWebTokenException|InternalJwtException|RuntimeException e) {
        log.warn("Error extracting user id from JWT", e);
      }  
    return userId;
  }
  
  public UserContext getUserContext(InternalRequestContext internalRequestContext) {
    UserContext userContext = null;
    if (internalRequestContext != null && !StringUtils.isEmpty(internalRequestContext.getUserToken()))
      try {
        InternalJwtAuthentication internalJwtAuthentication = getInternalJwtAuthentication(internalRequestContext.getUserToken());
        userContext = getUserContext(internalJwtAuthentication);
      } catch (JsonWebTokenException|InternalJwtException|RuntimeException e) {
        log.warn("Error extracting user context from JWT", e);
      }  
    return userContext;
  }
  
  public UserContext getUserContext() {
    UserContext userContext = null;
    try {
      if (SecurityContextHolder.getContext().getAuthentication() instanceof InternalJwtAuthentication) {
        InternalJwtAuthentication internalJwtAuthentication = (InternalJwtAuthentication)SecurityContextHolder.getContext().getAuthentication();
        userContext = getUserContext(internalJwtAuthentication);
      } 
    } catch (RuntimeException e) {
      log.warn("Error extracting user context from JWT", e);
    } 
    return userContext;
  }
  
  private UserContext getUserContext(InternalJwtAuthentication internalJwtAuthentication) {
    String serviceAgreementId = (String) internalJwtAuthentication.getDetails().getClaim("said").orElse(null);
    String legalEntityId = (String) internalJwtAuthentication.getDetails().getClaim("leid").orElse(null);
    if (!StringUtils.isEmpty(serviceAgreementId))
      return new UserContext(serviceAgreementId, legalEntityId); 
    return null;
  }
}
