package com.value.buildingblocks.jwt.external.filter;

import com.value.buildingblocks.jwt.core.authenticaton.SecurityContextHolderHelper;
import com.value.buildingblocks.jwt.core.blacklist.TokenBlacklistService;
import com.value.buildingblocks.jwt.external.ExternalJwtConsumer;
import com.value.buildingblocks.jwt.external.ExternalJwtConsumerProperties;
import com.value.buildingblocks.jwt.external.ExternalJwtProducer;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.GenericFilterBean;

public class ExternalJwtConsumerFilter extends GenericFilterBean {
  private final ExternalJwtConsumerHelper externalJwtConsumerHelper;
  
  @Autowired
  public ExternalJwtConsumerFilter(
    ExternalJwtConsumer tokenConsumer, ExternalJwtProducer tokenProducer, ExternalJwtConsumerProperties tokenProperties, TokenBlacklistService tokenBlacklistService) {
    this.externalJwtConsumerHelper = new ExternalJwtConsumerHelper(tokenConsumer, tokenProducer, tokenProperties, tokenBlacklistService);
  }
  
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
      HttpServletRequest httpServletRequest = (HttpServletRequest)request;
      HttpServletResponse httpServletResponse = (HttpServletResponse)response;
      Optional<ExternalJwtConsumerData> data = this.externalJwtConsumerHelper.process(httpServletRequest);
      data.ifPresent(consumerData -> {
            SecurityContextHolderHelper.setAuthentication(consumerData.getAuthentication());
            consumerData.getTokenCookie().ifPresent(httpServletResponse::addCookie);
          });
    } 
    chain.doFilter(request, response);
  }
}
