package com.value.dbs.user.manager.models.v2;

import com.value.buildingblocks.persistence.model.AdditionalPropertiesAware;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"externalId", "legalEntityInternalId", "fullName", "emailAddress", "mobileNumber", "givenName", "familyName", "attributes"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateIdentityRequest implements AdditionalPropertiesAware {
  @JsonProperty("externalId")
  @Pattern(regexp = "^\\S(.*(\\S))?$")
  @Size(min = 1, max = 64)
  private String externalId;
  
  @JsonProperty("legalEntityInternalId")
  @Pattern(regexp = "^\\S+$")
  @Size(min = 1, max = 36)
  @NotNull
  private String legalEntityInternalId;
  
  @JsonProperty("fullName")
  @Pattern(regexp = "^\\S(.*(\\S))?$")
  @Size(min = 1, max = 255)
  private String fullName;
  
  @JsonProperty("emailAddress")
  @Size(min = 3, max = 254)
  @NotNull
  private String emailAddress;
  
  @JsonProperty("mobileNumber")
  @Size(min = 1, max = 20)
  private String mobileNumber;
  
  @JsonProperty("givenName")
  @Size(min = 1, max = 255)
  private String givenName;
  
  @JsonProperty("familyName")
  @Size(min = 1, max = 255)
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
  
  public CreateIdentityRequest withExternalId(String externalId) {
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
  
  public CreateIdentityRequest withLegalEntityInternalId(String legalEntityInternalId) {
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
  
  public CreateIdentityRequest withFullName(String fullName) {
    this.fullName = fullName;
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
  
  public CreateIdentityRequest withEmailAddress(String emailAddress) {
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
  
  public CreateIdentityRequest withMobileNumber(String mobileNumber) {
    this.mobileNumber = mobileNumber;
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
  
  public CreateIdentityRequest withGivenName(String givenName) {
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
  
  public CreateIdentityRequest withFamilyName(String familyName) {
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
  
  public CreateIdentityRequest withAttributes(Map<String, String> attributes) {
    this.attributes = attributes;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.externalId).append(this.legalEntityInternalId).append(this.fullName).append(this.emailAddress).append(this.mobileNumber).append(this.givenName).append(this.familyName).append(this.attributes).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof CreateIdentityRequest))
      return false; 
    CreateIdentityRequest rhs = (CreateIdentityRequest)other;
    return (new EqualsBuilder()).append(this.externalId, rhs.externalId).append(this.legalEntityInternalId, rhs.legalEntityInternalId).append(this.fullName, rhs.fullName).append(this.emailAddress, rhs.emailAddress).append(this.mobileNumber, rhs.mobileNumber).append(this.givenName, rhs.givenName).append(this.familyName, rhs.familyName).append(this.attributes, rhs.attributes).isEquals();
  }
  
  @JsonProperty("additions")
  public Map<String, String> getAdditions() {
    return this.additions;
  }
  
  @JsonProperty("additions")
  public void setAdditions(Map<String, String> additions) {
    this.additions = additions;
  }
}
