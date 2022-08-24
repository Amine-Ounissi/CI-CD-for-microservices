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
@JsonPropertyOrder({"id"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddRealmResponse implements AdditionalPropertiesAware {
  @JsonProperty("id")
  @NotNull
  private String id;
  
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
  
  public AddRealmResponse withId(String id) {
    this.id = id;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.id).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof AddRealmResponse))
      return false; 
    AddRealmResponse rhs = (AddRealmResponse)other;
    return (new EqualsBuilder()).append(this.id, rhs.id).isEquals();
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
