package com.value.authentication.tokenconverter.verifyonly;

import java.util.Map;
import javax.servlet.http.HttpServletResponse;

public interface VerifyOnlyService {
  boolean isVerifyOnlyToken(Map<String, Object> paramMap);
  
  void addVerifyOnlyHeader(HttpServletResponse paramHttpServletResponse);
}
