package com.value.dbs.user.manager.models.v2;

import com.value.buildingblocks.persistence.model.AdditionalPropertiesAware;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "ipAddress", "start", "lastAccess", "client"})
public class IdentitySession implements AdditionalPropertiesAware {
  @JsonProperty("id")
  private String id;
  
  @JsonProperty("ipAddress")
  private String ipAddress;
  
  @JsonProperty("start")
  private Date start;
  
  @JsonProperty("lastAccess")
  private Date lastAccess;
  
  @JsonProperty("client")
  private String client;
  
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
  
  public IdentitySession withId(String id) {
    this.id = id;
    return this;
  }
  
  @JsonProperty("ipAddress")
  public String getIpAddress() {
    return this.ipAddress;
  }
  
  @JsonProperty("ipAddress")
  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }
  
  public IdentitySession withIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
    return this;
  }
  
  @JsonProperty("start")
  public Date getStart() {
    return this.start;
  }
  
  @JsonProperty("start")
  public void setStart(Date start) {
    this.start = start;
  }
  
  public IdentitySession withStart(Date start) {
    this.start = start;
    return this;
  }
  
  @JsonProperty("lastAccess")
  public Date getLastAccess() {
    return this.lastAccess;
  }
  
  @JsonProperty("lastAccess")
  public void setLastAccess(Date lastAccess) {
    this.lastAccess = lastAccess;
  }
  
  public IdentitySession withLastAccess(Date lastAccess) {
    this.lastAccess = lastAccess;
    return this;
  }
  
  @JsonProperty("client")
  public String getClient() {
    return this.client;
  }
  
  @JsonProperty("client")
  public void setClient(String client) {
    this.client = client;
  }
  
  public IdentitySession withClient(String client) {
    this.client = client;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.id).append(this.ipAddress).append(this.start).append(this.lastAccess).append(this.client).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof IdentitySession))
      return false; 
    IdentitySession rhs = (IdentitySession)other;
    return (new EqualsBuilder()).append(this.id, rhs.id).append(this.ipAddress, rhs.ipAddress).append(this.start, rhs.start).append(this.lastAccess, rhs.lastAccess).append(this.client, rhs.client).isEquals();
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
