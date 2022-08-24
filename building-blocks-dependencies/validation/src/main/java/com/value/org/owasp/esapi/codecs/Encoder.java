package com.value.org.owasp.esapi.codecs;

public interface Encoder {
  String canonicalize(String paramString, boolean paramBoolean);
  
  String canonicalize(String paramString, boolean paramBoolean1, boolean paramBoolean2);
}
