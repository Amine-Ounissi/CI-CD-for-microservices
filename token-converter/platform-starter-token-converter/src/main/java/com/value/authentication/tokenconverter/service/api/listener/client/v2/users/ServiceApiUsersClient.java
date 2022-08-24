package com.value.authentication.tokenconverter.service.api.listener.client.v2.users;

import com.value.authentication.tokenconverter.service.api.rest.spec.v2.users.UserListByExternalidsPostRequestBody;
import com.value.authentication.tokenconverter.service.api.rest.spec.v2.users.UserListInBulkByExternalIdsPostRequestBody;
import com.value.authentication.tokenconverter.service.api.rest.spec.v2.users.UsersByExternalIdsPostRequestBody;
import com.value.authentication.tokenconverter.service.api.rest.spec.v2.users.UsersInBulkByExternalIdsPostRequestBody;
import com.value.dbs.user.manager.models.v2.AddRealmRequest;
import com.value.dbs.user.manager.models.v2.AddRealmResponse;
import com.value.dbs.user.manager.models.v2.AssignRealm;
import com.value.dbs.user.manager.models.v2.BatchResponseItem;
import com.value.dbs.user.manager.models.v2.BatchUser;
import com.value.dbs.user.manager.models.v2.CreateIdentityRequest;
import com.value.dbs.user.manager.models.v2.CreateIdentityResponse;
import com.value.dbs.user.manager.models.v2.ElectronicAddress;
import com.value.dbs.user.manager.models.v2.GetElectronicAddress;
import com.value.dbs.user.manager.models.v2.GetElectronicAddresses;
import com.value.dbs.user.manager.models.v2.GetIdentities;
import com.value.dbs.user.manager.models.v2.GetIdentity;
import com.value.dbs.user.manager.models.v2.GetIdentitySessions;
import com.value.dbs.user.manager.models.v2.GetPhoneAddress;
import com.value.dbs.user.manager.models.v2.GetPhoneAddresses;
import com.value.dbs.user.manager.models.v2.GetPostalAddress;
import com.value.dbs.user.manager.models.v2.GetPostalAddresses;
import com.value.dbs.user.manager.models.v2.GetUser;
import com.value.dbs.user.manager.models.v2.GetUsersByLegalEntityIdsRequest;
import com.value.dbs.user.manager.models.v2.GetUsersList;
import com.value.dbs.user.manager.models.v2.IdentityLogoutRequest;
import com.value.dbs.user.manager.models.v2.IdentityRequiredActionsRequest;
import com.value.dbs.user.manager.models.v2.ImportIdentity;
import com.value.dbs.user.manager.models.v2.LegalEntity;
import com.value.dbs.user.manager.models.v2.PhoneAddress;
import com.value.dbs.user.manager.models.v2.PostalAddress;
import com.value.dbs.user.manager.models.v2.Realm;
import com.value.dbs.user.manager.models.v2.ReplaceUserProfileAttributes;
import com.value.dbs.user.manager.models.v2.UpdateIdentityRequest;
import com.value.dbs.user.manager.models.v2.User;
import com.value.dbs.user.manager.models.v2.UserCreated;
import com.value.dbs.user.manager.models.v2.UserExternal;
import com.value.dbs.user.manager.models.v2.UserProfile;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface ServiceApiUsersClient {
  ResponseEntity<? extends List<GetUser>> getUsers(
    GetUsersQueryParameters paramGetUsersQueryParameters);
  
  ResponseEntity<UserCreated> postUsers(UserExternal paramUserExternal);
  
  ResponseEntity<? extends List<BatchResponseItem>> putUsers(List<BatchUser> paramList);
  
  ResponseEntity<GetUser> getUserByInternalId(String paramString);
  
  ResponseEntity<UserProfile> getProfile(String paramString);
  
  ResponseEntity<Void> putProfile(ReplaceUserProfileAttributes paramReplaceUserProfileAttributes,
    String paramString);
  
  ResponseEntity<GetPhoneAddresses> getPhoneAddresses(String paramString);
  
  ResponseEntity<Void> postPhoneAddresses(PhoneAddress paramPhoneAddress, String paramString);
  
  ResponseEntity<GetPhoneAddress> getPhoneAddress(String paramString1, String paramString2);
  
  ResponseEntity<Void> putPhoneAddress(PhoneAddress paramPhoneAddress, String paramString1,
    String paramString2);
  
  ResponseEntity<Void> deletePhoneAddress(String paramString1, String paramString2);
  
  ResponseEntity<GetElectronicAddresses> getElectronicAddresses(String paramString);
  
  ResponseEntity<Void> postElectronicAddresses(ElectronicAddress paramElectronicAddress,
    String paramString);
  
  ResponseEntity<GetElectronicAddress> getElectronicAddress(String paramString1,
    String paramString2);
  
  ResponseEntity<Void> putElectronicAddress(ElectronicAddress paramElectronicAddress,
    String paramString1, String paramString2);
  
  ResponseEntity<Void> deleteElectronicAddress(String paramString1, String paramString2);
  
  ResponseEntity<GetPostalAddresses> getPostalAddresses(String paramString);
  
  ResponseEntity<Void> postPostalAddresses(PostalAddress paramPostalAddress, String paramString);
  
  ResponseEntity<GetPostalAddress> getPostalAddress(String paramString1, String paramString2);
  
  ResponseEntity<Void> putPostalAddress(PostalAddress paramPostalAddress, String paramString1,
    String paramString2);
  
  ResponseEntity<Void> deletePostalAddress(String paramString1, String paramString2);
  
  ResponseEntity<GetUsersList> getUsersInBulk(
    GetUsersInBulkQueryParameters paramGetUsersInBulkQueryParameters);
  
  ResponseEntity<? extends List<BatchResponseItem>> postUsersInBulk(List<User> paramList);
  
  ResponseEntity<? extends List<GetUser>> postUserListInBulkByExternalIds(
    UserListInBulkByExternalIdsPostRequestBody paramUserListInBulkByExternalIdsPostRequestBody);
  
  ResponseEntity<? extends List<GetUser>> postUsersInBulkByExternalIds(
    UsersInBulkByExternalIdsPostRequestBody paramUsersInBulkByExternalIdsPostRequestBody);
  
  ResponseEntity<Void> getCheckUserexists(String paramString);
  
  ResponseEntity<Void> getCheckUserExists(String paramString);
  
  ResponseEntity<GetUser> getUserByExternalid(String paramString);
  
  ResponseEntity<LegalEntity> getLegalEntityByUserexternalid(String paramString);
  
  ResponseEntity<GetUser> getUserByExternalId(String paramString);
  
  ResponseEntity<LegalEntity> getLegalEntityByUserExternalId(String paramString);
  
  ResponseEntity<? extends List<GetUser>> postUserListByExternalids(
    UserListByExternalidsPostRequestBody paramUserListByExternalidsPostRequestBody);
  
  ResponseEntity<? extends List<GetUser>> postUsersByExternalIds(
    UsersByExternalIdsPostRequestBody paramUsersByExternalIdsPostRequestBody);
  
  ResponseEntity<GetIdentities> getIdentities(
    GetIdentitiesQueryParameters paramGetIdentitiesQueryParameters);
  
  ResponseEntity<GetIdentities> getIdentities();
  
  ResponseEntity<CreateIdentityResponse> postIdentities(
    CreateIdentityRequest paramCreateIdentityRequest);
  
  ResponseEntity<GetIdentity> getIdentityByInternalId(String paramString);
  
  ResponseEntity<Void> putIdentityByInternalId(UpdateIdentityRequest paramUpdateIdentityRequest,
    String paramString);
  
  ResponseEntity<Void> putIdentityActionsByInternalId(
    IdentityRequiredActionsRequest paramIdentityRequiredActionsRequest, String paramString);
  
  ResponseEntity<GetIdentitySessions> getIdentitySessionsByInternalId(String paramString);
  
  ResponseEntity<Void> postLogoutIdentitySessionsByInternalId(
    IdentityLogoutRequest paramIdentityLogoutRequest, String paramString);
  
  ResponseEntity<CreateIdentityResponse> postExisting(ImportIdentity paramImportIdentity);
  
  ResponseEntity<? extends List<Realm>> getIdentityRealms(
    GetIdentityRealmsQueryParameters paramGetIdentityRealmsQueryParameters);
  
  ResponseEntity<? extends List<Realm>> getIdentityRealms();
  
  ResponseEntity<AddRealmResponse> postIdentityRealms(AddRealmRequest paramAddRealmRequest);
  
  ResponseEntity<Void> postAssignIdentityRealm(AssignRealm paramAssignRealm, String paramString);
  
  ResponseEntity<GetUsersList> postUsersByLegalentityids(
    GetUsersByLegalEntityIdsRequest paramGetUsersByLegalEntityIdsRequest);
  
  ResponseEntity<GetUsersList> postUsersByLegalEntityIds(
    GetUsersByLegalEntityIdsRequest paramGetUsersByLegalEntityIdsRequest);
}
