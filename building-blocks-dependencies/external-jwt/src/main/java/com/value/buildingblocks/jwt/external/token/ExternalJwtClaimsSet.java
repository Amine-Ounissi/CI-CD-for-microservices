package com.value.buildingblocks.jwt.external.token;

import com.value.buildingblocks.jwt.core.authenticaton.JsonWebTokenAuthentication;
import com.value.buildingblocks.jwt.core.token.JsonWebTokenClaimsSet;
import com.value.buildingblocks.jwt.external.ExternalJwtMapper;
import com.value.buildingblocks.jwt.external.ExternalJwtTenantMapper;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class ExternalJwtClaimsSet implements JsonWebTokenClaimsSet {
  public static final String ISSUER_CLAIM = "iss";
  
  public static final String SUBJECT_CLAIM = "sub";
  
  public static final String SUBJECT_ROLES = "rol";
  
  public static final String SUBJECT_ACCOUNT_NON_EXPIRED = "anexp";
  
  public static final String SUBJECT_ACCOUNT_NON_LOCKED = "anloc";
  
  public static final String SUBJECT_CREDENTIALS_NON_EXPIRED = "cnexp";
  
  public static final String SUBJECT_ENABLED = "enbl";
  
  public static final String EXPIRATION_TIME = "exp";
  
  public static final String ISSUED_AT = "iat";
  
  public static final String NOT_BEFORE_CLAIM = "nbf";
  
  public static final String NOT_AFTER_CLAIM = "naf";
  
  public static final String AUDIENCE_CLAIM = "aud";
  
  public static final String TOKEN_ID_CLAIM = "jti";
  
  public static final String TENANT_ID_CLAIM = "tid";
  
  public static final String TENANT_ID_HTTP_HEADER = "X-TID";
  
  private static final Logger logger = LoggerFactory.getLogger(ExternalJwtClaimsSet.class);
  
  protected Map<String, Object> claimsMap;
  
  public ExternalJwtClaimsSet() {
    this(null);
  }
  
  public ExternalJwtClaimsSet(Map<String, Object> providedClaims) {
    if (providedClaims == null)
      providedClaims = new HashMap<>(); 
    this.claimsMap = Collections.unmodifiableMap(providedClaims);
  }
  
  public Optional<Object> getClaim(String claimName) {
    return Optional.ofNullable(this.claimsMap.get(claimName));
  }
  
  public Map<String, Object> getClaims() {
    return Collections.unmodifiableMap(this.claimsMap);
  }
  
  public Optional<String> getIssuer() {
    return Optional.ofNullable((String)this.claimsMap.get("iss"));
  }
  
  public Optional<String> getSubject() {
    return Optional.ofNullable((String)this.claimsMap.get("sub"));
  }
  
  public Optional<Collection<String>> getRoles() {
    return Optional.ofNullable((Collection<String>)this.claimsMap.get("rol"));
  }
  
  public Optional<Boolean> isAccountNonExpired() {
    return Optional.ofNullable((Boolean)this.claimsMap.get("anexp"));
  }
  
  public Optional<Boolean> isAccountNonLocked() {
    return Optional.ofNullable((Boolean)this.claimsMap.get("anloc"));
  }
  
  public Optional<Boolean> isCredentialsNonExpired() {
    return Optional.ofNullable((Boolean)this.claimsMap.get("cnexp"));
  }
  
  public Optional<Boolean> isAccountEnabled() {
    return Optional.ofNullable((Boolean)this.claimsMap.get("enbl"));
  }
  
  public Optional<ZonedDateTime> getExpirationTime() {
    Optional<Instant> timeInstantClaim = ExternalJwtUtils.getTimeInstantClaim(this, "exp");
    return timeInstantClaim.map(instant -> instant.atZone(ZoneOffset.UTC));
  }
  
  public Optional<ZonedDateTime> getIssuedAt() {
    Optional<Instant> timeInstantClaim = ExternalJwtUtils.getTimeInstantClaim(this, "iat");
    return timeInstantClaim.map(instant -> instant.atZone(ZoneOffset.UTC));
  }
  
  public Optional<ZonedDateTime> getNotBeforeTime() {
    Optional<Instant> timeInstantClaim = ExternalJwtUtils.getTimeInstantClaim(this, "nbf");
    return timeInstantClaim.map(instant -> instant.atZone(ZoneOffset.UTC));
  }
  
  public Optional<ZonedDateTime> getNotAfterTime() {
    Optional<Instant> utcInstantClaim = ExternalJwtUtils.getTimeInstantClaim(this, "naf");
    return utcInstantClaim.map(instant -> instant.atZone(ZoneOffset.UTC));
  }
  
  public Optional<List<String>> getAudience() {
    Collection<String> audience = (Collection<String>)this.claimsMap.get("aud");
    if (audience != null)
      return Optional.of(Collections.unmodifiableList(new ArrayList<>(audience))); 
    return Optional.empty();
  }
  
  public Optional<String> getTokenId() {
    return Optional.ofNullable((String)this.claimsMap.get("jti"));
  }
  
  public Optional<String> getTenantId() {
    return Optional.ofNullable((String)this.claimsMap.get("tid"));
  }
  
  public static class Builder {
    private final Map<String, Object> claimsMap = new HashMap<>();
    
    private ExternalJwtMapper tokenMapper;
    
    private ExternalJwtTenantMapper tenantMapper;
    
    public Builder withTokenMapper(ExternalJwtMapper tokenMapper) {
      this.tokenMapper = tokenMapper;
      return this;
    }
    
    public Builder withTenantMapper(ExternalJwtTenantMapper tenantMapper) {
      this.tenantMapper = tenantMapper;
      return this;
    }
    
    public Builder loadAuthentication(Authentication authentication) {
      this.claimsMap.put("sub", subjectName(authentication));
      this.claimsMap.put("rol", subjectRoles(authentication));
      subjectAccountNonExpired(authentication).ifPresent(accountNonExpired -> this.claimsMap.put("anexp", accountNonExpired));
      subjectAccountNonLocked(authentication).ifPresent(accountNonLocked -> this.claimsMap.put("anloc", accountNonLocked));
      subjectCredentialsNonExpired(authentication).ifPresent(credentialsNonExpired -> this.claimsMap.put("cnexp", credentialsNonExpired));
      subjectEnabled(authentication).ifPresent(enabled -> this.claimsMap.put("enbl", enabled));
      return this;
    }
    
    public Builder issuer(String issuer) {
      this.claimsMap.put("iss", issuer);
      return this;
    }
    
    public Builder subject(String subject) {
      this.claimsMap.put("sub", subject);
      return this;
    }
    
    public Builder roles(Collection<String> roles) {
      this.claimsMap.put("rol", roles);
      return this;
    }
    
    public Builder subjectAccountNonExpired(boolean accountNonExpired) {
      this.claimsMap.put("anexp", Boolean.valueOf(accountNonExpired));
      return this;
    }
    
    private Optional<Boolean> subjectAccountNonExpired(Authentication authentication) {
      return authenticationUserDetails(authentication).map(UserDetails::isAccountNonExpired);
    }
    
    public Builder subjectAccountNonLocked(boolean accountNonLocked) {
      this.claimsMap.put("anloc", Boolean.valueOf(accountNonLocked));
      return this;
    }
    
    private Optional<Boolean> subjectAccountNonLocked(Authentication authentication) {
      return authenticationUserDetails(authentication).map(UserDetails::isAccountNonLocked);
    }
    
    public Builder subjectCredentialsNonExpired(boolean credentialsNonExpired) {
      this.claimsMap.put("cnexp", Boolean.valueOf(credentialsNonExpired));
      return this;
    }
    
    private Optional<Boolean> subjectCredentialsNonExpired(Authentication authentication) {
      return authenticationUserDetails(authentication).map(UserDetails::isCredentialsNonExpired);
    }
    
    public Builder subjectAccountEnabled(boolean enabled) {
      this.claimsMap.put("enbl", Boolean.valueOf(enabled));
      return this;
    }
    
    public Builder expirationTime(ZonedDateTime expirationTime) {
      long utcEpochSecond = ExternalJwtUtils.getUtcEpochSecond(expirationTime);
      this.claimsMap.put("exp", Long.valueOf(utcEpochSecond));
      return this;
    }
    
    public Builder issuedAt(ZonedDateTime issuedAt) {
      long utcEpochSecond = ExternalJwtUtils.getUtcEpochSecond(issuedAt);
      this.claimsMap.put("iat", Long.valueOf(utcEpochSecond));
      return this;
    }
    
    public Builder notBeforeTime(ZonedDateTime notBeforeTime) {
      long utcEpochSecond = ExternalJwtUtils.getUtcEpochSecond(notBeforeTime);
      this.claimsMap.put("nbf", Long.valueOf(utcEpochSecond));
      return this;
    }
    
    public Builder notAfterTime(ZonedDateTime notAfterTime) {
      long utcEpochSecond = ExternalJwtUtils.getUtcEpochSecond(notAfterTime);
      this.claimsMap.put("naf", Long.valueOf(utcEpochSecond));
      return this;
    }
    
    public Builder audience(List<String> audience) {
      this.claimsMap.put("aud", Collections.unmodifiableCollection(audience));
      return this;
    }
    
    public Builder tokenId(String tokenId) {
      this.claimsMap.put("jti", tokenId);
      return this;
    }
    
    public Builder tenantId(String tenantId) {
      this.claimsMap.put("tid", tenantId);
      return this;
    }
    
    public Builder addClaims(JsonWebTokenClaimsSet... claimsSets) {
      for (JsonWebTokenClaimsSet claimsSet : claimsSets) {
        if (claimsSet != null)
          this.claimsMap.putAll(claimsSet.getClaims()); 
      } 
      return this;
    }
    
    public Builder addClaims(Authentication authentication) {
      if (authentication.getDetails() instanceof JsonWebTokenClaimsSet) {
        JsonWebTokenClaimsSet detailClaims = (JsonWebTokenClaimsSet)authentication.getDetails();
        this.claimsMap.putAll(detailClaims.getClaims());
      } 
      return this;
    }
    
    public Builder addTimeValidationClaims(int expirationSeconds) {
      ZonedDateTime issuedTime = ExternalJwtUtils.getCurrentUtc();
      ZonedDateTime expirationTime = issuedTime.plusSeconds(expirationSeconds);
      issuedAt(issuedTime);
      expirationTime(expirationTime);
      return this;
    }
    
    public Builder addNotValidAfterIfNotPresent(int notValidAfterSeconds) {
      ZonedDateTime issuedTime;
      Object notAfterClaim = this.claimsMap.get("naf");
      if (notAfterClaim != null)
        return this; 
      Object issuedClaim = this.claimsMap.get("iat");
      if (issuedClaim instanceof ZonedDateTime) {
        issuedTime = (ZonedDateTime)issuedClaim;
      } else {
        issuedTime = ExternalJwtUtils.getCurrentUtc();
      } 
      ZonedDateTime notValidAfterTime = issuedTime.plusSeconds(notValidAfterSeconds);
      notAfterTime(notValidAfterTime);
      return this;
    }
    
    public Builder addMappedClaims(Authentication authentication, HttpServletRequest request) {
      loadTenantId(authentication, request);
      if (this.tokenMapper != null) {
        Optional<? extends JsonWebTokenClaimsSet> tokenMapperClaimsSet = (Optional)this.tokenMapper.claimSet(authentication, request);
        tokenMapperClaimsSet.ifPresent(tokenClaims -> this.claimsMap.putAll(tokenClaims.getClaims()));
      } 
      return this;
    }
    
    public Builder addUniqueTokenId() {
      this.claimsMap.put("jti", UUID.randomUUID().toString());
      return this;
    }
    
    public ExternalJwtClaimsSet build() {
      return new ExternalJwtClaimsSet(this.claimsMap);
    }
    
    private String subjectName(Authentication auth) {
      if (this.tokenMapper != null) {
        Optional<String> optionalUserName = this.tokenMapper.getUserName(auth);
        if (optionalUserName.isPresent())
          return optionalUserName.get(); 
      } 
      return auth.getName();
    }
    
    private Set<String> subjectRoles(Authentication auth) {
      Set<String> roles = new HashSet<>();
      for (GrantedAuthority grantedAuthority : auth.getAuthorities())
        roles.add(grantedAuthority.getAuthority()); 
      if (this.tokenMapper != null)
        this.tokenMapper.updateAuthorities(roles, auth); 
      return roles;
    }
    
    private Optional<Boolean> subjectEnabled(Authentication auth) {
      return authenticationUserDetails(auth).map(UserDetails::isEnabled);
    }
    
    private Optional<UserDetails> authenticationUserDetails(Authentication auth) {
      Object principal = auth.getPrincipal();
      if (principal instanceof UserDetails)
        return Optional.of((UserDetails)principal); 
      return Optional.empty();
    }
    
    private void loadTenantId(Authentication auth, HttpServletRequest request) {
      String tenantId = null;
      if (this.tenantMapper != null) {
        tenantId = this.tenantMapper.tenantId(auth, request).orElse(null);
        if (ExternalJwtClaimsSet.logger.isDebugEnabled() && tenantId != null)
          ExternalJwtClaimsSet.logger.debug("Load tenantID: {} from tenant mapper ({})", tenantId, this.tenantMapper
              .getClass().getName()); 
      } 
      if (tenantId == null) {
        Optional<String> authTenantId = getTenantId(auth);
        tenantId = authTenantId.orElse(null);
        if (ExternalJwtClaimsSet.logger.isDebugEnabled() && tenantId != null)
          ExternalJwtClaimsSet.logger.debug("Load tenantID: {} from authentication ({})", tenantId, auth
              .getClass().getName()); 
      } 
      if (tenantId == null) {
        Optional<String> requestTenantId = getTenantId(request);
        tenantId = requestTenantId.orElse(null);
        if (ExternalJwtClaimsSet.logger.isDebugEnabled() && tenantId != null)
          ExternalJwtClaimsSet.logger.debug("Load tenantID: {} from request ({})", tenantId, request.getRequestURI()); 
      } 
      if (tenantId != null)
        this.claimsMap.put("tid", tenantId); 
    }
    
    private Optional<String> getTenantId(Authentication auth) {
      if (!(auth instanceof JsonWebTokenAuthentication))
        return Optional.empty(); 
      JsonWebTokenAuthentication jsonWebTokenAuthentication = (JsonWebTokenAuthentication)auth;
      JsonWebTokenClaimsSet details = jsonWebTokenAuthentication.getDetails();
      if (details == null)
        return Optional.empty(); 
      return details.getClaim("tid").map(Object::toString);
    }
    
    private Optional<String> getTenantId(HttpServletRequest request) {
      if (request == null)
        return Optional.empty(); 
      return Optional.ofNullable(request.getHeader("X-TID"));
    }
  }
}
