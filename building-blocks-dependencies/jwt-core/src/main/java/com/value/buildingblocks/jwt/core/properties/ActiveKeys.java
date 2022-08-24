package com.value.buildingblocks.jwt.core.properties;

import com.value.buildingblocks.jwt.core.exception.TokenKeyException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class ActiveKeys {
  private static final Logger logger = LoggerFactory.getLogger(ActiveKeys.class);
  
  private final LinkedList<TokenKey> allKeys = new LinkedList<>();
  
  private boolean allKeysUpdated = false;
  
  public TokenKey getActiveKey() {
    loadKeys(getKey(), getKeys());
    TokenKey key = this.allKeys.getLast();
    logger.debug("Returned Signature secret key, id: {}", key.getId());
    return key;
  }
  
  public List<TokenKey> getAllKeys() {
    loadKeys(getKey(), getKeys());
    return this.allKeys;
  }
  
  synchronized void loadKeys(TokenKey key, List<TokenKey> keys) {
    if (this.allKeysUpdated)
      return; 
    LinkedHashSet<TokenKey> allKeysLocal = new LinkedHashSet<>();
    if (keys != null && !keys.isEmpty()) {
      keys.forEach(oneKey -> appendKey(allKeysLocal, oneKey));
    } else if (key != null) {
      appendKey(allKeysLocal, key);
    } 
    this.allKeysUpdated = true;
    this.allKeys.clear();
    this.allKeys.addAll(allKeysLocal);
  }
  
  private void appendKey(LinkedHashSet<TokenKey> keys, TokenKey key) {
    if (key.getId() == null)
      key.setId(generateId(key)); 
    keys.add(key);
  }
  
  private String generateId(TokenKey tokenKey) {
    try {
      MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
      byte[] digest = messageDigest.digest((tokenKey
          .getType() + "_" + tokenKey.getInput() + "_" + tokenKey.getValue())
          .getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(digest);
    } catch (NoSuchAlgorithmException e) {
      throw new TokenKeyException("Can't set default keyIDs for not set key IDs", e);
    } 
  }
  
  public abstract TokenKey getKey();
  
  public abstract List<TokenKey> getKeys();
}
