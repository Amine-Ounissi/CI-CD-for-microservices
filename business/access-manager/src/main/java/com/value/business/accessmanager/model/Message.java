package com.value.business.accessmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
  "message"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {

  /**
   * (Required)
   */
  @JsonProperty("id")
  @NotNull
  private String id;

  /**
   * Greetings message
   */
  @JsonProperty("message")
  @Size(max = 255)
  @NotNull
  private String message;

  public Message() {
  }

  public Message(String id, String message) {
    this.id = id;
    this.message = message;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

}