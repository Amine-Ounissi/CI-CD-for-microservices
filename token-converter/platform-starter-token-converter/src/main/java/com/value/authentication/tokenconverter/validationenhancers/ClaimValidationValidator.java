package com.value.authentication.tokenconverter.validationenhancers;

import com.value.authentication.tokenconverter.configuration.TokenConverterProperties;
import com.value.authentication.tokenconverter.enhancer.ValidationEnhancer;
import com.value.authentication.tokenconverter.exception.TokenConverterValidationException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClaimValidationValidator implements ValidationEnhancer {
  private static final Logger log = LoggerFactory.getLogger(ClaimValidationValidator.class);
  
  private final TokenConverterProperties properties;
  
  public ClaimValidationValidator(TokenConverterProperties properties) {
    this.properties = properties;
  }
  
  public void validate(Map<String, Object> claims) throws TokenConverterValidationException {
    log.debug("Checking claims against deny list entries");
    if (claimsContainMatchingValidationClaim(claims)) {
      logDebugMessageIfAppropriate(claims);
      throw new TokenConverterValidationException("Token failed validation as it contains a claim and value matching a deny list entry");
    } 
  }
  
  private boolean claimsContainMatchingValidationClaim(Map<String, Object> claims) {
    return this.properties.getClaimValueValidation()
      .getClaimDenyEntries()
      .stream()
      .anyMatch(denyListEntry -> denyListEntryHasMatchingClaimAndValue(denyListEntry, claims));
  }
  
  private boolean denyListEntryHasMatchingClaimAndValue(TokenConverterProperties.ClaimValueValidation.ClaimDenyEntry denyListEntry, Map<String, Object> claims) {
    Object claimValue = claims.get(denyListEntry.getClaimName());
    if (claimValue != null)
      return denyListEntry.getClaimValues().stream()
        .anyMatch(denyListValueEntry -> Objects.equals(denyListValueEntry, claimValue)); 
    return false;
  }
  
  private void logDebugMessageIfAppropriate(Map<String, Object> claims) {
    if (log.isDebugEnabled()) {
      List<TokenConverterProperties.ClaimValueValidation.ClaimDenyEntry> matchingDenyEntries = (List<TokenConverterProperties.ClaimValueValidation.ClaimDenyEntry>)this.properties.getClaimValueValidation().getClaimDenyEntries().stream().filter(denyListEntry -> denyListEntryHasMatchingClaimAndValue(denyListEntry, claims)).collect(Collectors.toList());
      log.debug("External token has claims matching deny list :: {}", matchingDenyEntries);
    } 
  }
}
