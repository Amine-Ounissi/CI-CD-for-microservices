package com.value.authentication.tokenconverter.service;

import java.net.URL;
import java.util.Map;

public interface JwsVerificationService {
  Map<String, Object> verifyToken(String paramString1, URL paramURL, String paramString2, String paramString3);
}
