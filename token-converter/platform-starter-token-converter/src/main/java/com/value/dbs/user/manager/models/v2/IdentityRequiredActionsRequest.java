package com.value.dbs.user.manager.models.v2;

import com.value.buildingblocks.persistence.model.AdditionalPropertiesAware;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"actions"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdentityRequiredActionsRequest implements AdditionalPropertiesAware {
  @JsonProperty("actions")
  @Size(min = 0, max = 50)
  @Valid
  private List<String> actions = null;
  
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, String> additions = new HashMap<>();
  
  @JsonProperty("actions")
  public List<String> getActions() {
    return this.actions;
  }
  
  @JsonProperty("actions")
  public void setActions(List<String> actions) {
    this.actions = actions;
  }
  
  public IdentityRequiredActionsRequest withActions(List<String> actions) {
    this.actions = actions;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.actions).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof IdentityRequiredActionsRequest))
      return false; 
    IdentityRequiredActionsRequest rhs = (IdentityRequiredActionsRequest)other;
    return (new EqualsBuilder()).append(this.actions, rhs.actions).isEquals();
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
