package com.value.platform.edge.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("gateway.request.ignore")
public class RemoveRequestCookieProperties {
  private List<String> cookies = new ArrayList<>(Collections.singletonList("Authorization"));
  
  public List<String> getCookies() {
    return this.cookies;
  }
  
  public void setCookies(List<String> cookies) {
    this.cookies = cookies;
  }
}
