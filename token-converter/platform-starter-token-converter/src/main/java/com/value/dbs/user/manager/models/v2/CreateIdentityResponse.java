package com.value.dbs.user.manager.models.v2;

import com.value.buildingblocks.persistence.model.AdditionalPropertiesAware;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"internalId", "externalId"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateIdentityResponse implements AdditionalPropertiesAware {
  @JsonProperty("internalId")
  @NotNull
  private String internalId;
  
  @JsonProperty("externalId")
  @NotNull
  private String externalId;
  
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, String> additions = new HashMap<>();
  
  @JsonProperty("internalId")
  public String getInternalId() {
    return this.internalId;
  }
  
  @JsonProperty("internalId")
  public void setInternalId(String internalId) {
    this.internalId = internalId;
  }
  
  public CreateIdentityResponse withInternalId(String internalId) {
    this.internalId = internalId;
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
  
  public CreateIdentityResponse withExternalId(String externalId) {
    this.externalId = externalId;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.internalId).append(this.externalId).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof CreateIdentityResponse))
      return false; 
    CreateIdentityResponse rhs = (CreateIdentityResponse)other;
    return (new EqualsBuilder()).append(this.internalId, rhs.internalId).append(this.externalId, rhs.externalId).isEquals();
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
