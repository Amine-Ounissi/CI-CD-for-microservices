package com.value.buildingblocks.jwt.internal.filter;

import com.value.buildingblocks.jwt.core.authenticaton.SecurityContextHolderHelper;
import com.value.buildingblocks.jwt.core.exception.JsonWebTokenException;
import com.value.buildingblocks.jwt.core.token.JsonWebTokenClaimsSetUtils;
import com.value.buildingblocks.jwt.internal.InternalJwtConsumer;
import com.value.buildingblocks.jwt.internal.InternalJwtConsumerProperties;
import com.value.buildingblocks.jwt.internal.authentication.InternalJwtAuthentication;
import com.value.buildingblocks.jwt.internal.exception.InternalJwtException;
import com.value.buildingblocks.jwt.internal.token.InternalJwtClaimsSet;
import com.value.buildingblocks.jwt.internal.token.InternalJwtHttpHeader;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.GenericFilterBean;

public class InternalJwtConsumerFilter extends GenericFilterBean {

  private static final Logger log = LoggerFactory.getLogger(InternalJwtConsumerFilter.class);

  private final InternalJwtConsumer tokenConsumer;

  private final String tokenHeaderName;

  public InternalJwtConsumerFilter(InternalJwtConsumer tokenConsumer) {
    this(tokenConsumer, null);
  }

  @Autowired
  public InternalJwtConsumerFilter(InternalJwtConsumer tokenConsumer,
    InternalJwtConsumerProperties tokenProperties) {
    this.tokenConsumer = tokenConsumer;
    if (tokenProperties != null) {
      this.tokenHeaderName = tokenProperties.getHeader().getName();
    } else {
      this.tokenHeaderName = "Authorization";
    }
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException {
    if (request instanceof HttpServletRequest) {
      HttpServletRequest httpRequest = (HttpServletRequest) request;
      try {
        Optional<InternalJwtHttpHeader> header = InternalJwtHttpHeader
          .load(this.tokenConsumer, httpRequest, this.tokenHeaderName);
        header.ifPresent(this::processToken);
      } catch (JsonWebTokenException | InternalJwtException e) {
        log
          .error("Problem loading Internal Json Web Token from the {} header", this.tokenHeaderName,
            e);
      }
    }
    chain.doFilter(request, response);
  }

  protected void processToken(InternalJwtHttpHeader header) {
    InternalJwtAuthentication internalJwtAuthentication = new InternalJwtAuthentication(
      header.getToken());
    log.debug("Load authentication from a token (sub: '{}', iss: '{}')",
      JsonWebTokenClaimsSetUtils
        .getClaim(internalJwtAuthentication, InternalJwtClaimsSet::getSubject),
      JsonWebTokenClaimsSetUtils
        .getClaim(internalJwtAuthentication, InternalJwtClaimsSet::getIssuer));
    SecurityContextHolderHelper.setAuthentication(internalJwtAuthentication);
  }
}
