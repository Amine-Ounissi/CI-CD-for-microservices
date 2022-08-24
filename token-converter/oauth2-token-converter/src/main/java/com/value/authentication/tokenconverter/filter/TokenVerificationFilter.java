package com.value.authentication.tokenconverter.filter;

import com.value.authentication.tokenconverter.exception.NoUserNameException;
import com.value.authentication.tokenconverter.exception.InvalidHeaderException;
import com.value.authentication.tokenconverter.exception.InvalidUrlException;
import com.value.authentication.tokenconverter.exception.NotWhitelistedException;
import com.value.authentication.tokenconverter.mapper.ClaimsStore;
import com.value.authentication.tokenconverter.model.ParsedToken;
import com.value.authentication.tokenconverter.model.Principal;
import com.value.authentication.tokenconverter.config.Oauth2TokenConverterConfig;
import com.value.authentication.tokenconverter.service.JwsVerificationService;
import com.value.authentication.tokenconverter.service.UnverifiedTokenService;
import com.value.authentication.tokenconverter.service.UrlMappingService;
import com.value.authentication.tokenconverter.service.UserInfoService;
import com.value.buildingblocks.multitenancy.TenantContext;
import com.value.buildingblocks.multitenancy.TenantProvider;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.RegexValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class TokenVerificationFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory
    .getLogger(TokenVerificationFilter.class);

  private static final String ISSUER_CLAIM = "iss";

  private static final String TID_CLAIM = "tid";

  private static final String[] SCHEMES = new String[]{"http", "https"};

  private static final String REGEX = "^([\\p{Alnum}\\-\\.]*)(:\\d*)?(.*)?";

  public static final String PREFERRED_USERNAME = "preferred_username";

  private final JwsVerificationService jwsVerificationService;

  private final UnverifiedTokenService unverifiedTokenService;

  private final UserInfoService userInfoService;

  private final Oauth2TokenConverterConfig oauth2TokenConverterConfig;

  private final Map<String, URI> uriMap = new ConcurrentHashMap<>();

  private final UrlValidator urlValidator = new UrlValidator(SCHEMES,
    new RegexValidator("^([\\p{Alnum}\\-\\.]*)(:\\d*)?(.*)?"), 8L);

  private final ClaimsStore claimsStore;

  private final TenantProvider tenantProvider;

  private final BearerTokenExtractor extractor = new BearerTokenExtractor();

  private final UrlMappingService urlMappingService;

  public TokenVerificationFilter(JwsVerificationService jwsVerificationService,
    UnverifiedTokenService unverifiedTokenService, UserInfoService userInfoService,
    Oauth2TokenConverterConfig oauth2TokenConverterConfig, ClaimsStore claimsStore,
    TenantProvider tenantProvider, UrlMappingService urlMappingService) {
    this.jwsVerificationService = jwsVerificationService;
    this.unverifiedTokenService = unverifiedTokenService;
    this.userInfoService = userInfoService;
    this.oauth2TokenConverterConfig = oauth2TokenConverterConfig;
    this.claimsStore = claimsStore;
    this.tenantProvider = tenantProvider;
    this.urlMappingService = urlMappingService;
  }

  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
    FilterChain filterChain) throws ServletException, IOException {
    log.debug("Started token verification");
    if (authorizationHeaderPresent(request)) {
      Authentication interimAuthentication = this.extractor.extract(request);
      if (interimAuthentication == null) {
        log.debug("Authorization header not valid, not attempting verification");
        throw new InvalidHeaderException();
      }
      String token = (String) interimAuthentication.getPrincipal();
      ParsedToken parsedToken = this.unverifiedTokenService.parseJwt(token);
      String tid = (String) parsedToken.getClaims().get("tid");
      TenantContext.clear();
      if (tid != null) {
        this.tenantProvider.findTenantById(tid).ifPresent(TenantContext::setTenant);
      }
      String baseUri = (String) parsedToken.getClaims().get("iss");
      URI uri = createUri(baseUri);
      verifyDomainWhitelisted(uri);
      baseUri = this.urlMappingService.mapToInternalUri(uri).toString();
      Principal principal = new Principal();
      this.claimsStore
        .setClaims(verifyAndRetrieveAdditionalClaims(token, parsedToken, baseUri, principal));
      SecurityContextHolder.getContext()
        .setAuthentication(
          new UsernamePasswordAuthenticationToken(principal.getUserName(), null,
            getGrantedAuthorities(principal.getAuthorities())));
      log.debug("Token verification successful");
    } else {
      log.debug("Token verification skipped, no Authorization header/cookie provided");
    }
    filterChain.doFilter(request, response);
  }

  private Map<String, Object> verifyAndRetrieveAdditionalClaims(String token,
    ParsedToken parsedToken, String baseUri, Principal principal) throws MalformedURLException {
    Map<String, Object> combinedClaims = new HashMap<>();
    combinedClaims.putAll(parsedToken.getClaims());
    log.debug("External token claims :: {}", combinedClaims);
    if (this.oauth2TokenConverterConfig.isForceJwksVerificationGivenMatchingClaims() && combinedClaims
      .entrySet().stream().anyMatch(this::forceJwksEntryMatches)) {
      if (log.isDebugEnabled()) {
        List<Map.Entry<String, Object>> matchingEntries = (List<Map.Entry<String, Object>>) combinedClaims
          .entrySet().stream().filter(this::forceJwksEntryMatches).collect(Collectors.toList());
        log.debug(
          "Token with matching no session verify only claim detected, overriding to JWKS verification.  Matching entries : [{}]",
          matchingEntries);
      }
      verifyByJwks(token, parsedToken, baseUri, principal, "preferred_username");
      return combinedClaims;
    }
    if (this.oauth2TokenConverterConfig.isJwksVerification()) {
      log.debug("Beginning JWKS verification");
      verifyByJwks(token, parsedToken, baseUri, principal,
        this.oauth2TokenConverterConfig.getJwksUsernameClaimName());
    }
    if (this.oauth2TokenConverterConfig.isUserinfoVerification()) {
      log.debug("Beginning userinfo verification");
      combinedClaims.putAll(verifyByUserInfo(token, baseUri, principal));
    }
    return combinedClaims;
  }

  private void verifyDomainWhitelisted(URI uri) {
    if (this.oauth2TokenConverterConfig.getWhitelistedDomains().stream()
      .map(Oauth2TokenConverterConfig.DomainWhitelistEntry::getIssuerHost)
      .noneMatch(uri.getHost()::equals)) {
      throw new NotWhitelistedException(
        "The token issuer domain is not in the configured whitelist. Issuer URL :: " + uri
          .toString());
    }
  }

  private boolean authorizationHeaderPresent(HttpServletRequest request) {
    String authorization = request.getHeader("Authorization");
    return (authorization != null);
  }

  private Map<String, Object> verifyByUserInfo(String token, String baseUri, Principal principal) {
    log.debug("Userinfo request to {}", baseUri);
    Map<String, Object> userInfoClaims = this.userInfoService.verifyToken(token,
      createUri(baseUri.concat(this.oauth2TokenConverterConfig.getUserinfoSuffix())));
    String userInfoUsername = (String) userInfoClaims
      .get(this.oauth2TokenConverterConfig.getUserinfoUsernameClaimName());
    setPrincipalDataFromClaims(principal, userInfoClaims, userInfoUsername);
    log.debug("Userinfo claims successfully retrieved");
    return userInfoClaims;
  }

  private void setPrincipalDataFromClaims(Principal principal, Map<String, Object> claims,
    String claimName) {
    List<String> authorities = (List<String>) claims.get("authorities");
    principal.setUserName(claimName);
    principal.setAuthorities((authorities != null) ? authorities : Collections.emptyList());
    if (StringUtils.isEmpty(principal.getUserName())) {
      log.debug("Unable to retrieve Username.  Claims received :: {}.", claims.keySet());
      throw new NoUserNameException();
    }
  }

  private void verifyByJwks(String token, ParsedToken parsedToken, String baseUri,
    Principal principal, String jwksUsernameClaimName) throws MalformedURLException {
    Map<String, Object> jwksClaims = this.jwsVerificationService.verifyToken(token,
      createUri(baseUri.concat(this.oauth2TokenConverterConfig.getJwksSuffix())).toURL(), parsedToken
        .getAlgorithm(), parsedToken.getKeyId());
    String username = (String) jwksClaims.get(jwksUsernameClaimName);
    setPrincipalDataFromClaims(principal, jwksClaims, username);
  }

  private URI createUri(String uriString) {
    URI uri;
    if (this.uriMap.containsKey(uriString)) {
      return this.uriMap.get(uriString);
    }
    if (!this.urlValidator.isValid(uriString)) {
      throw new InvalidUrlException("The URL is not valid :: " + uriString);
    }
    try {
      uri = URI.create(uriString);
    } catch (IllegalArgumentException ex) {
      throw new InvalidUrlException(ex.getMessage());
    }
    this.uriMap.put(uriString, uri);
    return uri;
  }

  private List<GrantedAuthority> getGrantedAuthorities(List<String> authorities) {
    return authorities.stream()
      .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
      .collect(Collectors.toList());
  }

  private boolean forceJwksEntryMatches(Map.Entry<String, Object> claimEntry) {
    return this.oauth2TokenConverterConfig.getForceJwksMatchingClaimEntries().stream()
      .anyMatch(forceJwksEntry ->
        (Objects.equals(forceJwksEntry.getClaimName(), claimEntry.getKey()) && Objects
          .equals(forceJwksEntry.getClaimValue(), claimEntry.getValue())));
  }
}
