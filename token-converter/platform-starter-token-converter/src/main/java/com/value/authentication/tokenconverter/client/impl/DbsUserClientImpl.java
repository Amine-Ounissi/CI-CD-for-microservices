package com.value.authentication.tokenconverter.client.impl;

import com.value.authentication.tokenconverter.client.DbsUserClient;
import com.value.buildingblocks.presentation.errors.ApiErrorException;
import com.value.dbs.user.manager.models.v2.GetUser;
import com.value.authentication.tokenconverter.client.UserInfo;
import com.value.authentication.tokenconverter.service.api.listener.client.v2.users.ServiceApiUsersClient;
import java.util.Optional;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;

public class DbsUserClientImpl implements DbsUserClient {
  private static final Logger log = LoggerFactory.getLogger(DbsUserClientImpl.class);
  
  private final ServiceApiUsersClient client;
  
  public DbsUserClientImpl(ServiceApiUsersClient client) {
    this.client = client;
  }
  
  @Cacheable(value = {"userInfoByInternalIdCache"}, unless = "#result == null")
  public Optional<UserInfo> getUserInfoFromUserInternalId(String userId) {
    if (StringUtils.isEmpty(userId)) {
      log.debug("No user Id passed.");
      return Optional.empty();
    } 
    log.debug("Retrieving user info from DBS for ID [{}].", userId);
    try {
      ResponseEntity<GetUser> responseEntity = this.client.getUserByInternalId(userId);
      if (responseEntity.getBody() != null) {
        log.debug("User info returned with response body: {}", responseEntity.getBody());
        return 
          Optional.of(new UserInfo(((GetUser)responseEntity.getBody()).getId(), ((GetUser)responseEntity.getBody()).getLegalEntityId()));
      } 
    } catch (ApiErrorException e) {
      log.info("Exception thrown while retrieving User Info from DBS by internal ID.", (Throwable)e);
    } catch (Exception e) {
      log.warn("Exception thrown while retrieving User Info from DBS by internal ID.", e);
    } 
    log.debug("Couldn't find user info from DBS. Returning Empty");
    return Optional.empty();
  }
  
  @Cacheable(value = {"userInfoByExternalIdCache"}, unless = "#result == null")
  public Optional<UserInfo> getUserInfoFromUserExternalId(String externalId) {
    if (StringUtils.isEmpty(externalId)) {
      log.debug("No external ID passed.");
      return Optional.empty();
    } 
    log.debug("Retrieving user info from DBS for external ID [{}].", externalId);
    try {
      ResponseEntity<GetUser> responseEntity = this.client.getUserByExternalId(externalId);
      if (responseEntity.getBody() != null) {
        log.debug("User info returned with response body: {}", responseEntity.getBody());
        return 
          Optional.of(new UserInfo(((GetUser)responseEntity.getBody()).getId(), ((GetUser)responseEntity.getBody()).getLegalEntityId()));
      } 
    } catch (ApiErrorException e) {
      log.info("Exception thrown while retrieving User Info from DBS by external ID.", (Throwable)e);
    } catch (Exception e) {
      log.warn("Exception thrown while retrieving User Info from DBS by external ID.", e);
    } 
    log.debug("Couldn't find user info from DBS. Returning Empty");
    return Optional.empty();
  }
}
