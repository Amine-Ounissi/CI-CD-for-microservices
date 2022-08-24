package com.value.buildingblocks.jwt.internal.token;

import com.value.buildingblocks.jwt.core.authenticaton.JsonWebTokenAuthentication;
import com.value.buildingblocks.jwt.core.token.JsonWebTokenClaimsSet;
import com.value.buildingblocks.jwt.internal.InternalJwtMapper;
import com.value.buildingblocks.jwt.internal.InternalJwtTenantMapper;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class InternalJwtClaimsSet implements JsonWebTokenClaimsSet {
  public static final String ISSUER_CLAIM = "iss";
  
  public static final String SUBJECT_CLAIM = "sub";
  
  public static final String SUBJECT_ROLES = "rol";
  
  public static final String SUBJECT_ACCOUNT_NON_EXPIRED = "anexp";
  
  public static final String SUBJECT_ACCOUNT_NON_LOCKED = "anloc";
  
  public static final String SUBJECT_CREDENTIALS_NON_EXPIRED = "cnexp";
  
  public static final String SUBJECT_ENABLED = "enbl";
  
  public static final String CORRELATION_ID = "crlid";
  
  public static final String INTERNAL_USER_ID = "inuid";
  
  public static final String TOKEN_ID_CLAIM = "jti";
  
  public static final String TENANT_ID_CLAIM = "tid";
  
  public static final String TRANSPORT_METHOD_CLAIM = "trans";
  
  public static final String TENANT_ID_HTTP_HEADER = "X-TID";
  
  private static final Logger logger = LoggerFactory.getLogger(InternalJwtClaimsSet.class);
  
  protected Map<String, Object> claims;
  
  public InternalJwtClaimsSet() {
    this(null);
  }
  
  public InternalJwtClaimsSet(Map<String, Object> claims) {
    if (claims == null)
      claims = new HashMap<>(); 
    this.claims = Collections.unmodifiableMap(claims);
  }
  
  public Optional<Object> getClaim(String claimName) {
    return Optional.ofNullable(this.claims.get(claimName));
  }
  
  public Map<String, Object> getClaims() {
    return Collections.unmodifiableMap(this.claims);
  }
  
  public Optional<String> getIssuer() {
    return Optional.ofNullable((String)this.claims.get("iss"));
  }
  
  public Optional<String> getSubject() {
    return Optional.ofNullable((String)this.claims.get("sub"));
  }
  
  public Optional<String> getInternalUserId() {
    return Optional.ofNullable((String)this.claims.get("inuid"));
  }
  
  public Optional<Collection<String>> getRoles() {
    return Optional.ofNullable((Collection<String>)this.claims.get("rol"));
  }

  public Optional<Collection<String>> getAuthorities() {
    return Optional.ofNullable((Collection<String>)this.claims.get("authorities"));
  }
  
  public Optional<Boolean> isAccountNonExpired() {
    return Optional.ofNullable((Boolean)this.claims.get("anexp"));
  }
  
  public Optional<Boolean> isAccountNonLocked() {
    return Optional.ofNullable((Boolean)this.claims.get("anloc"));
  }
  
  public Optional<Boolean> isCredentialsNonExpired() {
    return Optional.ofNullable((Boolean)this.claims.get("cnexp"));
  }
  
  public Optional<Boolean> isAccountEnabled() {
    return Optional.ofNullable((Boolean)this.claims.get("enbl"));
  }
  
  public Optional<String> getCorrelationId() {
    return Optional.ofNullable((String)this.claims.get("crlid"));
  }
  
  public Optional<String> getTokenId() {
    return Optional.ofNullable((String)this.claims.get("jti"));
  }
  
  public Optional<String> getTenantId() {
    return Optional.ofNullable((String)this.claims.get("tid"));
  }
  
  public Optional<String> getTransportMethod() {
    return Optional.ofNullable((String)this.claims.get("trans"));
  }
  
  public String toString() {
    StringBuilder builder = new StringBuilder("InternalJwtClaimsSet{");
    builder.append("claims=").append(this.claims);
    builder.append('}');
    return builder.toString();
  }
  
  public static class Builder {
    private final Map<String, Object> claims = new HashMap<>();
    
    private InternalJwtMapper tokenMapper;
    
    private InternalJwtTenantMapper tenantMapper;
    
    public Builder withTokenMapper(InternalJwtMapper tokenMapper) {
      this.tokenMapper = tokenMapper;
      return this;
    }
    
    public Builder withTenantMapper(InternalJwtTenantMapper tenantMapper) {
      this.tenantMapper = tenantMapper;
      return this;
    }
    
    public Builder loadAuthentication(Authentication authentication) {
      this.claims.put("sub", subjectName(authentication));
      this.claims.put("rol", subjectRoles(authentication));
      subjectAccountNonExpired(authentication)
        .ifPresent(accountNonExpired -> this.claims.put("anexp", accountNonExpired));
      subjectAccountNonLocked(authentication)
        .ifPresent(accountNonLocked -> this.claims.put("anloc", accountNonLocked));
      subjectCredentialsNonExpired(authentication)
        .ifPresent(credentialsNonExpired -> this.claims.put("cnexp", credentialsNonExpired));
      subjectEnabled(authentication)
        .ifPresent(enabled -> this.claims.put("enbl", enabled));
      return this;
    }
    
    public Builder issuer(String issuer) {
      this.claims.put("iss", issuer);
      return this;
    }
    
    public Builder subject(String subject) {
      this.claims.put("sub", subject);
      return this;
    }
    
    public Builder roles(Collection<String> roles) {
      this.claims.put("rol", roles);
      return this;
    }
    
    public Builder subjectAccountNonExpired(boolean accountNonExpired) {
      this.claims.put("anexp", Boolean.valueOf(accountNonExpired));
      return this;
    }
    
    private Optional<Boolean> subjectAccountNonExpired(Authentication authentication) {
      return authenticationUserDetails(authentication).map(UserDetails::isAccountNonExpired);
    }
    
    public Builder subjectAccountNonLocked(boolean accountNonLocked) {
      this.claims.put("anloc", Boolean.valueOf(accountNonLocked));
      return this;
    }
    
    private Optional<Boolean> subjectAccountNonLocked(Authentication authentication) {
      return authenticationUserDetails(authentication).map(UserDetails::isAccountNonLocked);
    }
    
    private Optional<Boolean> subjectCredentialsNonExpired(Authentication authentication) {
      return authenticationUserDetails(authentication).map(UserDetails::isCredentialsNonExpired);
    }
    
    public Builder subjectCredentialsNonExpired(boolean credentialsNonExpired) {
      this.claims.put("cnexp", Boolean.valueOf(credentialsNonExpired));
      return this;
    }
    
    public Builder subjectAcountEnabled(boolean enabled) {
      this.claims.put("enbl", Boolean.valueOf(enabled));
      return this;
    }
    
    public Builder internalUserId(String internalUserId) {
      this.claims.put("inuid", internalUserId);
      return this;
    }
    
    public Builder tokenId(String tokenId) {
      this.claims.put("jti", tokenId);
      return this;
    }
    
    public Builder tenantId(String tenantId) {
      this.claims.put("tid", tenantId);
      return this;
    }
    
    public Builder transportMethod(String transportMethod) {
      this.claims.put("trans", transportMethod);
      return this;
    }
    
    public Builder addClaims(JsonWebTokenClaimsSet... claimsSets) {
      for (JsonWebTokenClaimsSet claimsSet : claimsSets) {
        if (claimsSet != null)
          this.claims.putAll(claimsSet.getClaims()); 
      } 
      return this;
    }
    
    public Builder addClaims(Authentication authentication) {
      if (authentication.getDetails() instanceof JsonWebTokenClaimsSet) {
        JsonWebTokenClaimsSet detailClaims = (JsonWebTokenClaimsSet)authentication.getDetails();
        this.claims.putAll(detailClaims.getClaims());
      } 
      return this;
    }
    
    public Builder addMappedClaims(Authentication authentication, HttpServletRequest request) {
      loadTenantId(authentication, request);
      if (this.tokenMapper != null) {
        Optional<? extends JsonWebTokenClaimsSet> tokenMapperClaimsSet = (Optional)this.tokenMapper.claimSet(authentication, request);
        tokenMapperClaimsSet.ifPresent(tokenClaims -> this.claims.putAll(tokenClaims.getClaims()));
      } 
      return this;
    }
    
    public InternalJwtClaimsSet build() {
      return new InternalJwtClaimsSet(this.claims);
    }
    
    private String subjectName(Authentication authentication) {
      if (this.tokenMapper != null) {
        Optional<String> optionalUserName = this.tokenMapper.getUserName(authentication);
        if (optionalUserName.isPresent())
          return optionalUserName.get(); 
      } 
      return authentication.getName();
    }
    
    private Set<String> subjectRoles(Authentication authentication) {
      Set<String> roles = new HashSet<>();
      for (GrantedAuthority grantedAuthority : authentication.getAuthorities())
        roles.add(grantedAuthority.getAuthority()); 
      if (this.tokenMapper != null)
        this.tokenMapper.updateAuthorities(roles, authentication); 
      return roles;
    }
    
    private Optional<Boolean> subjectEnabled(Authentication authentication) {
      return authenticationUserDetails(authentication).map(UserDetails::isEnabled);
    }
    
    private Optional<UserDetails> authenticationUserDetails(Authentication authentication) {
      Object principal = authentication.getPrincipal();
      if (principal instanceof UserDetails)
        return Optional.of((UserDetails)principal); 
      return Optional.empty();
    }
    
    private void loadTenantId(Authentication authentication, HttpServletRequest request) {
      String tenantId = null;
      if (this.tenantMapper != null) {
        tenantId = this.tenantMapper.tenantId(authentication, request).orElse(null);
        if (InternalJwtClaimsSet.logger.isDebugEnabled() && tenantId != null)
          InternalJwtClaimsSet.logger.debug("Load tenantID: {} from tenant mapper ({})", tenantId, this.tenantMapper
              .getClass().getName()); 
      } 
      if (tenantId == null) {
        Optional<String> authTenantId = getTenantId(authentication);
        tenantId = authTenantId.orElse(null);
        if (InternalJwtClaimsSet.logger.isDebugEnabled() && tenantId != null)
          InternalJwtClaimsSet.logger.debug("Load tenantID: {} from authentication ({})", tenantId, authentication
              .getClass().getName()); 
      } 
      if (tenantId == null) {
        Optional<String> requestTenantId = getTenantId(request);
        tenantId = requestTenantId.orElse(null);
        if (InternalJwtClaimsSet.logger.isDebugEnabled() && tenantId != null)
          InternalJwtClaimsSet.logger.debug("Load tenantID: {} from request ({})", tenantId, request.getRequestURI()); 
      } 
      if (tenantId != null)
        this.claims.put("tid", tenantId); 
    }
    
    private Optional<String> getTenantId(Authentication authentication) {
      if (!(authentication instanceof JsonWebTokenAuthentication))
        return Optional.empty(); 
      JsonWebTokenAuthentication jsonWebTokenAuthentication = (JsonWebTokenAuthentication)authentication;
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
