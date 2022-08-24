package com.value.dbs.user.manager.models.v2;

import com.value.buildingblocks.persistence.model.AdditionalPropertiesAware;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"externalId", "legalEntityId", "fullName", "preferredLanguage"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements AdditionalPropertiesAware {
  @JsonProperty("externalId")
  @Pattern(regexp = "^\\S(.*(\\S))?$")
  @Size(min = 1, max = 64)
  @NotNull
  private String externalId;
  
  @JsonProperty("legalEntityId")
  @Pattern(regexp = "^\\S(.*(\\S))?$")
  @Size(min = 1, max = 36)
  @NotNull
  private String legalEntityId;
  
  @JsonProperty("fullName")
  @Pattern(regexp = "^\\S(.*(\\S))?$")
  @Size(min = 1, max = 255)
  @NotNull
  private String fullName;
  
  @JsonProperty("preferredLanguage")
  @Size(min = 2, max = 8)
  private String preferredLanguage;
  
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
  
  public User withExternalId(String externalId) {
    this.externalId = externalId;
    return this;
  }
  
  @JsonProperty("legalEntityId")
  public String getLegalEntityId() {
    return this.legalEntityId;
  }
  
  @JsonProperty("legalEntityId")
  public void setLegalEntityId(String legalEntityId) {
    this.legalEntityId = legalEntityId;
  }
  
  public User withLegalEntityId(String legalEntityId) {
    this.legalEntityId = legalEntityId;
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
  
  public User withFullName(String fullName) {
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
  
  public User withPreferredLanguage(String preferredLanguage) {
    this.preferredLanguage = preferredLanguage;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.externalId).append(this.legalEntityId).append(this.fullName).append(this.preferredLanguage).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof User))
      return false; 
    User rhs = (User)other;
    return (new EqualsBuilder()).append(this.externalId, rhs.externalId).append(this.legalEntityId, rhs.legalEntityId).append(this.fullName, rhs.fullName).append(this.preferredLanguage, rhs.preferredLanguage).isEquals();
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
