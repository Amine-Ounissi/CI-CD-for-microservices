package com.value.authentication.tokenconverter.service.api.listener.client.v2.users;

import com.value.authentication.tokenconverter.service.api.rest.spec.v2.users.UserListByExternalidsPostRequestBody;
import com.value.authentication.tokenconverter.service.api.rest.spec.v2.users.UserListInBulkByExternalIdsPostRequestBody;
import com.value.authentication.tokenconverter.service.api.rest.spec.v2.users.UsersByExternalIdsPostRequestBody;
import com.value.authentication.tokenconverter.service.api.rest.spec.v2.users.UsersInBulkByExternalIdsPostRequestBody;
import com.value.buildingblocks.backend.communication.http.HttpClientUtils;
import com.value.buildingblocks.backend.communication.http.UriQueryBuilder;
import com.value.buildingblocks.backend.internalrequest.InternalRequestContext;
import com.value.buildingblocks.presentation.errors.BadRequestException;
import com.value.buildingblocks.presentation.errors.ForbiddenException;
import com.value.buildingblocks.presentation.errors.InternalServerErrorException;
import com.value.buildingblocks.presentation.errors.NotFoundException;
import com.value.buildingblocks.presentation.errors.UnauthorizedException;
import com.value.dbs.user.manager.models.v2.AddRealmRequest;
import com.value.dbs.user.manager.models.v2.AddRealmResponse;
import com.value.dbs.user.manager.models.v2.AssignRealm;
import com.value.dbs.user.manager.models.v2.BatchResponseItem;
import com.value.dbs.user.manager.models.v2.BatchUser;
import com.value.dbs.user.manager.models.v2.ConflictError;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

public class ServiceApiUsersClientHttpImpl implements ServiceApiUsersClient {
  protected RestTemplate restTemplate;
  
  protected String serviceId;
  
  protected String scheme;
  
  protected String baseUri = "/internal/v2";
  
  protected ObjectMapper objectMapper;
  
  protected InternalRequestContext internalRequestContext;
  
  private static final Logger LOG = LoggerFactory.getLogger(ServiceApiUsersClientHttpImpl.class);
  
  @Deprecated
  public ServiceApiUsersClientHttpImpl(RestTemplate restTemplate, String serviceId, String scheme, ObjectMapper objectMapper, InternalRequestContext internalRequestContext) {
    Objects.requireNonNull(restTemplate, "RestTemplate is required.");
    Objects.requireNonNull(serviceId, "ServiceId is required.");
    Objects.requireNonNull(scheme, "scheme is required.");
    Objects.requireNonNull(this.baseUri, "BaseUri is required.");
    Objects.requireNonNull(internalRequestContext, "internalRequestContext is required.");
    this.restTemplate = restTemplate;
    this.serviceId = serviceId;
    this.scheme = scheme;
    this.objectMapper = objectMapper;
    this.internalRequestContext = internalRequestContext;
  }
  
  public ServiceApiUsersClientHttpImpl(RestTemplate restTemplate, String serviceId, String scheme, InternalRequestContext internalRequestContext) {
    this(restTemplate, serviceId, scheme, null, internalRequestContext);
  }
  
  public String getBaseUri() {
    return this.baseUri;
  }
  
  public void setBaseUri(String baseUri) {
    Objects.requireNonNull(baseUri, "baseUri is required.");
    this.baseUri = baseUri;
  }
  
  private void addHeaderIfNotEmpty(HttpHeaders headers, String name, String value) {
    if (!StringUtils.isEmpty(value))
      headers.add(name, value); 
  }
  
  public ResponseEntity<? extends List<GetUser>> getUsers(GetUsersQueryParameters queryParameters) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users");
      UriQueryBuilder uriQueryBuilder = UriQueryBuilder.instance();
      uriQueryBuilder.addParameterIfNotNull("entityId", queryParameters.getEntityId());
      uriQueryBuilder.addParameterIfNotNull("query", queryParameters.getQuery());
      uriQueryBuilder.addParameterIfNotNull("from", queryParameters.getFrom());
      uriQueryBuilder.addParameterIfNotNull("cursor", queryParameters.getCursor());
      uriQueryBuilder.addParameterIfNotNull("size", queryParameters.getSize());
      String queryPart = uriQueryBuilder.build();
      String uriString = uriBuilder.buildAndExpand(new Object[0]).toUriString() + queryPart;
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity((MultiValueMap)httpHeaders);
      ResponseEntity<GetUserArrayList> response = this.restTemplate.exchange(uri, HttpMethod.GET, httpEntity, GetUserArrayList.class);
      return (ResponseEntity)response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<UserCreated> postUsers(UserExternal userExternal) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users");
      String uriString = uriBuilder.buildAndExpand(new Object[0]).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity(userExternal, (MultiValueMap)httpHeaders);
      ResponseEntity<UserCreated> response = this.restTemplate.exchange(uri, HttpMethod.POST, httpEntity, UserCreated.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<? extends List<BatchResponseItem>> putUsers(List<BatchUser> batchUser) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users");
      String uriString = uriBuilder.buildAndExpand(new Object[0]).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity(batchUser, (MultiValueMap)httpHeaders);
      ResponseEntity<BatchResponseItemArrayList> response = this.restTemplate.exchange(uri, HttpMethod.PUT, httpEntity, BatchResponseItemArrayList.class);
      return (ResponseEntity)response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<GetUser> getUserByInternalId(String internalId) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/{internalId}");
      String uriString = uriBuilder.buildAndExpand(new Object[] { UriUtils.encodePathSegment(internalId, StandardCharsets.UTF_8.name()) }).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity((MultiValueMap)httpHeaders);
      ResponseEntity<GetUser> response = this.restTemplate.exchange(uri, HttpMethod.GET, httpEntity, GetUser.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<UserProfile> getProfile(String internalId) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/{internalId}/profile");
      String uriString = uriBuilder.buildAndExpand(new Object[] { UriUtils.encodePathSegment(internalId, StandardCharsets.UTF_8.name()) }).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity((MultiValueMap)httpHeaders);
      ResponseEntity<UserProfile> response = this.restTemplate.exchange(uri, HttpMethod.GET, httpEntity, UserProfile.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 401:
          throw (UnauthorizedException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, UnauthorizedException.class);
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<Void> putProfile(ReplaceUserProfileAttributes replaceUserProfileAttributes, String internalId) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/{internalId}/profile");
      String uriString = uriBuilder.buildAndExpand(new Object[] { UriUtils.encodePathSegment(internalId, StandardCharsets.UTF_8.name()) }).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity(replaceUserProfileAttributes, (MultiValueMap)httpHeaders);
      ResponseEntity<Void> response = this.restTemplate.exchange(uri, HttpMethod.PUT, httpEntity, Void.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 401:
          throw (UnauthorizedException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, UnauthorizedException.class);
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<GetPhoneAddresses> getPhoneAddresses(String internalId) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/{internalId}/profile/phone-addresses");
      String uriString = uriBuilder.buildAndExpand(new Object[] { UriUtils.encodePathSegment(internalId, StandardCharsets.UTF_8.name()) }).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity((MultiValueMap)httpHeaders);
      ResponseEntity<GetPhoneAddresses> response = this.restTemplate.exchange(uri, HttpMethod.GET, httpEntity, GetPhoneAddresses.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 401:
          throw (UnauthorizedException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, UnauthorizedException.class);
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<Void> postPhoneAddresses(PhoneAddress phoneAddress, String internalId) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/{internalId}/profile/phone-addresses");
      String uriString = uriBuilder.buildAndExpand(new Object[] { UriUtils.encodePathSegment(internalId, StandardCharsets.UTF_8.name()) }).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity(phoneAddress, (MultiValueMap)httpHeaders);
      ResponseEntity<Void> response = this.restTemplate.exchange(uri, HttpMethod.POST, httpEntity, Void.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 401:
          throw (UnauthorizedException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, UnauthorizedException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<GetPhoneAddress> getPhoneAddress(String internalId, String phoneAddressKey) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/{internalId}/profile/phone-addresses/{phoneAddressKey}");
      String[] encodedUriParams = new String[2];
      encodedUriParams[0] = UriUtils.encodePathSegment(internalId, StandardCharsets.UTF_8.name());
      encodedUriParams[1] = UriUtils.encodePathSegment(phoneAddressKey, StandardCharsets.UTF_8.name());
      String uriString = uriBuilder.buildAndExpand((Object[])encodedUriParams).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity((MultiValueMap)httpHeaders);
      ResponseEntity<GetPhoneAddress> response = this.restTemplate.exchange(uri, HttpMethod.GET, httpEntity, GetPhoneAddress.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 401:
          throw (UnauthorizedException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, UnauthorizedException.class);
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<Void> putPhoneAddress(PhoneAddress phoneAddress, String internalId, String phoneAddressKey) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/{internalId}/profile/phone-addresses/{phoneAddressKey}");
      String[] encodedUriParams = new String[2];
      encodedUriParams[0] = UriUtils.encodePathSegment(internalId, StandardCharsets.UTF_8.name());
      encodedUriParams[1] = UriUtils.encodePathSegment(phoneAddressKey, StandardCharsets.UTF_8.name());
      String uriString = uriBuilder.buildAndExpand((Object[])encodedUriParams).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity(phoneAddress, (MultiValueMap)httpHeaders);
      ResponseEntity<Void> response = this.restTemplate.exchange(uri, HttpMethod.PUT, httpEntity, Void.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 401:
          throw (UnauthorizedException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, UnauthorizedException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<Void> deletePhoneAddress(String internalId, String phoneAddressKey) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/{internalId}/profile/phone-addresses/{phoneAddressKey}");
      String[] encodedUriParams = new String[2];
      encodedUriParams[0] = UriUtils.encodePathSegment(internalId, StandardCharsets.UTF_8.name());
      encodedUriParams[1] = UriUtils.encodePathSegment(phoneAddressKey, StandardCharsets.UTF_8.name());
      String uriString = uriBuilder.buildAndExpand((Object[])encodedUriParams).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity((MultiValueMap)httpHeaders);
      ResponseEntity<Void> response = this.restTemplate.exchange(uri, HttpMethod.DELETE, httpEntity, Void.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 401:
          throw (UnauthorizedException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, UnauthorizedException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<GetElectronicAddresses> getElectronicAddresses(String internalId) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/{internalId}/profile/electronic-addresses");
      String uriString = uriBuilder.buildAndExpand(new Object[] { UriUtils.encodePathSegment(internalId, StandardCharsets.UTF_8.name()) }).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity((MultiValueMap)httpHeaders);
      ResponseEntity<GetElectronicAddresses> response = this.restTemplate.exchange(uri, HttpMethod.GET, httpEntity, GetElectronicAddresses.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 401:
          throw (UnauthorizedException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, UnauthorizedException.class);
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<Void> postElectronicAddresses(ElectronicAddress electronicAddress, String internalId) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/{internalId}/profile/electronic-addresses");
      String uriString = uriBuilder.buildAndExpand(new Object[] { UriUtils.encodePathSegment(internalId, StandardCharsets.UTF_8.name()) }).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity(electronicAddress, (MultiValueMap)httpHeaders);
      ResponseEntity<Void> response = this.restTemplate.exchange(uri, HttpMethod.POST, httpEntity, Void.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 401:
          throw (UnauthorizedException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, UnauthorizedException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<GetElectronicAddress> getElectronicAddress(String internalId, String electronicAddressKey) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/{internalId}/profile/electronic-addresses/{electronicAddressKey}");
      String[] encodedUriParams = new String[2];
      encodedUriParams[0] = UriUtils.encodePathSegment(internalId, StandardCharsets.UTF_8.name());
      encodedUriParams[1] = UriUtils.encodePathSegment(electronicAddressKey, StandardCharsets.UTF_8.name());
      String uriString = uriBuilder.buildAndExpand((Object[])encodedUriParams).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity((MultiValueMap)httpHeaders);
      ResponseEntity<GetElectronicAddress> response = this.restTemplate.exchange(uri, HttpMethod.GET, httpEntity, GetElectronicAddress.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 401:
          throw (UnauthorizedException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, UnauthorizedException.class);
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<Void> putElectronicAddress(ElectronicAddress electronicAddress, String internalId, String electronicAddressKey) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/{internalId}/profile/electronic-addresses/{electronicAddressKey}");
      String[] encodedUriParams = new String[2];
      encodedUriParams[0] = UriUtils.encodePathSegment(internalId, StandardCharsets.UTF_8.name());
      encodedUriParams[1] = UriUtils.encodePathSegment(electronicAddressKey, StandardCharsets.UTF_8.name());
      String uriString = uriBuilder.buildAndExpand((Object[])encodedUriParams).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity(electronicAddress, (MultiValueMap)httpHeaders);
      ResponseEntity<Void> response = this.restTemplate.exchange(uri, HttpMethod.PUT, httpEntity, Void.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 401:
          throw (UnauthorizedException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, UnauthorizedException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<Void> deleteElectronicAddress(String internalId, String electronicAddressKey) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/{internalId}/profile/electronic-addresses/{electronicAddressKey}");
      String[] encodedUriParams = new String[2];
      encodedUriParams[0] = UriUtils.encodePathSegment(internalId, StandardCharsets.UTF_8.name());
      encodedUriParams[1] = UriUtils.encodePathSegment(electronicAddressKey, StandardCharsets.UTF_8.name());
      String uriString = uriBuilder.buildAndExpand((Object[])encodedUriParams).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity((MultiValueMap)httpHeaders);
      ResponseEntity<Void> response = this.restTemplate.exchange(uri, HttpMethod.DELETE, httpEntity, Void.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 401:
          throw (UnauthorizedException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, UnauthorizedException.class);
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<GetPostalAddresses> getPostalAddresses(String internalId) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/{internalId}/profile/postal-addresses");
      String uriString = uriBuilder.buildAndExpand(new Object[] { UriUtils.encodePathSegment(internalId, StandardCharsets.UTF_8.name()) }).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity((MultiValueMap)httpHeaders);
      ResponseEntity<GetPostalAddresses> response = this.restTemplate.exchange(uri, HttpMethod.GET, httpEntity, GetPostalAddresses.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 401:
          throw (UnauthorizedException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, UnauthorizedException.class);
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<Void> postPostalAddresses(PostalAddress postalAddress, String internalId) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/{internalId}/profile/postal-addresses");
      String uriString = uriBuilder.buildAndExpand(new Object[] { UriUtils.encodePathSegment(internalId, StandardCharsets.UTF_8.name()) }).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity(postalAddress, (MultiValueMap)httpHeaders);
      ResponseEntity<Void> response = this.restTemplate.exchange(uri, HttpMethod.POST, httpEntity, Void.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 401:
          throw (UnauthorizedException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, UnauthorizedException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<GetPostalAddress> getPostalAddress(String internalId, String postalAddressKey) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/{internalId}/profile/postal-addresses/{postalAddressKey}");
      String[] encodedUriParams = new String[2];
      encodedUriParams[0] = UriUtils.encodePathSegment(internalId, StandardCharsets.UTF_8.name());
      encodedUriParams[1] = UriUtils.encodePathSegment(postalAddressKey, StandardCharsets.UTF_8.name());
      String uriString = uriBuilder.buildAndExpand((Object[])encodedUriParams).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity((MultiValueMap)httpHeaders);
      ResponseEntity<GetPostalAddress> response = this.restTemplate.exchange(uri, HttpMethod.GET, httpEntity, GetPostalAddress.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 401:
          throw (UnauthorizedException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, UnauthorizedException.class);
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<Void> putPostalAddress(PostalAddress postalAddress, String internalId, String postalAddressKey) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/{internalId}/profile/postal-addresses/{postalAddressKey}");
      String[] encodedUriParams = new String[2];
      encodedUriParams[0] = UriUtils.encodePathSegment(internalId, StandardCharsets.UTF_8.name());
      encodedUriParams[1] = UriUtils.encodePathSegment(postalAddressKey, StandardCharsets.UTF_8.name());
      String uriString = uriBuilder.buildAndExpand((Object[])encodedUriParams).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity(postalAddress, (MultiValueMap)httpHeaders);
      ResponseEntity<Void> response = this.restTemplate.exchange(uri, HttpMethod.PUT, httpEntity, Void.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 401:
          throw (UnauthorizedException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, UnauthorizedException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<Void> deletePostalAddress(String internalId, String postalAddressKey) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/{internalId}/profile/postal-addresses/{postalAddressKey}");
      String[] encodedUriParams = new String[2];
      encodedUriParams[0] = UriUtils.encodePathSegment(internalId, StandardCharsets.UTF_8.name());
      encodedUriParams[1] = UriUtils.encodePathSegment(postalAddressKey, StandardCharsets.UTF_8.name());
      String uriString = uriBuilder.buildAndExpand((Object[])encodedUriParams).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity((MultiValueMap)httpHeaders);
      ResponseEntity<Void> response = this.restTemplate.exchange(uri, HttpMethod.DELETE, httpEntity, Void.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 401:
          throw (UnauthorizedException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, UnauthorizedException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<GetUsersList> getUsersInBulk(GetUsersInBulkQueryParameters queryParameters) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/bulk");
      UriQueryBuilder uriQueryBuilder = UriQueryBuilder.instance();
      uriQueryBuilder.addParameterIfNotNull("id", queryParameters.getId());
      uriQueryBuilder.addParameterIfNotNull("query", queryParameters.getQuery());
      uriQueryBuilder.addParameterIfNotNull("from", queryParameters.getFrom());
      uriQueryBuilder.addParameterIfNotNull("cursor", queryParameters.getCursor());
      uriQueryBuilder.addParameterIfNotNull("size", queryParameters.getSize());
      String queryPart = uriQueryBuilder.build();
      String uriString = uriBuilder.buildAndExpand(new Object[0]).toUriString() + queryPart;
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity((MultiValueMap)httpHeaders);
      ResponseEntity<GetUsersList> response = this.restTemplate.exchange(uri, HttpMethod.GET, httpEntity, GetUsersList.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<? extends List<BatchResponseItem>> postUsersInBulk(List<User> user) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/bulk");
      String uriString = uriBuilder.buildAndExpand(new Object[0]).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity(user, (MultiValueMap)httpHeaders);
      ResponseEntity<BatchResponseItemArrayList> response = this.restTemplate.exchange(uri, HttpMethod.POST, httpEntity, BatchResponseItemArrayList.class);
      return (ResponseEntity)response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<? extends List<GetUser>> postUserListInBulkByExternalIds(
    UserListInBulkByExternalIdsPostRequestBody userListInBulkByExternalIdsPostRequestBody) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/bulk/externalIds");
      String uriString = uriBuilder.buildAndExpand(new Object[0]).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity(userListInBulkByExternalIdsPostRequestBody, (MultiValueMap)httpHeaders);
      ResponseEntity<GetUserArrayList> response = this.restTemplate.exchange(uri, HttpMethod.POST, httpEntity, GetUserArrayList.class);
      return (ResponseEntity)response;
    } catch (RestClientResponseException exception) {
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<? extends List<GetUser>> postUsersInBulkByExternalIds(
    UsersInBulkByExternalIdsPostRequestBody usersInBulkByExternalIdsPostRequestBody) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/bulk/externalids");
      String uriString = uriBuilder.buildAndExpand(new Object[0]).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity(usersInBulkByExternalIdsPostRequestBody, (MultiValueMap)httpHeaders);
      ResponseEntity<GetUserArrayList> response = this.restTemplate.exchange(uri, HttpMethod.POST, httpEntity, GetUserArrayList.class);
      return (ResponseEntity)response;
    } catch (RestClientResponseException exception) {
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<Void> getCheckUserexists(String externalId) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/existing/externalId/{externalId}");
      String uriString = uriBuilder.buildAndExpand(new Object[] { UriUtils.encodePathSegment(externalId, StandardCharsets.UTF_8.name()) }).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity((MultiValueMap)httpHeaders);
      ResponseEntity<Void> response = this.restTemplate.exchange(uri, HttpMethod.GET, httpEntity, Void.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 401:
          throw (UnauthorizedException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, UnauthorizedException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<Void> getCheckUserExists(String externalId) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/existing/externalids/{externalId}");
      String uriString = uriBuilder.buildAndExpand(new Object[] { UriUtils.encodePathSegment(externalId, StandardCharsets.UTF_8.name()) }).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity((MultiValueMap)httpHeaders);
      ResponseEntity<Void> response = this.restTemplate.exchange(uri, HttpMethod.GET, httpEntity, Void.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 401:
          throw (UnauthorizedException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, UnauthorizedException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<GetUser> getUserByExternalid(String externalId) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/externalId/{externalId}");
      String uriString = uriBuilder.buildAndExpand(new Object[] { UriUtils.encodePathSegment(externalId, StandardCharsets.UTF_8.name()) }).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity((MultiValueMap)httpHeaders);
      ResponseEntity<GetUser> response = this.restTemplate.exchange(uri, HttpMethod.GET, httpEntity, GetUser.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<LegalEntity> getLegalEntityByUserexternalid(String externalId) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/externalId/{externalId}/legalentities");
      String uriString = uriBuilder.buildAndExpand(new Object[] { UriUtils.encodePathSegment(externalId, StandardCharsets.UTF_8.name()) }).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity((MultiValueMap)httpHeaders);
      ResponseEntity<LegalEntity> response = this.restTemplate.exchange(uri, HttpMethod.GET, httpEntity, LegalEntity.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<GetUser> getUserByExternalId(String externalId) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/externalids/{externalId}");
      String uriString = uriBuilder.buildAndExpand(new Object[] { UriUtils.encodePathSegment(externalId, StandardCharsets.UTF_8.name()) }).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity((MultiValueMap)httpHeaders);
      ResponseEntity<GetUser> response = this.restTemplate.exchange(uri, HttpMethod.GET, httpEntity, GetUser.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<LegalEntity> getLegalEntityByUserExternalId(String externalId) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/externalids/{externalId}/legalentities");
      String uriString = uriBuilder.buildAndExpand(new Object[] { UriUtils.encodePathSegment(externalId, StandardCharsets.UTF_8.name()) }).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity((MultiValueMap)httpHeaders);
      ResponseEntity<LegalEntity> response = this.restTemplate.exchange(uri, HttpMethod.GET, httpEntity, LegalEntity.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<? extends List<GetUser>> postUserListByExternalids(
    UserListByExternalidsPostRequestBody userListByExternalidsPostRequestBody) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/externalIds");
      String uriString = uriBuilder.buildAndExpand(new Object[0]).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity(userListByExternalidsPostRequestBody, (MultiValueMap)httpHeaders);
      ResponseEntity<GetUserArrayList> response = this.restTemplate.exchange(uri, HttpMethod.POST, httpEntity, GetUserArrayList.class);
      return (ResponseEntity)response;
    } catch (RestClientResponseException exception) {
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<? extends List<GetUser>> postUsersByExternalIds(
    UsersByExternalIdsPostRequestBody usersByExternalIdsPostRequestBody) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/externalids");
      String uriString = uriBuilder.buildAndExpand(new Object[0]).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity(usersByExternalIdsPostRequestBody, (MultiValueMap)httpHeaders);
      ResponseEntity<GetUserArrayList> response = this.restTemplate.exchange(uri, HttpMethod.POST, httpEntity, GetUserArrayList.class);
      return (ResponseEntity)response;
    } catch (RestClientResponseException exception) {
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<GetIdentities> getIdentities(GetIdentitiesQueryParameters queryParameters) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/identities");
      UriQueryBuilder uriQueryBuilder = UriQueryBuilder.instance();
      uriQueryBuilder.addParameterIfNotNull("externalId", queryParameters.getExternalId());
      uriQueryBuilder.addParameterIfNotNull("from", queryParameters.getFrom());
      uriQueryBuilder.addParameterIfNotNull("cursor", queryParameters.getCursor());
      uriQueryBuilder.addParameterIfNotNull("size", queryParameters.getSize());
      String queryPart = uriQueryBuilder.build();
      String uriString = uriBuilder.buildAndExpand(new Object[0]).toUriString() + queryPart;
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity((MultiValueMap)httpHeaders);
      ResponseEntity<GetIdentities> response = this.restTemplate.exchange(uri, HttpMethod.GET, httpEntity, GetIdentities.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 401:
          throw (UnauthorizedException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, UnauthorizedException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<GetIdentities> getIdentities() {
    return getIdentities(new GetIdentitiesQueryParameters());
  }
  
  public ResponseEntity<CreateIdentityResponse> postIdentities(
    CreateIdentityRequest createIdentityRequest) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/identities");
      String uriString = uriBuilder.buildAndExpand(new Object[0]).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity(createIdentityRequest, (MultiValueMap)httpHeaders);
      ResponseEntity<CreateIdentityResponse> response = this.restTemplate.exchange(uri, HttpMethod.POST, httpEntity, CreateIdentityResponse.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 401:
          throw (UnauthorizedException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, UnauthorizedException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
        case 409:
          throw (ConflictError)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ConflictError.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<GetIdentity> getIdentityByInternalId(String internalId) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/identities/{internalId}");
      String uriString = uriBuilder.buildAndExpand(new Object[] { UriUtils.encodePathSegment(internalId, StandardCharsets.UTF_8.name()) }).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity((MultiValueMap)httpHeaders);
      ResponseEntity<GetIdentity> response = this.restTemplate.exchange(uri, HttpMethod.GET, httpEntity, GetIdentity.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 401:
          throw (UnauthorizedException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, UnauthorizedException.class);
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<Void> putIdentityByInternalId(UpdateIdentityRequest updateIdentityRequest, String internalId) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/identities/{internalId}");
      String uriString = uriBuilder.buildAndExpand(new Object[] { UriUtils.encodePathSegment(internalId, StandardCharsets.UTF_8.name()) }).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity(updateIdentityRequest, (MultiValueMap)httpHeaders);
      ResponseEntity<Void> response = this.restTemplate.exchange(uri, HttpMethod.PUT, httpEntity, Void.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 401:
          throw (UnauthorizedException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, UnauthorizedException.class);
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<Void> putIdentityActionsByInternalId(
    IdentityRequiredActionsRequest identityRequiredActionsRequest, String internalId) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/identities/{internalId}/actions");
      String uriString = uriBuilder.buildAndExpand(new Object[] { UriUtils.encodePathSegment(internalId, StandardCharsets.UTF_8.name()) }).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity(identityRequiredActionsRequest, (MultiValueMap)httpHeaders);
      ResponseEntity<Void> response = this.restTemplate.exchange(uri, HttpMethod.PUT, httpEntity, Void.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 401:
          throw (UnauthorizedException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, UnauthorizedException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<GetIdentitySessions> getIdentitySessionsByInternalId(String internalId) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/identities/{internalId}/sessions");
      String uriString = uriBuilder.buildAndExpand(new Object[] { UriUtils.encodePathSegment(internalId, StandardCharsets.UTF_8.name()) }).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity((MultiValueMap)httpHeaders);
      ResponseEntity<GetIdentitySessions> response = this.restTemplate.exchange(uri, HttpMethod.GET, httpEntity, GetIdentitySessions.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 401:
          throw (UnauthorizedException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, UnauthorizedException.class);
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<Void> postLogoutIdentitySessionsByInternalId(
    IdentityLogoutRequest identityLogoutRequest, String internalId) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/identities/{internalId}/sessions/logout");
      String uriString = uriBuilder.buildAndExpand(new Object[] { UriUtils.encodePathSegment(internalId, StandardCharsets.UTF_8.name()) }).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity(identityLogoutRequest, (MultiValueMap)httpHeaders);
      ResponseEntity<Void> response = this.restTemplate.exchange(uri, HttpMethod.POST, httpEntity, Void.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 401:
          throw (UnauthorizedException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, UnauthorizedException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<CreateIdentityResponse> postExisting(ImportIdentity importIdentity) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/identities/existing");
      String uriString = uriBuilder.buildAndExpand(new Object[0]).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity(importIdentity, (MultiValueMap)httpHeaders);
      ResponseEntity<CreateIdentityResponse> response = this.restTemplate.exchange(uri, HttpMethod.POST, httpEntity, CreateIdentityResponse.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
        case 409:
          throw (ConflictError)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ConflictError.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<? extends List<Realm>> getIdentityRealms(GetIdentityRealmsQueryParameters queryParameters) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/identities/realms");
      UriQueryBuilder uriQueryBuilder = UriQueryBuilder.instance();
      uriQueryBuilder.addParameterIfNotNull("legalEntityId", queryParameters.getLegalEntityId());
      String queryPart = uriQueryBuilder.build();
      String uriString = uriBuilder.buildAndExpand(new Object[0]).toUriString() + queryPart;
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity((MultiValueMap)httpHeaders);
      ResponseEntity<RealmArrayList> response = this.restTemplate.exchange(uri, HttpMethod.GET, httpEntity, RealmArrayList.class);
      return (ResponseEntity)response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<? extends List<Realm>> getIdentityRealms() {
    return getIdentityRealms(new GetIdentityRealmsQueryParameters());
  }
  
  public ResponseEntity<AddRealmResponse> postIdentityRealms(AddRealmRequest addRealmRequest) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/identities/realms");
      String uriString = uriBuilder.buildAndExpand(new Object[0]).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity(addRealmRequest, (MultiValueMap)httpHeaders);
      ResponseEntity<AddRealmResponse> response = this.restTemplate.exchange(uri, HttpMethod.POST, httpEntity, AddRealmResponse.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<Void> postAssignIdentityRealm(AssignRealm assignRealm, String realmName) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/identities/realms/{realmName}/legalentities");
      String uriString = uriBuilder.buildAndExpand(new Object[] { UriUtils.encodePathSegment(realmName, StandardCharsets.UTF_8.name()) }).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity(assignRealm, (MultiValueMap)httpHeaders);
      ResponseEntity<Void> response = this.restTemplate.exchange(uri, HttpMethod.POST, httpEntity, Void.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 404:
          throw (NotFoundException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, NotFoundException.class);
        case 500:
          throw (InternalServerErrorException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, InternalServerErrorException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<GetUsersList> postUsersByLegalentityids(
    GetUsersByLegalEntityIdsRequest getUsersByLegalEntityIdsRequest) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/legalEntityIds");
      String uriString = uriBuilder.buildAndExpand(new Object[0]).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity(getUsersByLegalEntityIdsRequest, (MultiValueMap)httpHeaders);
      ResponseEntity<GetUsersList> response = this.restTemplate.exchange(uri, HttpMethod.POST, httpEntity, GetUsersList.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 401:
          throw (UnauthorizedException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, UnauthorizedException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
  
  public ResponseEntity<GetUsersList> postUsersByLegalEntityIds(GetUsersByLegalEntityIdsRequest getUsersByLegalEntityIdsRequest) {
    try {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.scheme + "://" + this.serviceId).path(this.baseUri + "/users/legalentityids");
      String uriString = uriBuilder.buildAndExpand(new Object[0]).toUriString();
      URI uri = new URI(uriString);
      HttpHeaders httpHeaders = new HttpHeaders();
      addHeaderIfNotEmpty(httpHeaders, "Content-Type", MediaType.APPLICATION_JSON.toString());
      if (RequestContextHolder.getRequestAttributes() != null) {
        InternalRequestContext internalRequestContext = this.internalRequestContext;
        String authToken = internalRequestContext.getUserToken();
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-User-Token", authToken);
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-Remote-User", internalRequestContext.getRemoteUser());
        addHeaderIfNotEmpty(httpHeaders, "x-forwarded-for", internalRequestContext.getRemoteAddress());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestTime", String.valueOf(internalRequestContext.getRequestTime()));
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-UserAgent", internalRequestContext.getUserAgent());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-ChannelID", internalRequestContext.getChannelId());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-RequestUUID", internalRequestContext.getRequestUuid());
        addHeaderIfNotEmpty(httpHeaders, "X-CXT-AuthStatus", String.valueOf(internalRequestContext.getAuthStatus()));
      } 
      HttpEntity httpEntity = new HttpEntity(getUsersByLegalEntityIdsRequest, (MultiValueMap)httpHeaders);
      ResponseEntity<GetUsersList> response = this.restTemplate.exchange(uri, HttpMethod.POST, httpEntity, GetUsersList.class);
      return response;
    } catch (RestClientResponseException exception) {
      switch (exception.getRawStatusCode()) {
        case 400:
          throw (BadRequestException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, BadRequestException.class);
        case 403:
          throw (ForbiddenException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, ForbiddenException.class);
        case 401:
          throw (UnauthorizedException)HttpClientUtils.extractException(this.objectMapper, this.restTemplate.getMessageConverters(), exception, UnauthorizedException.class);
      } 
      throw exception;
    } catch (Exception exception) {
      LOG.debug("Unexpected error sending request.", exception);
      throw new InternalServerErrorException(exception.getMessage(), exception);
    } 
  }
}
