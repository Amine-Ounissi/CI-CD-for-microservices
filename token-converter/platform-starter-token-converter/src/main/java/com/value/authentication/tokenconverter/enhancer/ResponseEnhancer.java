package com.value.authentication.tokenconverter.enhancer;

import java.util.List;
import java.util.Map;
import javax.servlet.http.Cookie;

public interface ResponseEnhancer {
  Map<String, String> getHeaders();
  
  List<Cookie> getCookies();
}
