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
import javax.validation.constraints.NotNull;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"sessionIds"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdentityLogoutRequest implements AdditionalPropertiesAware {
  @JsonProperty("sessionIds")
  @Valid
  @NotNull
  private List<String> sessionIds = new ArrayList<>();
  
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, String> additions = new HashMap<>();
  
  @JsonProperty("sessionIds")
  public List<String> getSessionIds() {
    return this.sessionIds;
  }
  
  @JsonProperty("sessionIds")
  public void setSessionIds(List<String> sessionIds) {
    this.sessionIds = sessionIds;
  }
  
  public IdentityLogoutRequest withSessionIds(List<String> sessionIds) {
    this.sessionIds = sessionIds;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.sessionIds).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof IdentityLogoutRequest))
      return false; 
    IdentityLogoutRequest rhs = (IdentityLogoutRequest)other;
    return (new EqualsBuilder()).append(this.sessionIds, rhs.sessionIds).isEquals();
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
