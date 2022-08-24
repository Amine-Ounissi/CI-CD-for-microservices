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
@JsonPropertyOrder({"userUpdate", "externalId"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchUser implements AdditionalPropertiesAware {
  @JsonProperty("userUpdate")
  @Valid
  @NotNull
  private User userUpdate;
  
  @JsonProperty("externalId")
  @Pattern(regexp = "^\\S(.*(\\S))?$")
  @Size(min = 1, max = 64)
  @NotNull
  private String externalId;
  
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, String> additions = new HashMap<>();
  
  @JsonProperty("userUpdate")
  public User getUserUpdate() {
    return this.userUpdate;
  }
  
  @JsonProperty("userUpdate")
  public void setUserUpdate(User userUpdate) {
    this.userUpdate = userUpdate;
  }
  
  public BatchUser withUserUpdate(User userUpdate) {
    this.userUpdate = userUpdate;
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
  
  public BatchUser withExternalId(String externalId) {
    this.externalId = externalId;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.userUpdate).append(this.externalId).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof BatchUser))
      return false; 
    BatchUser rhs = (BatchUser)other;
    return (new EqualsBuilder()).append(this.userUpdate, rhs.userUpdate).append(this.externalId, rhs.externalId).isEquals();
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
