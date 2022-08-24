package com.value.authentication.tokenconverter.service.impl;

import com.value.authentication.tokenconverter.exception.InvalidIssuerException;
import com.value.authentication.tokenconverter.model.ParsedToken;
import com.value.authentication.tokenconverter.exception.JwtException;
import com.value.authentication.tokenconverter.service.UnverifiedTokenService;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UnverifiedTokenServiceImpl implements UnverifiedTokenService {
  private static final Logger log = LoggerFactory.getLogger(UnverifiedTokenServiceImpl.class);
  
  public ParsedToken parseJwt(String token) {
    SignedJWT signedJwt;
    Map<String, Object> claims;
    try {
      signedJwt = SignedJWT.parse(token);
      if (StringUtils.isBlank(signedJwt.getJWTClaimsSet().getIssuer())) {
        log.debug("Token failed verification because the issuer is invalid: {}", token);
        throw new InvalidIssuerException();
      } 
      claims = signedJwt.getJWTClaimsSet().getClaims();
    } catch (ParseException e) {
      log.debug("Token failed verification: {}", token, e);
      throw new JwtException(e.getMessage());
    } 
    JWSHeader header = signedJwt.getHeader();
    return new ParsedToken(claims, header.getAlgorithm().getName(), header.getKeyID());
  }
}
