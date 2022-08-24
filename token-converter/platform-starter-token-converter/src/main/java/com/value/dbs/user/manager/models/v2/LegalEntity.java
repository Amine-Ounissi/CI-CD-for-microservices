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
@JsonPropertyOrder({"id", "externalId", "name", "parentId", "isParent"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class LegalEntity implements AdditionalPropertiesAware {
  @JsonProperty("id")
  @Size(min = 1, max = 36)
  @NotNull
  private String id;
  
  @JsonProperty("externalId")
  @Size(min = 1, max = 64)
  @NotNull
  private String externalId;
  
  @JsonProperty("name")
  @Size(min = 1, max = 128)
  @NotNull
  private String name;
  
  @JsonProperty("parentId")
  @Size(max = 36)
  private String parentId;
  
  @JsonProperty("isParent")
  private Boolean isParent = Boolean.valueOf(false);
  
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
  
  public LegalEntity withId(String id) {
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
  
  public LegalEntity withExternalId(String externalId) {
    this.externalId = externalId;
    return this;
  }
  
  @JsonProperty("name")
  public String getName() {
    return this.name;
  }
  
  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }
  
  public LegalEntity withName(String name) {
    this.name = name;
    return this;
  }
  
  @JsonProperty("parentId")
  public String getParentId() {
    return this.parentId;
  }
  
  @JsonProperty("parentId")
  public void setParentId(String parentId) {
    this.parentId = parentId;
  }
  
  public LegalEntity withParentId(String parentId) {
    this.parentId = parentId;
    return this;
  }
  
  @JsonProperty("isParent")
  public Boolean getIsParent() {
    return this.isParent;
  }
  
  @JsonProperty("isParent")
  public void setIsParent(Boolean isParent) {
    this.isParent = isParent;
  }
  
  public LegalEntity withIsParent(Boolean isParent) {
    this.isParent = isParent;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.id).append(this.externalId).append(this.name).append(this.parentId).append(this.isParent).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof LegalEntity))
      return false; 
    LegalEntity rhs = (LegalEntity)other;
    return (new EqualsBuilder()).append(this.id, rhs.id).append(this.externalId, rhs.externalId).append(this.name, rhs.name).append(this.parentId, rhs.parentId).append(this.isParent, rhs.isParent).isEquals();
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
