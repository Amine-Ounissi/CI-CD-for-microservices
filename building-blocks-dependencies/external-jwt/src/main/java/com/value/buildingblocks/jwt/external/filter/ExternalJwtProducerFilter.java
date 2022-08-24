package com.value.buildingblocks.jwt.external.filter;

import com.value.buildingblocks.jwt.core.util.JsonWebTokenUtils;
import com.value.buildingblocks.jwt.external.ExternalJwtProducer;
import com.value.buildingblocks.jwt.external.ExternalJwtProducerProperties;
import com.value.buildingblocks.jwt.external.exception.ExternalJwtException;
import com.value.buildingblocks.jwt.external.token.ExternalJwtCookie;
import com.value.buildingblocks.jwt.external.token.ExternalJwtHttpHeader;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public class ExternalJwtProducerFilter extends GenericFilterBean {
  private static final Logger log = LoggerFactory.getLogger(ExternalJwtProducerFilter.class);
  
  private final ExternalJwtProducer tokenProducer;
  
  private final String tokenCookieName;
  
  private final String tokenHeaderName;
  
  @Autowired
  public ExternalJwtProducerFilter(ExternalJwtProducer tokenProducer, ExternalJwtProducerProperties tokenProperties) {
    this.tokenProducer = tokenProducer;
    this.tokenCookieName = tokenProperties.getCookie().getName();
    this.tokenHeaderName = tokenProperties.getHeader().getName();
  }
  
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if (response instanceof HttpServletResponse && request instanceof HttpServletRequest) {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication != null && authentication.isAuthenticated()) {
        boolean isGenerated = generateHeaderToken((HttpServletResponse)response, (HttpServletRequest)request, authentication);
        if (!isGenerated)
          generateCookieToken((HttpServletResponse)response, (HttpServletRequest)request, authentication); 
      } 
    } 
    chain.doFilter(request, response);
  }
  
  private boolean generateHeaderToken(HttpServletResponse httpResponse, HttpServletRequest httpRequest, Authentication authentication) {
    String tokenHeader = httpRequest.getHeader(this.tokenHeaderName);
    if (tokenHeader != null && tokenHeader.startsWith("Bearer"))
      try {
        ExternalJwtHttpHeader header = ExternalJwtHttpHeader.create().withProducer(this.tokenProducer).withHeaderName(this.tokenHeaderName).withAuthentication(authentication).withRequest(httpRequest).build();
        httpResponse.addHeader(header.getHeaderName(), header.getHeaderValue());
        return true;
      } catch (ExternalJwtException |com.value.buildingblocks.jwt.core.exception.JsonWebTokenException e) {
        log.error("Can't create header base token from current authentication", e);
      }  
    return false;
  }
  
  private void generateCookieToken(HttpServletResponse httpResponse, HttpServletRequest httpRequest, Authentication authentication) {
    if (!JsonWebTokenUtils.getCookieValue(this.tokenCookieName, httpRequest).isPresent())
      try {
        ExternalJwtCookie cookie = ExternalJwtCookie.create().withProducer(this.tokenProducer).withCookieName(this.tokenCookieName).withAuthentication(authentication).withRequest(httpRequest).build();
        httpResponse.addCookie(cookie);
      } catch (ExternalJwtException|com.value.buildingblocks.jwt.core.exception.JsonWebTokenException e) {
        log.error("Can't create cookie based token from current authentication", e);
      }  
  }
}
