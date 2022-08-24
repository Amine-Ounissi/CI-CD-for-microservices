package com.value.buildingblocks.backend.communication.http;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class InterServiceLoggingInterceptor implements ClientHttpRequestInterceptor {
  private static final Logger log = LoggerFactory.getLogger(InterServiceLoggingInterceptor.class);
  
  private String requestFormat = "request: {} headers=[{}] payload=[{}]";
  
  private String responseFormat = "response: {} headers=[{}] payload=[{}]";
  
  private boolean includeHeaders = true;
  
  private boolean includePayload = true;
  
  private boolean includeRequestUri = true;
  
  private boolean includeResponseStatus = true;
  
  private String excludedKeyword = "omitted";
  
  private int maxPayloadLength = -1;
  
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    if (log.isDebugEnabled()) {
      String requestUri = this.includeRequestUri ? (request.getMethod() + " " + request.getURI()) : this.excludedKeyword;
      logMessage(this.requestFormat, requestUri, request.getHeaders(), body);
      ClientHttpResponse response = execution.execute(request, body);
      String responseStatus = this.includeResponseStatus ? (response.getStatusCode() + " " + response.getStatusText()) : this.excludedKeyword;
      byte[] payload = null;
      if (this.includePayload && response.getClass().toString().endsWith("BufferingClientHttpResponseWrapper")) {
        InputStream bodyInput = response.getBody();
        payload = new byte[bodyInput.available()];
        bodyInput.read(payload, 0, bodyInput.available());
      } 
      logMessage(this.responseFormat, responseStatus, response.getHeaders(), payload);
      return response;
    } 
    return execution.execute(request, body);
  }
  
  private void logMessage(String format, String summary, HttpHeaders headers, byte[] payload) {
    String headersMsg = this.includeHeaders ? headers.toString() : this.excludedKeyword;
    String payloadMsg = this.excludedKeyword;
    if (this.includePayload && payload != null)
      if (this.maxPayloadLength < 0) {
        payloadMsg = new String(payload, findCharset(headers));
      } else {
        int length = Math.min(payload.length, this.maxPayloadLength);
        payloadMsg = new String(payload, 0, length, findCharset(headers));
      }  
    log.debug(format, new Object[] { summary, headersMsg, payloadMsg });
  }
  
  private Charset findCharset(HttpHeaders headers) {
    try {
      MediaType contentType = headers.getContentType();
      return (contentType != null && contentType.getCharset() != null) ? contentType
        .getCharset() : StandardCharsets.UTF_8;
    } catch (Exception e) {
      return StandardCharsets.UTF_8;
    } 
  }
  
  public boolean isIncludeHeaders() {
    return this.includeHeaders;
  }
  
  public void setIncludeHeaders(boolean includeHeaders) {
    this.includeHeaders = includeHeaders;
  }
  
  public boolean isIncludePayload() {
    return this.includePayload;
  }
  
  public void setIncludePayload(boolean includePayload) {
    this.includePayload = includePayload;
  }
  
  public boolean isIncludeRequestUri() {
    return this.includeRequestUri;
  }
  
  public void setIncludeRequestUri(boolean includeRequestUri) {
    this.includeRequestUri = includeRequestUri;
  }
  
  public boolean isIncludeResponseStatus() {
    return this.includeResponseStatus;
  }
  
  public void setIncludeResponseStatus(boolean includeResponseStatus) {
    this.includeResponseStatus = includeResponseStatus;
  }
  
  public String getRequestFormat() {
    return this.requestFormat;
  }
  
  public void setRequestFormat(String requestFormat) {
    this.requestFormat = requestFormat;
  }
  
  public String getResponseFormat() {
    return this.responseFormat;
  }
  
  public void setResponseFormat(String responseFormat) {
    this.responseFormat = responseFormat;
  }
  
  public String getExcludedKeyword() {
    return this.excludedKeyword;
  }
  
  public void setExcludedKeyword(String excludedKeyword) {
    this.excludedKeyword = excludedKeyword;
  }
  
  public int getMaxPayloadLength() {
    return this.maxPayloadLength;
  }
  
  public void setMaxPayloadLength(int maxPayloadLength) {
    this.maxPayloadLength = maxPayloadLength;
  }
}
