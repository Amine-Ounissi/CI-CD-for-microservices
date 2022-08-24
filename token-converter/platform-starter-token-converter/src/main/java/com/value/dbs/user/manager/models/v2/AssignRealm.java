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
@JsonPropertyOrder({"legalEntityId"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssignRealm implements AdditionalPropertiesAware {
  @JsonProperty("legalEntityId")
  @Pattern(regexp = "^\\S+$")
  @Size(min = 1, max = 36)
  @NotNull
  private String legalEntityId;
  
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, String> additions = new HashMap<>();
  
  @JsonProperty("legalEntityId")
  public String getLegalEntityId() {
    return this.legalEntityId;
  }
  
  @JsonProperty("legalEntityId")
  public void setLegalEntityId(String legalEntityId) {
    this.legalEntityId = legalEntityId;
  }
  
  public AssignRealm withLegalEntityId(String legalEntityId) {
    this.legalEntityId = legalEntityId;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.legalEntityId).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof AssignRealm))
      return false; 
    AssignRealm rhs = (AssignRealm)other;
    return (new EqualsBuilder()).append(this.legalEntityId, rhs.legalEntityId).isEquals();
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
