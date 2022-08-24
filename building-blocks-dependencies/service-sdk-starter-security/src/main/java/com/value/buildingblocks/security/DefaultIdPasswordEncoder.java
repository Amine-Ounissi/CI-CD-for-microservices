package com.value.buildingblocks.security;

import org.springframework.security.crypto.password.PasswordEncoder;

class DefaultIdPasswordEncoder implements PasswordEncoder {
  public String encode(CharSequence rawPassword) {
    throw new UnsupportedOperationException("encode is not supported");
  }
  
  public boolean matches(CharSequence rawPassword, String prefixEncodedPassword) {
    return false;
  }
}
