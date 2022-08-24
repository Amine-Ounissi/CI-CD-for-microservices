package com.value.authentication.tokenconverter.filter;

import com.value.authentication.tokenconverter.exception.common.TokenConverterBaseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ErrorHandlingFilter extends OncePerRequestFilter {
  private static final Logger log = LoggerFactory.getLogger(ErrorHandlingFilter.class);
  
  public static final String TOKEN_VERIFICATION_ERROR = "Token verification failed :: {}";
  
  private final ObjectMapper mapper = new ObjectMapper();
  
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    try {
      filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    } catch (TokenConverterBaseException exception) {
      logMessage(exception);
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().write(this.mapper.writeValueAsString(exception.getErrorSet()));
    } 
  }
  
  private void logMessage(TokenConverterBaseException exception) {
    switch (exception.getLoggingLevel().ordinal()) {
      case 1:
        log.debug("Token verification failed :: {}", exception.getErrorSet());
        break;
      case 2:
        log.info("Token verification failed :: {}", exception.getErrorSet());
        break;
      case 3:
        log.warn("Token verification failed :: {}", exception.getErrorSet());
        break;
      case 4:
        log.error("Token verification failed :: {}", exception.getErrorSet());
        break;
      case 5:
        log.trace("Token verification failed :: {}", exception.getErrorSet());
        break;
    } 
  }
}
