package com.value.buildingblocks.eureka.loadbalancer;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.support.HttpRequestWrapper;

final class ContextPathHttpRequestWrapper extends HttpRequestWrapper {
  private static final Logger logger = LoggerFactory.getLogger(ContextPathHttpRequestWrapper.class);
  
  private String contextPath;
  
  ContextPathHttpRequestWrapper(HttpRequest request, String contextPath) {
    super(request);
    this.contextPath = contextPath;
  }
  
  public URI getURI() {
    URI uri = super.getURI();
    logger.debug("Original request URL: {}", uri);
    String pathWithContext = (this.contextPath != null) ? this.contextPath.concat(uri.getPath()) : uri.getPath();
    try {
      URIBuilder builder = new URIBuilder();
      builder.setScheme(uri.getScheme());
      builder.setUserInfo(uri.getUserInfo());
      builder.setHost(uri.getHost());
      builder.setPort(uri.getPort());
      builder.setPath(pathWithContext);
      builder.setParameters(URLEncodedUtils.parse(uri.getRawQuery(), StandardCharsets.UTF_8));
      builder.setFragment(uri.getFragment());
      uri = builder.build().normalize();
    } catch (URISyntaxException e) {
      logger.warn("An error occurred while building the load balanced URI with service context: {}. Check the service configuration.", pathWithContext);
    } 
    logger.debug("Transformed request URL: {}", uri);
    return uri;
  }
}
