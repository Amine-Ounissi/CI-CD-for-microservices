package com.value.authentication.tokenconverter.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.value.authentication.tokenconverter.validator.ValidVerificationMethod;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(
  prefix = "value.oauth2-token-converter"
)
@ValidVerificationMethod(
  message = "Must enable either JWKS or Userinfo validation and its associated claim identifier"
)
public class Oauth2TokenConverterConfig {

  @Valid
  @NotBlank
  private String userinfoSuffix;
  @Valid
  @NotBlank
  private String jwksSuffix;
  @Valid
  @Size(
    min = 1
  )
  private List<Oauth2TokenConverterConfig.DomainWhitelistEntry> whitelistedDomains;
  @Valid
  @Size(
    min = 1
  )
  private List<String> whitelistedAlgorithms;
  private boolean jwksVerification;
  private boolean userinfoVerification;
  private boolean forceJwksVerificationGivenMatchingClaims;
  private List<Oauth2TokenConverterConfig.ForceJwksClaimEntry> forceJwksMatchingClaimEntries = Collections
    .singletonList(new Oauth2TokenConverterConfig.ForceJwksClaimEntry("has_session", "false"));
  private long jwksCacheExpirySeconds = 60L;
  private String userinfoUsernameClaimName = "preferred_username";
  private String jwksUsernameClaimName = "user_name";
  private int userInfoConnectionTimeoutSeconds = 5;
  private int userInfoReadTimeoutSeconds = 5;

  public Oauth2TokenConverterConfig() {
  }

  public String getJwksUsernameClaimName() {
    return this.jwksUsernameClaimName;
  }

  public void setJwksUsernameClaimName(String jwksUsernameClaimName) {
    this.jwksUsernameClaimName = jwksUsernameClaimName;
  }

  public String getUserinfoUsernameClaimName() {
    return this.userinfoUsernameClaimName;
  }

  public void setUserinfoUsernameClaimName(String userinfoUsernameClaimName) {
    this.userinfoUsernameClaimName = userinfoUsernameClaimName;
  }

  public boolean isJwksVerification() {
    return this.jwksVerification;
  }

  public void setJwksVerification(boolean jwksVerification) {
    this.jwksVerification = jwksVerification;
  }

  public boolean isUserinfoVerification() {
    return this.userinfoVerification;
  }

  public void setUserinfoVerification(boolean userinfoVerification) {
    this.userinfoVerification = userinfoVerification;
  }

  public boolean isForceJwksVerificationGivenMatchingClaims() {
    return this.forceJwksVerificationGivenMatchingClaims;
  }

  public void setForceJwksVerificationGivenMatchingClaims(
    boolean forceJwksVerificationGivenMatchingClaims) {
    this.forceJwksVerificationGivenMatchingClaims = forceJwksVerificationGivenMatchingClaims;
  }

  public List<Oauth2TokenConverterConfig.ForceJwksClaimEntry> getForceJwksMatchingClaimEntries() {
    return this.forceJwksMatchingClaimEntries;
  }

  public void setForceJwksMatchingClaimEntries(
    List<Oauth2TokenConverterConfig.ForceJwksClaimEntry> forceJwksMatchingClaimEntries) {
    this.forceJwksMatchingClaimEntries = forceJwksMatchingClaimEntries;
  }

  public String getUserinfoSuffix() {
    return this.userinfoSuffix;
  }

  public void setUserinfoSuffix(String userinfoSuffix) {
    this.userinfoSuffix = userinfoSuffix;
  }

  public String getJwksSuffix() {
    return this.jwksSuffix;
  }

  public void setJwksSuffix(String jwksSuffix) {
    this.jwksSuffix = jwksSuffix;
  }

  public List<String> getWhitelistedAlgorithms() {
    return this.whitelistedAlgorithms;
  }

  public void setWhitelistedAlgorithms(List<String> whitelistedAlgorithms) {
    this.whitelistedAlgorithms = whitelistedAlgorithms;
  }

  public long getJwksCacheExpirySeconds() {
    return this.jwksCacheExpirySeconds;
  }

  public void setJwksCacheExpirySeconds(long jwksCacheExpirySeconds) {
    this.jwksCacheExpirySeconds = jwksCacheExpirySeconds;
  }

  public List<Oauth2TokenConverterConfig.DomainWhitelistEntry> getWhitelistedDomains() {
    return this.whitelistedDomains;
  }

  public void setWhitelistedDomains(
    List<Oauth2TokenConverterConfig.DomainWhitelistEntry> whitelistedDomains) {
    this.whitelistedDomains = whitelistedDomains;
  }

  public void setUserInfoConnectionTimeoutSeconds(int userInfoConnectionTimeoutSeconds) {
    this.userInfoConnectionTimeoutSeconds = userInfoConnectionTimeoutSeconds;
  }

  public int getUserInfoConnectionTimeoutSeconds() {
    return this.userInfoConnectionTimeoutSeconds;
  }

  public void setUserInfoReadTimeoutSeconds(int userInfoReadTimeoutSeconds) {
    this.userInfoReadTimeoutSeconds = userInfoReadTimeoutSeconds;
  }

  public int getUserInfoReadTimeoutSeconds() {
    return this.userInfoReadTimeoutSeconds;
  }

  @Bean
  public Cache<String, JWKSource<SecurityContext>> keyCache() {
    return Caffeine.newBuilder().expireAfterWrite(Duration.ofSeconds(this.jwksCacheExpirySeconds))
      .build();
  }

  public static class ForceJwksClaimEntry {

    private String claimName;
    private String claimValue;

    public ForceJwksClaimEntry(String claimName, String claimValue) {
      this.claimName = claimName;
      this.claimValue = claimValue;
    }

    public ForceJwksClaimEntry() {
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
      return (new ToStringBuilder(this)).append("claimName", this.claimName)
        .append("claimValue", this.claimValue).toString();
    }
  }

  public static class DomainWhitelistEntry {

    @NotNull
    private String issuerHost;
    private String internalHost;
    private String internalHostScheme;

    public DomainWhitelistEntry() {
    }

    public String getIssuerHost() {
      return this.issuerHost;
    }

    public void setIssuerHost(String issuerHost) {
      this.issuerHost = issuerHost;
    }

    public String getInternalHost() {
      return this.internalHost;
    }

    public void setInternalHost(String internalHost) {
      this.internalHost = internalHost;
    }

    public String getInternalHostScheme() {
      return this.internalHostScheme;
    }

    public void setInternalHostScheme(String internalHostScheme) {
      this.internalHostScheme = internalHostScheme;
    }
  }
}
