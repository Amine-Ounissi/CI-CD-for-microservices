package com.value.dbs.user.manager.models.v2;

import com.value.buildingblocks.persistence.model.AdditionalPropertiesAware;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"totalElements", "identities"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetIdentities implements AdditionalPropertiesAware {
  @JsonProperty("totalElements")
  private Long totalElements;
  
  @JsonProperty("identities")
  @Valid
  private List<IdentityListedItem> identities = new ArrayList<>();
  
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, String> additions = new HashMap<>();
  
  @JsonProperty("totalElements")
  public Long getTotalElements() {
    return this.totalElements;
  }
  
  @JsonProperty("totalElements")
  public void setTotalElements(Long totalElements) {
    this.totalElements = totalElements;
  }
  
  public GetIdentities withTotalElements(Long totalElements) {
    this.totalElements = totalElements;
    return this;
  }
  
  @JsonProperty("identities")
  public List<IdentityListedItem> getIdentities() {
    return this.identities;
  }
  
  @JsonProperty("identities")
  public void setIdentities(List<IdentityListedItem> identities) {
    this.identities = identities;
  }
  
  public GetIdentities withIdentities(List<IdentityListedItem> identities) {
    this.identities = identities;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.totalElements).append(this.identities).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof GetIdentities))
      return false; 
    GetIdentities rhs = (GetIdentities)other;
    return (new EqualsBuilder()).append(this.totalElements, rhs.totalElements).append(this.identities, rhs.identities).isEquals();
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
