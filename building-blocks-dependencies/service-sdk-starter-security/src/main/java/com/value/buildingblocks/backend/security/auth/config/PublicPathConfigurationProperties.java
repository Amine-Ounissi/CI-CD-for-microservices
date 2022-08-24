package com.value.buildingblocks.backend.security.auth.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "value.security.public")
public class PublicPathConfigurationProperties {

  public static final String DEFAULT_PUBLIC_API_PATH = "/public-api/**";

  private List<String> paths = new ArrayList<>();

  public PublicPathConfigurationProperties() {
    this.paths.add(DEFAULT_PUBLIC_API_PATH);
  }

  public List<String> getPaths() {
    return this.paths;
  }

  public void setPaths(List<String> publicPaths) {
    this.paths = publicPaths;
  }
}
