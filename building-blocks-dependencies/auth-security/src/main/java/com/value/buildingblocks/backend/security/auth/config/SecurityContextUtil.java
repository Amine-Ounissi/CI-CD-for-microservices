package com.value.buildingblocks.backend.security.auth.config;

import com.value.buildingblocks.backend.internalrequest.InternalRequestContext;
import com.value.buildingblocks.jwt.core.token.JsonWebTokenClaimsSet;
import com.value.buildingblocks.jwt.internal.InternalJwtConsumer;
import com.value.buildingblocks.jwt.internal.authentication.InternalJwtAuthentication;
import com.value.buildingblocks.jwt.internal.token.InternalJwt;
import com.value.buildingblocks.jwt.internal.token.InternalJwtClaimsSet;
import java.util.Collection;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class SecurityContextUtil {
  private static final Logger log = LoggerFactory.getLogger(SecurityContextUtil.class);
  
  private static final String PREFIX = "bearer ";
  
  private final InternalJwtConsumer internalJwtConsumer;
  
  private final String serviceApiScope;
  
  @Autowired
  private HttpServletRequest httpServletRequest;
  
  @Autowired
  private InternalRequestContext internalRequestContext;
  
  public SecurityContextUtil(InternalJwtConsumer internalJwtConsumer, String serviceApiScope) {
    this.internalJwtConsumer = internalJwtConsumer;
    this.serviceApiScope = serviceApiScope;
  }
  
  public <T> Optional<T> getUserTokenClaim(String claim, Class<T> type) {
    return getOriginatingUserJwt()
      .map(InternalJwt::getClaimsSet)
      .map(userClaims -> userClaims.getClaim(claim).orElse(null))
      .filter(type::isInstance)
      .map(type::cast);
  }
  
  private boolean containsServiceScope(JsonWebTokenClaimsSet claims) {
    if (StringUtils.isEmpty(this.serviceApiScope))
      return false; 
    return claims.getClaim("scope")
      .filter(Collection.class::isInstance)
      .map(Collection.class::cast)
      .map(scopes -> Boolean.valueOf(scopes.contains(this.serviceApiScope)))
      .orElse(Boolean.FALSE).booleanValue();
  }
  
  public Optional<String> getInternalId() {
    return getUserTokenClaim("inuid", String.class);
  }
  
  public Optional<InternalJwt> getOriginatingUserJwt() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof InternalJwtAuthentication) {
      InternalJwtAuthentication internalJwtAuthentication = (InternalJwtAuthentication)authentication;
      InternalJwtClaimsSet claims = (InternalJwtClaimsSet)internalJwtAuthentication.getDetails();
      if (!containsServiceScope((JsonWebTokenClaimsSet)claims))
        return Optional.of(new InternalJwt(internalJwtAuthentication.getCredentials(), claims)); 
    } 
    return determineOriginatingUserJwtFromRequestAttributes();
  }
  
  private Optional<InternalJwt> determineOriginatingUserJwtFromRequestAttributes() {
    RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
    if (attributes != null) {
      Optional<InternalJwt> result;
      Object attribute = attributes.getAttribute("com.value.buildingblocks.backend.security.auth.config.SecurityContextUtil.ORIGINATING_USER_JWT", 0);
      if (attribute instanceof Optional) {
        Optional optionalAttribute = (Optional)attribute;
        if (!optionalAttribute.isPresent())
          return Optional.empty(); 
        Object wrappedAttribute = optionalAttribute.get();
        if (wrappedAttribute instanceof InternalJwt)
          return Optional.of((InternalJwt)wrappedAttribute); 
      } 
      if (RequestContextHolder.getRequestAttributes() instanceof org.springframework.web.context.request.ServletRequestAttributes) {
        result = determineInternalJwtFromRequestHeader();
      } else {
        result = determineInternalJwtFromInternalRequestContext();
      } 
      attributes.setAttribute("com.value.buildingblocks.backend.security.auth.config.SecurityContextUtil.ORIGINATING_USER_JWT", result, 0);
      return result;
    } 
    log.warn("No RequestAttributes are set. RequestAttributes should now be set for HTTP Requests.");
    return Optional.empty();
  }
  
  private Optional<InternalJwt> determineInternalJwtFromRequestHeader() {
    String rawToken = this.httpServletRequest.getHeader("X-CXT-User-Token");
    if (!StringUtils.isEmpty(rawToken))
      try {
        return Optional.of(this.internalJwtConsumer.parseToken(getToken(rawToken)));
      } catch (Exception ex) {
        log.warn("Error extracting originating user token from {} header", "X-CXT-User-Token", ex);
      }  
    log.debug("UserToken not found in HttpServletRequest");
    return Optional.empty();
  }
  
  private Optional<InternalJwt> determineInternalJwtFromInternalRequestContext() {
    String rawToken = this.internalRequestContext.getUserToken();
    if (!StringUtils.isEmpty(rawToken))
      try {
        return Optional.of(this.internalJwtConsumer.parseToken(getToken(rawToken)));
      } catch (Exception ex) {
        log.warn("Error extracting originating user token from InternalRequestContext", ex);
      }  
    log.debug("UserToken not found in InternalRequestContext");
    return Optional.empty();
  }
  
  private String getToken(String securityContext) {
    if (securityContext.toLowerCase().startsWith("bearer "))
      return securityContext.substring("bearer ".length()); 
    return securityContext;
  }
}
