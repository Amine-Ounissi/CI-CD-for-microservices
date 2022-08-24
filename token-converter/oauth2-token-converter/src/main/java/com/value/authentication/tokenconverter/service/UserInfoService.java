package com.value.authentication.tokenconverter.service;

import java.net.URI;
import java.util.Map;

public interface UserInfoService {
  Map<String, Object> verifyToken(String paramString, URI paramURI);
}
