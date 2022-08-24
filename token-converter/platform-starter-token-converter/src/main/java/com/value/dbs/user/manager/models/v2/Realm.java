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
@JsonPropertyOrder({"id", "realmName"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Realm implements AdditionalPropertiesAware {
  @JsonProperty("id")
  @NotNull
  private String id;
  
  @JsonProperty("realmName")
  @Size(min = 1, max = 255)
  @NotNull
  private String realmName;
  
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
  
  public Realm withId(String id) {
    this.id = id;
    return this;
  }
  
  @JsonProperty("realmName")
  public String getRealmName() {
    return this.realmName;
  }
  
  @JsonProperty("realmName")
  public void setRealmName(String realmName) {
    this.realmName = realmName;
  }
  
  public Realm withRealmName(String realmName) {
    this.realmName = realmName;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.id).append(this.realmName).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof Realm))
      return false; 
    Realm rhs = (Realm)other;
    return (new EqualsBuilder()).append(this.id, rhs.id).append(this.realmName, rhs.realmName).isEquals();
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
