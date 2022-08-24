package com.value.authentication.tokenconverter.service;

import com.value.authentication.tokenconverter.model.ParsedToken;
import java.io.IOException;

public interface UnverifiedTokenService {
  ParsedToken parseJwt(String paramString) throws IOException;
}
