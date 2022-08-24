package com.value.authentication.tokenconverter.service.api.rest.spec.v2.users;

import com.value.buildingblocks.persistence.model.AdditionalPropertiesAware;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"ids"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserListByExternalidsPostRequestBody implements AdditionalPropertiesAware {
  @JsonProperty("ids")
  @JsonDeserialize(as = LinkedHashSet.class)
  @Size(min = 1)
  @Valid
  private Set<String> ids = new LinkedHashSet<>();
  
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, String> additions = new HashMap<>();
  
  @JsonProperty("ids")
  public Set<String> getIds() {
    return this.ids;
  }
  
  @JsonProperty("ids")
  public void setIds(Set<String> ids) {
    this.ids = ids;
  }
  
  public UserListByExternalidsPostRequestBody withIds(Set<String> ids) {
    this.ids = ids;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.ids).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof UserListByExternalidsPostRequestBody))
      return false; 
    UserListByExternalidsPostRequestBody rhs = (UserListByExternalidsPostRequestBody)other;
    return (new EqualsBuilder()).append(this.ids, rhs.ids).isEquals();
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
