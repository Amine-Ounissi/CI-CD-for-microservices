package com.value.authentication.tokenconverter.verifyonly.impl;

import com.value.authentication.tokenconverter.configuration.TokenConverterProperties;
import com.value.authentication.tokenconverter.verifyonly.VerifyOnlyService;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class VerifyOnlyServiceImpl implements VerifyOnlyService {
  @Generated
  private static final Logger log = LoggerFactory.getLogger(VerifyOnlyServiceImpl.class);
  
  private final TokenConverterProperties properties;
  
  public VerifyOnlyServiceImpl(TokenConverterProperties properties) {
    this.properties = properties;
  }
  
  public boolean isVerifyOnlyToken(Map<String, Object> claims) {
    if (this.properties.isVerifyOnlyNoInternalToken() && claims
      .entrySet().stream().anyMatch(this::verifyOnlyEntryHasMatchingClaim)) {
      if (log.isDebugEnabled()) {
        List<Map.Entry<String, Object>> matchingEntries = (List<Map.Entry<String, Object>>)claims.entrySet().stream().filter(this::verifyOnlyEntryHasMatchingClaim).collect(Collectors.toList());
        log.debug("Token determined to be verify only as contains matching claims [{}]", matchingEntries);
      } 
      return true;
    } 
    return false;
  }
  
  public void addVerifyOnlyHeader(HttpServletResponse response) {
    response.addHeader("X-VDS-TOKEN-VERIFIED", "true");
  }
  
  private boolean verifyOnlyEntryHasMatchingClaim(Map.Entry<String, Object> mapEntry) {
    return this.properties.getVerifyOnlyEntries().stream()
      .anyMatch(verifyOnlyEntry -> (Objects.equals(verifyOnlyEntry.getClaimName(), mapEntry.getKey()) && Objects.equals(verifyOnlyEntry.getClaimValue(), mapEntry.getValue())));
  }
}
