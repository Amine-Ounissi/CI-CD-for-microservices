package com.value.buildingblocks.jwt.core.properties;

import com.value.buildingblocks.jwt.core.exception.TokenKeyException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;

public enum TokenKeyInput {
  RAW {
    public byte[] convert(byte[] value) {
      return value;
    }
  },
  BASE64 {
    public byte[] convert(byte[] value) {
      byte[] key = getKeyBody(value);
      return Base64.getDecoder().decode(key);
    }
    
    private byte[] getKeyBody(byte[] value) {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      try(ByteArrayInputStream inputStream = new ByteArrayInputStream(value); 
          BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        String line;
        while ((line = reader.readLine()) != null) {
          if (line.startsWith("-----") && line.endsWith("-----"))
            continue; 
          outputStream.write(line.getBytes());
        } 
      } catch (IOException e) {
        throw new TokenKeyException("Can't read token with input key type BASE64", e);
      } 
      return outputStream.toByteArray();
    }
  };
  
  public abstract byte[] convert(byte[] paramArrayOfbyte);
}
