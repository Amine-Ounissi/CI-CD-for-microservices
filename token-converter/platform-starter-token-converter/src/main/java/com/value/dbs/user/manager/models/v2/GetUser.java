package com.value.dbs.user.manager.models.v2;

import com.value.buildingblocks.persistence.model.AdditionalPropertiesAware;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"externalId", "preferredLanguage", "legalEntityId", "id", "fullName"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetUser implements AdditionalPropertiesAware {
  @JsonProperty("externalId")
  @Size(min = 1, max = 64)
  @NotNull
  private String externalId;
  
  @JsonProperty("preferredLanguage")
  @Size(min = 2, max = 8)
  private String preferredLanguage;
  
  @JsonProperty("legalEntityId")
  @Size(min = 1, max = 36)
  @NotNull
  private String legalEntityId;
  
  @JsonProperty("id")
  @Size(min = 1, max = 36)
  @NotNull
  private String id;
  
  @JsonProperty("fullName")
  @Size(min = 1, max = 255)
  @NotNull
  private String fullName;
  
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
  
  public GetUser withExternalId(String externalId) {
    this.externalId = externalId;
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
  
  public GetUser withPreferredLanguage(String preferredLanguage) {
    this.preferredLanguage = preferredLanguage;
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
  
  public GetUser withLegalEntityId(String legalEntityId) {
    this.legalEntityId = legalEntityId;
    return this;
  }
  
  @JsonProperty("id")
  public String getId() {
    return this.id;
  }
  
  @JsonProperty("id")
  public void setId(String id) {
    this.id = id;
  }
  
  public GetUser withId(String id) {
    this.id = id;
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
  
  public GetUser withFullName(String fullName) {
    this.fullName = fullName;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.externalId).append(this.preferredLanguage).append(this.legalEntityId).append(this.id).append(this.fullName).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof GetUser))
      return false; 
    GetUser rhs = (GetUser)other;
    return (new EqualsBuilder()).append(this.externalId, rhs.externalId).append(this.preferredLanguage, rhs.preferredLanguage).append(this.legalEntityId, rhs.legalEntityId).append(this.id, rhs.id).append(this.fullName, rhs.fullName).isEquals();
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
