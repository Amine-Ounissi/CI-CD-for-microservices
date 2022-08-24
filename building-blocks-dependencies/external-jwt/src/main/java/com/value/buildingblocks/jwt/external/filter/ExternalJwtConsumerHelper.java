package com.value.buildingblocks.jwt.external.filter;

import com.value.buildingblocks.jwt.core.blacklist.TokenBlacklistService;
import com.value.buildingblocks.jwt.core.blacklist.dto.BlacklistRequest;
import com.value.buildingblocks.jwt.core.exception.BlacklistException;
import com.value.buildingblocks.jwt.core.exception.JsonWebTokenException;
import com.value.buildingblocks.jwt.core.util.JsonWebTokenUtils;
import com.value.buildingblocks.jwt.external.ExternalJwtConsumer;
import com.value.buildingblocks.jwt.external.ExternalJwtConsumerProperties;
import com.value.buildingblocks.jwt.external.ExternalJwtProducer;
import com.value.buildingblocks.jwt.external.ExternalJwtRefreshProperties;
import com.value.buildingblocks.jwt.external.authentication.ExternalJwtAuthentication;
import com.value.buildingblocks.jwt.external.exception.ExternalJwtException;
import com.value.buildingblocks.jwt.external.token.ExternalJwt;
import com.value.buildingblocks.jwt.external.token.ExternalJwtCookie;
import com.value.buildingblocks.jwt.external.token.ExternalJwtHttpHeader;
import com.value.buildingblocks.jwt.external.token.ExternalJwtUtils;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

public class ExternalJwtConsumerHelper {
  private static final Logger logger = LoggerFactory.getLogger(ExternalJwtConsumerHelper.class);
  
  private static final HashSet<String> ALLOWED_METHODS = new HashSet<>(Arrays.asList(new String[] { "GET", "HEAD", "TRACE", "OPTIONS" }));
  
  private static final String CSRF_COOKIE_NAME = "XSRF-TOKEN";
  
  private static final String CSRF_HEADER_NAME = "X-XSRF-TOKEN";
  
  private static final String COOKIE_LITERAL = "cookie";
  
  private static final String HEADER_LITERAL = "header";
  
  private static final String AUTHORIZATION_CSRF_TOKEN = "YXV0aG9yaXphdGlvbi13aXRoLWhlYWRlci1jc3JmLXRva2Vu";
  
  private static final Cookie AUTHORIZATION_CSRF_COOKIE = new Cookie("XSRF-TOKEN", "YXV0aG9yaXphdGlvbi13aXRoLWhlYWRlci1jc3JmLXRva2Vu");
  
  private static final String BLACKLIST_REFRESH_REASON = "Token refresh request";
  
  private final ExternalJwtConsumer tokenConsumer;
  
  private final ExternalJwtProducer tokenProducer;
  
  private TokenBlacklistService tokenBlacklistService;
  
  private final AntPathMatcher antPathMatcher = new AntPathMatcher();
  
  private final ExternalJwtConsumerProperties tokenProperties;
  
  private final ExternalJwtRefreshProperties refreshProperties;
  
  private final String tokenCookieName;
  
  private final String tokenHeaderName;
  
  public ExternalJwtConsumerHelper(ExternalJwtConsumer tokenConsumer, ExternalJwtProducer tokenProducer, ExternalJwtConsumerProperties tokenProperties, TokenBlacklistService tokenBlacklistService) {
    this.tokenConsumer = tokenConsumer;
    this.tokenProducer = tokenProducer;
    this.tokenProperties = tokenProperties;
    this.tokenBlacklistService = tokenBlacklistService;
    this.tokenCookieName = tokenProperties.getCookie().getName();
    this.tokenHeaderName = tokenProperties.getHeader().getName();
    this.refreshProperties = tokenProperties.getRefresh();
  }
  
  public Optional<ExternalJwtConsumerData> process(HttpServletRequest request) throws IllegalArgumentException {
    ZonedDateTime currentTime = ExternalJwtUtils.getCurrentUtc();
    if (JsonWebTokenUtils.isHeaderBasedAuth(this.tokenHeaderName, request))
      return process(true, request, currentTime); 
    if (JsonWebTokenUtils.isCookieBasedAuth(this.tokenCookieName, request))
      return process(false, request, currentTime); 
    return Optional.empty();
  }
  
  private Optional<ExternalJwtConsumerData> process(boolean headerBased, HttpServletRequest request, ZonedDateTime currentTime) {
    Optional<ExternalJwt> loadedToken = loadToken(headerBased, request);
    if (!loadedToken.isPresent())
      return Optional.empty(); 
    ExternalJwt externalJwt = loadedToken.get();
    if (externalJwt.isTokenExpired(currentTime)) {
      if (logger.isDebugEnabled())
        logger.debug("Used {} token ID: {}, subject '{}' expired at '{}'", new Object[] { headerBased ? "header" : "cookie", externalJwt
              
              .getClaimsSet().getTokenId().orElse("?"), externalJwt
              .getClaimsSet().getSubject().orElse(null), externalJwt
              .getClaimsSet().getExpirationTime().orElse(null) }); 
      return Optional.empty();
    } 
    try {
      if (externalJwt.isReadyForRenew(currentTime, this.tokenProperties.getRenew()) && notIgnoredRequest(request))
        return renewToken(headerBased, externalJwt, currentTime); 
    } catch (ExternalJwtException |JsonWebTokenException|BlacklistException e) {
      logger.error("Can't renew the {} based token from current authentication", headerBased ? "header" : "cookie", e);
    } 
    ExternalJwtConsumerData externalJwtConsumerData = new ExternalJwtConsumerData((Authentication)new ExternalJwtAuthentication(externalJwt));
    if (headerBased)
      appendCsrfToken(request, externalJwtConsumerData); 
    return Optional.of(externalJwtConsumerData);
  }
  
  private Optional<ExternalJwtConsumerData> renewToken(boolean headerBased, ExternalJwt externalJwt, ZonedDateTime currentTime) throws BlacklistException, ExternalJwtException, JsonWebTokenException {
    ExternalJwtAuthentication externalJwtAuthentication = new ExternalJwtAuthentication(externalJwt);
    if (this.tokenBlacklistService == null || this.tokenBlacklistService.validateAndBlacklistJti(externalJwtAuthentication, "Token refresh request")) {
      if (headerBased)
        return Optional.of(new ExternalJwtConsumerData(renewHeaderToken(externalJwt))); 
      return Optional.of(new ExternalJwtConsumerData(renewCookieToken(externalJwt, currentTime)));
    } 
    return alreadyBlacklisted(externalJwt);
  }
  
  private Optional<ExternalJwt> loadToken(boolean headerBased, HttpServletRequest request) {
    try {
      if (headerBased)
        return ExternalJwtHttpHeader.load(this.tokenConsumer, request, this.tokenHeaderName);
      return ExternalJwtCookie.load(this.tokenConsumer, request, this.tokenCookieName);
    } catch (ExternalJwtException|JsonWebTokenException e) {
      logger.error("Problem loading External Json Web Token from the {} {}", new Object[] { headerBased ? this.tokenHeaderName : this.tokenCookieName, headerBased ? "header" : "cookie", e });
      return Optional.empty();
    } 
  }
  
  private ExternalJwtHttpHeader renewHeaderToken(ExternalJwt externalJwt) throws ExternalJwtException, JsonWebTokenException {
    ExternalJwtHttpHeader renewHeader = ExternalJwtHttpHeader.create().withProducer(this.tokenProducer).withHeaderName(this.tokenHeaderName).withAuthentication((Authentication)new ExternalJwtAuthentication(externalJwt)).build();
    if (logger.isDebugEnabled()) {
      ExternalJwt renewToken = renewHeader.getToken();
      logger.debug("Renew header token ID: {}, subject '{}' before expired at '{}', new expired at: '{}'", new Object[] { renewToken
            .getClaimsSet().getTokenId().orElse("?"), renewToken
            .getClaimsSet().getSubject().orElse(null), externalJwt
            .getClaimsSet().getExpirationTime().orElse(null), renewToken
            .getClaimsSet().getExpirationTime().orElse(null) });
    } 
    return renewHeader;
  }
  
  private ExternalJwtCookie renewCookieToken(ExternalJwt externalJwt, ZonedDateTime currentTime) throws ExternalJwtException, JsonWebTokenException {
    ExternalJwtCookie renewCookie = ExternalJwtCookie.create().withProducer(this.tokenProducer).withCookieName(this.tokenCookieName).withAuthentication((Authentication)new ExternalJwtAuthentication(externalJwt)).withCurrentTime(currentTime).build();
    if (logger.isDebugEnabled()) {
      ExternalJwt renewToken = renewCookie.getToken();
      logger.debug("Renew cookie token ID: {}, subject '{}' before expired at '{}', new expired at: '{}'", new Object[] { renewToken
            .getClaimsSet().getTokenId().orElse("?"), renewToken
            .getClaimsSet().getSubject().orElse(null), externalJwt
            .getClaimsSet().getExpirationTime().orElse(null), renewToken
            .getClaimsSet().getExpirationTime().orElse(null) });
    } 
    return renewCookie;
  }
  
  private void appendCsrfToken(HttpServletRequest httpRequest, ExternalJwtConsumerData data) {
    if (allowedRequestMethod(httpRequest))
      return; 
    Cookie csrfCookie = WebUtils.getCookie(httpRequest, "XSRF-TOKEN");
    if (csrfCookie != null && StringUtils.hasText(csrfCookie.getValue())) {
      String csrfHeader = csrfCookie.getValue();
      data.addDownStreamHeader("X-XSRF-TOKEN", csrfHeader);
      logger.debug("Append static {} header. User is authorized with the Authorization header", "X-XSRF-TOKEN");
    } else {
      data.addDownStreamHeader("X-XSRF-TOKEN", "YXV0aG9yaXphdGlvbi13aXRoLWhlYWRlci1jc3JmLXRva2Vu");
      data.addDownStreamCookie(AUTHORIZATION_CSRF_COOKIE);
      logger.debug("Append static {} header and {} cookie. User is authorized with the Authorization header", "X-XSRF-TOKEN", "XSRF-TOKEN");
    } 
  }
  
  private boolean allowedRequestMethod(HttpServletRequest request) {
    return ALLOWED_METHODS.contains(request.getMethod());
  }
  
  private boolean notIgnoredRequest(HttpServletRequest request) {
    String originalRequest = request.getHeader(this.refreshProperties.getHeaderName());
    if (originalRequest != null && !originalRequest.isEmpty())
      return !this.antPathMatcher.match(this.refreshProperties.getIgnorePattern(), originalRequest); 
    return true;
  }
  
  private Optional<ExternalJwtConsumerData> alreadyBlacklisted(ExternalJwt externalJwt) {
    if (logger.isInfoEnabled())
      logger.info("{} already blacklisted: {}", BlacklistRequest.Type.JTI
          .name(), externalJwt.getClaimsSet().getClaim("jti")); 
    return Optional.empty();
  }
}
