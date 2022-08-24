package com.value.authentication.tokenconverter.service;

import java.net.URI;

public interface UrlMappingService {
  URI mapToInternalUri(URI paramURI);
}
