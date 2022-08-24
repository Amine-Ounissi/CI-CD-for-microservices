package com.value.buildingblocks.jwt.core.blacklist;

import com.value.buildingblocks.jwt.core.authenticaton.JsonWebTokenAuthentication;
import com.value.buildingblocks.jwt.core.blacklist.dto.BlacklistRequest;
import com.value.buildingblocks.jwt.core.blacklist.util.BlacklistUtil;
import com.value.buildingblocks.jwt.core.exception.BlacklistException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public interface TokenBlacklistService {
  void blacklist(BlacklistRequest paramBlacklistRequest);
  
  boolean validate(JsonWebTokenAuthentication paramJsonWebTokenAuthentication);
  
  default boolean validate() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken || 
      !authentication.isAuthenticated())
      return false; 
    return validate((JsonWebTokenAuthentication)authentication);
  }
  
  default boolean validateAndBlacklistJti(JsonWebTokenAuthentication authentication, String blacklistReason) throws BlacklistException {
    if (validate(authentication)) {
      blacklist(BlacklistUtil.prepareJtiBlackListRequest(authentication, blacklistReason));
      return true;
    } 
    return false;
  }
}
