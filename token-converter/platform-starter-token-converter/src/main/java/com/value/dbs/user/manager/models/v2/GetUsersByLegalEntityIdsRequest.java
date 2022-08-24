package com.value.dbs.user.manager.models.v2;

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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"legalEntityIds", "excludeIds", "query", "externalId", "cursor", "from", "size", "sortOrder"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetUsersByLegalEntityIdsRequest implements AdditionalPropertiesAware {
  @JsonProperty("legalEntityIds")
  @JsonDeserialize(as = LinkedHashSet.class)
  @Size(min = 1)
  @Valid
  @NotNull
  private Set<String> legalEntityIds = new LinkedHashSet<>();
  
  @JsonProperty("excludeIds")
  @JsonDeserialize(as = LinkedHashSet.class)
  @Size(min = 0)
  @Valid
  private Set<String> excludeIds = new LinkedHashSet<>();
  
  @JsonProperty("query")
  private String query;
  
  @JsonProperty("externalId")
  private String externalId;
  
  @JsonProperty("cursor")
  @Size(min = 0, max = 330)
  private String cursor;
  
  @JsonProperty("from")
  private Integer from = Integer.valueOf(0);
  
  @JsonProperty("size")
  private Integer size = Integer.valueOf(10);
  
  @JsonProperty("sortOrder")
  @Size(min = 0, max = 255)
  private String sortOrder = "legalEntityId, fullName, id";
  
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, String> additions = new HashMap<>();
  
  @JsonProperty("legalEntityIds")
  public Set<String> getLegalEntityIds() {
    return this.legalEntityIds;
  }
  
  @JsonProperty("legalEntityIds")
  public void setLegalEntityIds(Set<String> legalEntityIds) {
    this.legalEntityIds = legalEntityIds;
  }
  
  public GetUsersByLegalEntityIdsRequest withLegalEntityIds(Set<String> legalEntityIds) {
    this.legalEntityIds = legalEntityIds;
    return this;
  }
  
  @JsonProperty("excludeIds")
  public Set<String> getExcludeIds() {
    return this.excludeIds;
  }
  
  @JsonProperty("excludeIds")
  public void setExcludeIds(Set<String> excludeIds) {
    this.excludeIds = excludeIds;
  }
  
  public GetUsersByLegalEntityIdsRequest withExcludeIds(Set<String> excludeIds) {
    this.excludeIds = excludeIds;
    return this;
  }
  
  @JsonProperty("query")
  public String getQuery() {
    return this.query;
  }
  
  @JsonProperty("query")
  public void setQuery(String query) {
    this.query = query;
  }
  
  public GetUsersByLegalEntityIdsRequest withQuery(String query) {
    this.query = query;
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
  
  public GetUsersByLegalEntityIdsRequest withExternalId(String externalId) {
    this.externalId = externalId;
    return this;
  }
  
  @JsonProperty("cursor")
  public String getCursor() {
    return this.cursor;
  }
  
  @JsonProperty("cursor")
  public void setCursor(String cursor) {
    this.cursor = cursor;
  }
  
  public GetUsersByLegalEntityIdsRequest withCursor(String cursor) {
    this.cursor = cursor;
    return this;
  }
  
  @JsonProperty("from")
  public Integer getFrom() {
    return this.from;
  }
  
  @JsonProperty("from")
  public void setFrom(Integer from) {
    this.from = from;
  }
  
  public GetUsersByLegalEntityIdsRequest withFrom(Integer from) {
    this.from = from;
    return this;
  }
  
  @JsonProperty("size")
  public Integer getSize() {
    return this.size;
  }
  
  @JsonProperty("size")
  public void setSize(Integer size) {
    this.size = size;
  }
  
  public GetUsersByLegalEntityIdsRequest withSize(Integer size) {
    this.size = size;
    return this;
  }
  
  @JsonProperty("sortOrder")
  public String getSortOrder() {
    return this.sortOrder;
  }
  
  @JsonProperty("sortOrder")
  public void setSortOrder(String sortOrder) {
    this.sortOrder = sortOrder;
  }
  
  public GetUsersByLegalEntityIdsRequest withSortOrder(String sortOrder) {
    this.sortOrder = sortOrder;
    return this;
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.legalEntityIds).append(this.excludeIds).append(this.query).append(this.externalId).append(this.cursor).append(this.from).append(this.size).append(this.sortOrder).toHashCode();
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof GetUsersByLegalEntityIdsRequest))
      return false; 
    GetUsersByLegalEntityIdsRequest rhs = (GetUsersByLegalEntityIdsRequest)other;
    return (new EqualsBuilder()).append(this.legalEntityIds, rhs.legalEntityIds).append(this.excludeIds, rhs.excludeIds).append(this.query, rhs.query).append(this.externalId, rhs.externalId).append(this.cursor, rhs.cursor).append(this.from, rhs.from).append(this.size, rhs.size).append(this.sortOrder, rhs.sortOrder).isEquals();
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
