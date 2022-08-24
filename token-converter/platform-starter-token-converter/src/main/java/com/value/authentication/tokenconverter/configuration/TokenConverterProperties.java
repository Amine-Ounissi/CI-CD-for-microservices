package com.value.authentication.tokenconverter.configuration;

import com.value.authentication.tokenconverter.crypto.JsonWebAlgorithm;
import com.value.authentication.tokenconverter.utils.ValidSignature;
import com.value.authentication.tokenconverter.utils.ValidValueValidation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "value.token-converter")
@ValidSignature(message = "Incorrect signature setting specified")
@ValidValueValidation(message = "If claim value validation is enabled, deny list entries must be populated and have both claim name and value")
public class TokenConverterProperties {

  private static final String AUTHORIZATION_REQUEST_HEADER_NAME = "Authorization";

  private static final String AUTHORIZATION_RESPONSE_HEADER_NAME = "Authorization";

  private static final String AUTHORIZATION_COOKIE_NAME = "Authorization";

  private static final String VDS_CLIENT_ID = "vds-client";

  private static final String VDS_SECRET = "vds-secret";

  private static final Integer TOKEN_TIMEOUT = Integer.valueOf(300);

  private String downstreamPrefix = "";

  private String upstreamPrefix = "";

  private String authorizationRequestHeaderName = "Authorization";

  private String authorizationResponseHeaderName = "Authorization";

  private String authorizationCookieName = "Authorization";

  private String clientId = "vds-client";

  private String secret = "vds-secret";

  private String actuatorClientId = "vds-actuator";

  private String actuatorSecret = "vds-actuator";

  private Integer tokenTimeout = TOKEN_TIMEOUT;

  private Boolean enableCookieFilter = Boolean.valueOf(true);

  private List<ClaimMapping> claimsMapping = new ArrayList<>();

  private Jwt jwt = new Jwt();

  private Dbs dbs = new Dbs();

  private Cache cache = new Cache();

  private ClaimValueValidation claimValueValidation = new ClaimValueValidation();

  private boolean verifyOnlyNoInternalToken = false;

  private List<VerifyOnlyEntry> verifyOnlyEntries = Collections
    .singletonList(new VerifyOnlyEntry("verify_only", "1"));

  public boolean isVerifyOnlyNoInternalToken() {
    return this.verifyOnlyNoInternalToken;
  }

  public void setVerifyOnlyNoInternalToken(boolean verifyOnlyNoInternalToken) {
    this.verifyOnlyNoInternalToken = verifyOnlyNoInternalToken;
  }

  public List<VerifyOnlyEntry> getVerifyOnlyEntries() {
    return this.verifyOnlyEntries;
  }

  public void setVerifyOnlyEntries(List<VerifyOnlyEntry> verifyOnlyEntries) {
    this.verifyOnlyEntries = verifyOnlyEntries;
  }

  public ClaimValueValidation getClaimValueValidation() {
    return this.claimValueValidation;
  }

  public Dbs getDbs() {
    return this.dbs;
  }

  public Cache getCache() {
    return this.cache;
  }

  public Jwt getJwt() {
    return this.jwt;
  }

  public String getDownstreamPrefix() {
    return this.downstreamPrefix;
  }

  public void setDownstreamPrefix(String downstreamPrefix) {
    this.downstreamPrefix = downstreamPrefix;
  }

  public String getUpstreamPrefix() {
    return this.upstreamPrefix;
  }

  public void setUpstreamPrefix(String upstreamPrefix) {
    this.upstreamPrefix = upstreamPrefix;
  }

  public String getAuthorizationRequestHeaderName() {
    return this.authorizationRequestHeaderName;
  }

  public void setAuthorizationRequestHeaderName(String authorizationRequestHeaderName) {
    this.authorizationRequestHeaderName = authorizationRequestHeaderName;
  }

  public String getAuthorizationResponseHeaderName() {
    return this.authorizationResponseHeaderName;
  }

  public void setAuthorizationResponseHeaderName(String authorizationResponseHeaderName) {
    this.authorizationResponseHeaderName = authorizationResponseHeaderName;
  }

  public String getAuthorizationCookieName() {
    return this.authorizationCookieName;
  }

  public void setAuthorizationCookieName(String authorizationCookieName) {
    this.authorizationCookieName = authorizationCookieName;
  }

  public String getClientId() {
    return this.clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getSecret() {
    return this.secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public String getActuatorClientId() {
    return actuatorClientId;
  }

  public void setActuatorClientId(String actuatorClientId) {
    this.actuatorClientId = actuatorClientId;
  }

  public String getActuatorSecret() {
    return actuatorSecret;
  }

  public void setActuatorSecret(String actuatorSecret) {
    this.actuatorSecret = actuatorSecret;
  }

  public Integer getTokenTimeout() {
    return this.tokenTimeout;
  }

  public void setTokenTimeout(Integer timeout) {
    this.tokenTimeout = timeout;
  }

  public List<ClaimMapping> getClaimsMapping() {
    return this.claimsMapping;
  }

  public void setClaimsMapping(List<ClaimMapping> claimsMapping) {
    this.claimsMapping = claimsMapping;
  }

  public Boolean getEnableCookieFilter() {
    return this.enableCookieFilter;
  }

  public void setEnableCookieFilter(Boolean enableCookieFilter) {
    this.enableCookieFilter = enableCookieFilter;
  }

  public static class Jwt {

    @NotNull
    @Deprecated
    @Pattern(regexp = "symmetric-key|asymmetric-key-pair", flags = {Pattern.Flag.CASE_INSENSITIVE})
    private String signatureMode;

    private JsonWebAlgorithm signatureAlgorithm;

    @Deprecated
    private boolean signingKeyBase64Encoded;

    @Deprecated
    private String signingKey = null;

    private KeyPair keyPair = new KeyPair();

    public JsonWebAlgorithm getSignatureAlgorithm() {
      return this.signatureAlgorithm;
    }

    public Jwt setSignatureAlgorithm(JsonWebAlgorithm signatureAlgorithm) {
      this.signatureAlgorithm = signatureAlgorithm;
      return this;
    }

    public boolean isSigningKeyBase64Encoded() {
      return this.signingKeyBase64Encoded;
    }

    public Jwt setSigningKeyBase64Encoded(boolean signingKeyBase64Encoded) {
      this.signingKeyBase64Encoded = signingKeyBase64Encoded;
      return this;
    }

    public KeyPair getKeyPair() {
      return this.keyPair;
    }

    public String getSignatureMode() {
      return this.signatureMode;
    }

    public void setSignatureMode(String signatureMode) {
      this.signatureMode = signatureMode;
    }

    public String getSigningKey() {
      return this.signingKey;
    }

    public void setSigningKey(String signingKey) {
      this.signingKey = signingKey;
    }

    public static class KeyPair {

      private String location = null;

      private String alias = null;

      private String password = null;

      private String aliasPassword = null;

      public String getLocation() {
        return this.location;
      }

      public void setLocation(String location) {
        this.location = location;
      }

      public String getAlias() {
        return this.alias;
      }

      public void setAlias(String alias) {
        this.alias = alias;
      }

      public String getPassword() {
        return this.password;
      }

      public void setPassword(String password) {
        this.password = password;
      }

      public String getAliasPassword() {
        return this.aliasPassword;
      }

      public void setAliasPassword(String aliasPassword) {
        this.aliasPassword = aliasPassword;
      }
    }
  }

  public static class ClaimMapping {

    private String external;

    private String internal;

    private boolean required;

    public ClaimMapping() {
    }

    public ClaimMapping(String external, String internal, boolean required) {
      this.external = external;
      this.internal = internal;
      this.required = required;
    }

    public String getExternal() {
      return this.external;
    }

    public void setExternal(String external) {
      this.external = external;
    }

    public String getInternal() {
      return this.internal;
    }

    public void setInternal(String internal) {
      this.internal = internal;
    }

    public boolean isRequired() {
      return this.required;
    }

    public void setRequired(boolean required) {
      this.required = required;
    }
  }

  public static class Dbs {

    private boolean propagationEnabled = false;

    private String cacheSpecs;

    private String ttl = "1h";

    private int maximumSize = 10000;

    public boolean isPropagationEnabled() {
      return this.propagationEnabled;
    }

    public Dbs setPropagationEnabled(boolean propagationEnabled) {
      this.propagationEnabled = propagationEnabled;
      return this;
    }

    public String getCacheSpecs() {
      return this.cacheSpecs;
    }

    public Dbs setCacheSpecs(String cacheSpecs) {
      this.cacheSpecs = cacheSpecs;
      return this;
    }

    public String getTtl() {
      return this.ttl;
    }

    public Dbs setTtl(String ttl) {
      this.ttl = ttl;
      return this;
    }

    public int getMaximumSize() {
      return this.maximumSize;
    }

    public Dbs setMaximumSize(int maximumSize) {
      this.maximumSize = maximumSize;
      return this;
    }
  }

  public static class Cache {

    private boolean enabled = false;

    private String cacheSpecs;

    private String ttl = "3s";

    private int maximumSize = 1000;

    public boolean isEnabled() {
      return this.enabled;
    }

    public Cache setEnabled(boolean enabled) {
      this.enabled = enabled;
      return this;
    }

    public String getCacheSpecs() {
      return this.cacheSpecs;
    }

    public Cache setCacheSpecs(String cacheSpecs) {
      this.cacheSpecs = cacheSpecs;
      return this;
    }

    public String getTtl() {
      return this.ttl;
    }

    public Cache setTtl(String ttl) {
      this.ttl = ttl;
      return this;
    }

    public int getMaximumSize() {
      return this.maximumSize;
    }

    public Cache setMaximumSize(int maximumSize) {
      this.maximumSize = maximumSize;
      return this;
    }
  }

  public static class ClaimValueValidation {

    private boolean enabled = false;

    private List<ClaimDenyEntry> claimDenyEntries;

    public boolean isEnabled() {
      return this.enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public List<ClaimDenyEntry> getClaimDenyEntries() {
      return this.claimDenyEntries;
    }

    public void setClaimDenyEntries(List<ClaimDenyEntry> claimDenyEntries) {
      this.claimDenyEntries = claimDenyEntries;
    }

    public static class ClaimDenyEntry {

      private String claimName;

      private List<String> claimValues;

      public String getClaimName() {
        return this.claimName;
      }

      public void setClaimName(String claimName) {
        this.claimName = claimName;
      }

      public List<String> getClaimValues() {
        return this.claimValues;
      }

      public void setClaimValues(List<String> claimValues) {
        this.claimValues = claimValues;
      }

      public boolean isValid() {
        return (StringUtils.isNotBlank(this.claimName) && !CollectionUtils
          .isEmpty(this.claimValues));
      }

      public String toString() {
        return (new ToStringBuilder(this))
          .append("claimName", this.claimName)
          .append("claimValues", this.claimValues)
          .toString();
      }
    }
  }

  public static class VerifyOnlyEntry {

    private String claimName;

    private String claimValue;

    public VerifyOnlyEntry(String claimName, String claimValue) {
      this.claimName = claimName;
      this.claimValue = claimValue;
    }

    public VerifyOnlyEntry() {
    }

    public String getClaimName() {
      return this.claimName;
    }

    public void setClaimName(String claimName) {
      this.claimName = claimName;
    }

    public String getClaimValue() {
      return this.claimValue;
    }

    public void setClaimValue(String claimValue) {
      this.claimValue = claimValue;
    }

    public String toString() {
      return (new ToStringBuilder(this))
        .append("claimName", this.claimName)
        .append("claimValue", this.claimValue)
        .toString();
    }
  }
}
