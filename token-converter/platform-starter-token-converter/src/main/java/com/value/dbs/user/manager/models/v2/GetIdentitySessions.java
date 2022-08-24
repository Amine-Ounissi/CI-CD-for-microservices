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
@JsonPropertyOrder({"sessions"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetIdentitySessions implements AdditionalPropertiesAware {
  @JsonProperty("sessions")
  @Valid
  private List<IdentitySession> sessions = new ArrayList<>();
  
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, String> additions = new HashMap<>();
  
  @JsonProperty("sessions")
  public List<IdentitySession> getSessions() {
    return this.sessions;
  }
  
  @JsonProperty("sessions")
  public void setSessions(List<IdentitySession> sessions) {
    this.sessions = sessions;
  }
  
  public GetIdentitySessions withSessions(List<IdentitySession> sessions) {
    this.sessions = sessions;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.sessions).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof GetIdentitySessions))
      return false; 
    GetIdentitySessions rhs = (GetIdentitySessions)other;
    return (new EqualsBuilder()).append(this.sessions, rhs.sessions).isEquals();
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
