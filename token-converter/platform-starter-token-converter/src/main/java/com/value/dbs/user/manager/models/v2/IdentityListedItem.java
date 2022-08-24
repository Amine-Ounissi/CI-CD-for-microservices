package com.value.dbs.user.manager.models.v2;

import com.value.buildingblocks.persistence.model.AdditionalPropertiesAware;
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
@JsonPropertyOrder({"id", "externalId", "legalEntityId", "fullName", "legalEntityName"})
public class IdentityListedItem implements AdditionalPropertiesAware {
  @JsonProperty("id")
  @Size(min = 1, max = 36)
  @NotNull
  private String id;
  
  @JsonProperty("externalId")
  @Size(min = 1, max = 64)
  @NotNull
  private String externalId;
  
  @JsonProperty("legalEntityId")
  @Size(min = 1, max = 36)
  @NotNull
  private String legalEntityId;
  
  @JsonProperty("fullName")
  @Size(min = 1, max = 255)
  @NotNull
  private String fullName;
  
  @JsonProperty("legalEntityName")
  @Size(min = 1, max = 128)
  @NotNull
  private String legalEntityName;
  
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, String> additions = new HashMap<>();
  
  @JsonProperty("id")
  public String getId() {
    return this.id;
  }
  
  @JsonProperty("id")
  public void setId(String id) {
    this.id = id;
  }
  
  public IdentityListedItem withId(String id) {
    this.id = id;
    return this;
  }
  
  @JsonProperty("externalId")
  public String getExternalId() {
    return this.externalId;
  }
  
  @JsonProperty("externalId")
  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }
  
  public IdentityListedItem withExternalId(String externalId) {
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
  
  public IdentityListedItem withLegalEntityId(String legalEntityId) {
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
  
  public IdentityListedItem withFullName(String fullName) {
    this.fullName = fullName;
    return this;
  }
  
  @JsonProperty("legalEntityName")
  public String getLegalEntityName() {
    return this.legalEntityName;
  }
  
  @JsonProperty("legalEntityName")
  public void setLegalEntityName(String legalEntityName) {
    this.legalEntityName = legalEntityName;
  }
  
  public IdentityListedItem withLegalEntityName(String legalEntityName) {
    this.legalEntityName = legalEntityName;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.id).append(this.externalId).append(this.legalEntityId).append(this.fullName).append(this.legalEntityName).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof IdentityListedItem))
      return false; 
    IdentityListedItem rhs = (IdentityListedItem)other;
    return (new EqualsBuilder()).append(this.id, rhs.id).append(this.externalId, rhs.externalId).append(this.legalEntityId, rhs.legalEntityId).append(this.fullName, rhs.fullName).append(this.legalEntityName, rhs.legalEntityName).isEquals();
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
