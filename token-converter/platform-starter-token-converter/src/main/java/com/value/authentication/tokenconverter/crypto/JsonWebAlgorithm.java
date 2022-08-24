package com.value.authentication.tokenconverter.crypto;

public enum JsonWebAlgorithm {
  HS256("HMACSHA256"),
  HS384("HMACSHA384"),
  HS512("HMACSHA512"),
  RS256("SHA256withRSA"),
  RS384("SHA384withRSA"),
  RS512("SHA512withRSA"),
  ES256("SHA256withECDSA"),
  ES384("SHA384withECDSA"),
  ES512("SHA512withECDSA");
  
  private final String jcaAlg;
  
  JsonWebAlgorithm(String jcaAlg) {
    this.jcaAlg = jcaAlg;
  }
  
  public String getJcaAlg() {
    return this.jcaAlg;
  }
}
