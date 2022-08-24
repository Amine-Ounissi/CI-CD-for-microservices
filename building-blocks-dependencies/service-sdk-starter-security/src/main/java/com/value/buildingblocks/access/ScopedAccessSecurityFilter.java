package com.value.buildingblocks.access;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.web.filter.OncePerRequestFilter;

public class ScopedAccessSecurityFilter extends OncePerRequestFilter {

  private final FilterSecurityInterceptor filterSecurityInterceptor;

  public ScopedAccessSecurityFilter(FilterSecurityInterceptor filterSecurityInterceptor) {
    this.filterSecurityInterceptor = filterSecurityInterceptor;
  }

  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
    FilterChain chain) throws ServletException, IOException {
    FilterInvocation fi = new FilterInvocation(request, response, chain);
    this.filterSecurityInterceptor.invoke(fi);
  }
}
