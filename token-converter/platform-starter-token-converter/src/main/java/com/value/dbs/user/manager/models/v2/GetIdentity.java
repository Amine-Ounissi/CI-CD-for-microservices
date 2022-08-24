package com.value.dbs.user.manager.models.v2;

import com.value.buildingblocks.persistence.model.AdditionalPropertiesAware;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"externalId", "legalEntityInternalId", "fullName", "preferredLanguage", "emailAddress", "mobileNumber", "status", "emailVerified", "createdDate", "requiredActions", "givenName", "familyName", "attributes"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetIdentity implements AdditionalPropertiesAware {
  @JsonProperty("externalId")
  @Pattern(regexp = "^\\S(.*(\\S))?$")
  @Size(min = 1, max = 64)
  @NotNull
  private String externalId;
  
  @JsonProperty("legalEntityInternalId")
  @Pattern(regexp = "^\\S+$")
  @Size(min = 1, max = 36)
  @NotNull
  private String legalEntityInternalId;
  
  @JsonProperty("fullName")
  @Pattern(regexp = "^\\S(.*(\\S))?$")
  @Size(min = 1, max = 255)
  @NotNull
  private String fullName;
  
  @JsonProperty("preferredLanguage")
  @Size(min = 2, max = 8)
  private String preferredLanguage;
  
  @JsonProperty("emailAddress")
  @Size(min = 3, max = 254)
  private String emailAddress;
  
  @JsonProperty("mobileNumber")
  @Size(min = 1, max = 20)
  private String mobileNumber;
  
  @JsonProperty("status")
  private Status status;
  
  @JsonProperty("emailVerified")
  private Boolean emailVerified;
  
  @JsonProperty("createdDate")
  private Date createdDate;
  
  @JsonProperty("requiredActions")
  @Size(min = 0, max = 50)
  @Valid
  private List<String> requiredActions = new ArrayList<>();
  
  @JsonProperty("givenName")
  @Size(min = 1, max = 255)
  @NotNull
  private String givenName;
  
  @JsonProperty("familyName")
  @Size(min = 1, max = 255)
  @NotNull
  private String familyName;
  
  @JsonProperty("attributes")
  @Size(max = 50)
  @Valid
  private Map<String, String> attributes;
  
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, String> additions = new HashMap<>();
  
  @JsonProperty("externalId")
  public String getExternalId() {
    return this.externalId;
  }
  
  @JsonProperty("externalId")
  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }
  
  public GetIdentity withExternalId(String externalId) {
    this.externalId = externalId;
    return this;
  }
  
  @JsonProperty("legalEntityInternalId")
  public String getLegalEntityInternalId() {
    return this.legalEntityInternalId;
  }
  
  @JsonProperty("legalEntityInternalId")
  public void setLegalEntityInternalId(String legalEntityInternalId) {
    this.legalEntityInternalId = legalEntityInternalId;
  }
  
  public GetIdentity withLegalEntityInternalId(String legalEntityInternalId) {
    this.legalEntityInternalId = legalEntityInternalId;
    return this;
  }
  
  @JsonProperty("fullName")
  public String getFullName() {
    return this.fullName;
  }
  
  @JsonProperty("fullName")
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }
  
  public GetIdentity withFullName(String fullName) {
    this.fullName = fullName;
    return this;
  }
  
  @JsonProperty("preferredLanguage")
  public String getPreferredLanguage() {
    return this.preferredLanguage;
  }
  
  @JsonProperty("preferredLanguage")
  public void setPreferredLanguage(String preferredLanguage) {
    this.preferredLanguage = preferredLanguage;
  }
  
  public GetIdentity withPreferredLanguage(String preferredLanguage) {
    this.preferredLanguage = preferredLanguage;
    return this;
  }
  
  @JsonProperty("emailAddress")
  public String getEmailAddress() {
    return this.emailAddress;
  }
  
  @JsonProperty("emailAddress")
  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }
  
  public GetIdentity withEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
    return this;
  }
  
  @JsonProperty("mobileNumber")
  public String getMobileNumber() {
    return this.mobileNumber;
  }
  
  @JsonProperty("mobileNumber")
  public void setMobileNumber(String mobileNumber) {
    this.mobileNumber = mobileNumber;
  }
  
  public GetIdentity withMobileNumber(String mobileNumber) {
    this.mobileNumber = mobileNumber;
    return this;
  }
  
  @JsonProperty("status")
  public Status getStatus() {
    return this.status;
  }
  
  @JsonProperty("status")
  public void setStatus(Status status) {
    this.status = status;
  }
  
  public GetIdentity withStatus(Status status) {
    this.status = status;
    return this;
  }
  
  @JsonProperty("emailVerified")
  public Boolean getEmailVerified() {
    return this.emailVerified;
  }
  
  @JsonProperty("emailVerified")
  public void setEmailVerified(Boolean emailVerified) {
    this.emailVerified = emailVerified;
  }
  
  public GetIdentity withEmailVerified(Boolean emailVerified) {
    this.emailVerified = emailVerified;
    return this;
  }
  
  @JsonProperty("createdDate")
  public Date getCreatedDate() {
    return this.createdDate;
  }
  
  @JsonProperty("createdDate")
  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }
  
  public GetIdentity withCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
    return this;
  }
  
  @JsonProperty("requiredActions")
  public List<String> getRequiredActions() {
    return this.requiredActions;
  }
  
  @JsonProperty("requiredActions")
  public void setRequiredActions(List<String> requiredActions) {
    this.requiredActions = requiredActions;
  }
  
  public GetIdentity withRequiredActions(List<String> requiredActions) {
    this.requiredActions = requiredActions;
    return this;
  }
  
  @JsonProperty("givenName")
  public String getGivenName() {
    return this.givenName;
  }
  
  @JsonProperty("givenName")
  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }
  
  public GetIdentity withGivenName(String givenName) {
    this.givenName = givenName;
    return this;
  }
  
  @JsonProperty("familyName")
  public String getFamilyName() {
    return this.familyName;
  }
  
  @JsonProperty("familyName")
  public void setFamilyName(String familyName) {
    this.familyName = familyName;
  }
  
  public GetIdentity withFamilyName(String familyName) {
    this.familyName = familyName;
    return this;
  }
  
  @JsonProperty("attributes")
  public Map<String, String> getAttributes() {
    return this.attributes;
  }
  
  @JsonProperty("attributes")
  public void setAttributes(Map<String, String> attributes) {
    this.attributes = attributes;
  }
  
  public GetIdentity withAttributes(Map<String, String> attributes) {
    this.attributes = attributes;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.externalId).append(this.legalEntityInternalId).append(this.fullName).append(this.preferredLanguage).append(this.emailAddress).append(this.mobileNumber).append(this.status).append(this.emailVerified).append(this.createdDate).append(this.requiredActions).append(this.givenName).append(this.familyName).append(this.attributes).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof GetIdentity))
      return false; 
    GetIdentity rhs = (GetIdentity)other;
    return (new EqualsBuilder()).append(this.externalId, rhs.externalId).append(this.legalEntityInternalId, rhs.legalEntityInternalId).append(this.fullName, rhs.fullName).append(this.preferredLanguage, rhs.preferredLanguage).append(this.emailAddress, rhs.emailAddress).append(this.mobileNumber, rhs.mobileNumber).append(this.status, rhs.status).append(this.emailVerified, rhs.emailVerified).append(this.createdDate, rhs.createdDate).append(this.requiredActions, rhs.requiredActions).append(this.givenName, rhs.givenName).append(this.familyName, rhs.familyName).append(this.attributes, rhs.attributes).isEquals();
  }
  
  @JsonProperty("additions")
  public Map<String, String> getAdditions() {
    return this.additions;
  }
  
  @JsonProperty("additions")
  public void setAdditions(Map<String, String> additions) {
    this.additions = additions;
  }
  
  public enum Status {
    ENABLED("ENABLED"),
    DISABLED("DISABLED"),
    TEMPORARILY_LOCKED("TEMPORARILY_LOCKED"),
    DORMANT("DORMANT"),
    INACTIVE("INACTIVE"),
    ACCESS_REVOKED("ACCESS_REVOKED");
    
    private final String value;
    
    private static final Map<String, Status> CONSTANTS = new HashMap<>();
    
    static {
      for (Status c : values())
        CONSTANTS.put(c.value, c); 
    }
    
    Status(String value) {
      this.value = value;
    }
    
    @JsonValue
    public String toString() {
      return this.value;
    }
    
    @JsonCreator
    public static Status fromValue(String value) {
      Status constant = CONSTANTS.get(value);
      if (constant == null)
        throw new IllegalArgumentException(value); 
      return constant;
    }
  }
}
