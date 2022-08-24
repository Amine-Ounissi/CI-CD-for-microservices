package com.value.buildingblocks.jwt.core.blacklist;

import com.value.buildingblocks.jwt.core.blacklist.dto.BlacklistRequest;
import java.util.Optional;

public interface BlacklistStorage {
  void add(BlacklistRequest paramBlacklistRequest);
  
  Optional<BlacklistRequest> find(String paramString, BlacklistRequest.Type paramType);
  
  void remove(BlacklistRequest paramBlacklistRequest);
  
  void cleanup();
}
