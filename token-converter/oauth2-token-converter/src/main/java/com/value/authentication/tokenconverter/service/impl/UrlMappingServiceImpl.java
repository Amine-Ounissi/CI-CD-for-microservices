package com.value.authentication.tokenconverter.service.impl;

import com.value.authentication.tokenconverter.config.Oauth2TokenConverterConfig;
import com.value.authentication.tokenconverter.service.UrlMappingService;
import java.net.URI;
import java.util.Optional;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UrlMappingServiceImpl implements UrlMappingService {

  private static final Logger log = LoggerFactory.getLogger(UrlMappingServiceImpl.class);

  private final Oauth2TokenConverterConfig config;

  public UrlMappingServiceImpl(Oauth2TokenConverterConfig config) {
    this.config = config;
  }

  public URI mapToInternalUri(URI uri) {
    Optional<Oauth2TokenConverterConfig.DomainWhitelistEntry> optionalWhitelistEntry = this.config
      .getWhitelistedDomains().stream().filter(
        whitelistedDomain -> StringUtils.equals(uri.getHost(), whitelistedDomain.getIssuerHost()))
      .filter(whitelistedDomain -> StringUtils.isNotBlank(whitelistedDomain.getInternalHost()))
      .findAny();
    if (optionalWhitelistEntry.isPresent()) {
      Oauth2TokenConverterConfig.DomainWhitelistEntry whitelistEntry = optionalWhitelistEntry.get();
      String externalUri = uri.toString();
      String internalUri = externalUri
        .replace(uri.getAuthority(), whitelistEntry.getInternalHost());
      log.debug("Mapping {} to internal URL {}", externalUri, internalUri);
      if (StringUtils.isNotBlank(whitelistEntry.getInternalHostScheme())) {
        String scheme = uri.getScheme();
        if (!StringUtils.equalsIgnoreCase(scheme, whitelistEntry.getInternalHostScheme())) {
          internalUri = internalUri.replace(scheme, whitelistEntry.getInternalHostScheme());
          log.debug("Mapping scheme {} to {}", scheme, whitelistEntry.getInternalHostScheme());
        } else {
          log.debug(
            "Issuer URL scheme matches configured internal host scheme.  No mapping required.");
        }
      }
      return URI.create(internalUri);
    }
    return uri;
  }
}
