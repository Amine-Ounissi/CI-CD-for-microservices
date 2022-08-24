package com.value.authentication.tokenconverter.client;

import java.util.Optional;

public interface DbsUserClient {
  Optional<UserInfo> getUserInfoFromUserInternalId(String paramString);
  
  Optional<UserInfo> getUserInfoFromUserExternalId(String paramString);
}
