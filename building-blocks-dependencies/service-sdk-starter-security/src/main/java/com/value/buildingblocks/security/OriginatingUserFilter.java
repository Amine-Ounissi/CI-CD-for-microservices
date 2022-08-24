package com.value.buildingblocks.security;

import com.value.buildingblocks.common.OriginatingUserJwtHolder;
import com.value.buildingblocks.jwt.internal.InternalJwtConsumer;
import com.value.buildingblocks.jwt.internal.authentication.InternalJwtAuthentication;
import com.value.buildingblocks.jwt.internal.token.InternalJwt;
import com.value.buildingblocks.jwt.internal.token.InternalJwtClaimsSet;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.OncePerRequestFilter;

public class OriginatingUserFilter extends OncePerRequestFilter {
  private static final Logger log = LoggerFactory.getLogger(OriginatingUserFilter.class);
  
  private static final String PREFIX = "bearer ";
  
  private final InternalJwtConsumer internalJwtConsumer;
  
  private String serviceApiPath = "/service-api/";
  
  public OriginatingUserFilter(InternalJwtConsumer internalJwtConsumer) {
    this.internalJwtConsumer = internalJwtConsumer;
  }
  
  public String getServiceApiPath() {
    return this.serviceApiPath;
  }
  
  public void setServiceApiPath(String serviceApiPath) {
    this.serviceApiPath = serviceApiPath;
  }
  
  private boolean isServiceApiRequest(HttpServletRequest httpServletRequest) {
    return httpServletRequest.getRequestURI().startsWith(httpServletRequest.getContextPath() + this.serviceApiPath);
  }
  
  protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
    InternalJwt internalJwt = null;
    if (isServiceApiRequest(httpServletRequest)) {
      internalJwt = getInternalJwtFromRequestHeader(
          (ServletRequestAttributes)RequestContextHolder.getRequestAttributes());
    } else {
      internalJwt = getInternalJwtFromAuthentication(internalJwt);
    } 
    OriginatingUserJwtHolder.setOriginatingUserJwt(internalJwt);
    try {
      filterChain.doFilter((ServletRequest)httpServletRequest, (ServletResponse)httpServletResponse);
    } finally {
      OriginatingUserJwtHolder.clearContext();
      log.debug("OriginatingUserJwtHolder now cleared, as request processing completed");
    } 
  }
  
  private InternalJwt getInternalJwtFromAuthentication(InternalJwt internalJwt) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof InternalJwtAuthentication) {
      InternalJwtAuthentication internalJwtAuthentication = (InternalJwtAuthentication)authentication;
      InternalJwtClaimsSet claims = (InternalJwtClaimsSet)internalJwtAuthentication.getDetails();
      internalJwt = new InternalJwt(internalJwtAuthentication.getCredentials(), claims);
    } 
    return internalJwt;
  }
  
  private InternalJwt getInternalJwtFromRequestHeader(ServletRequestAttributes requestAttributes) {
    String rawToken = requestAttributes.getRequest().getHeader("X-CXT-User-Token");
    if (!StringUtils.isEmpty(rawToken))
      try {
        return this.internalJwtConsumer.parseToken(getToken(rawToken));
      } catch (Exception ex) {
        log.warn("Error extracting originating user token from {} header", "X-CXT-User-Token", ex);
      }  
    log.debug("UserToken not found in HttpServletRequest");
    return null;
  }
  
  private String getToken(String securityContext) {
    if (securityContext.toLowerCase().startsWith("bearer "))
      return securityContext.substring("bearer ".length()); 
    return securityContext;
  }
}
