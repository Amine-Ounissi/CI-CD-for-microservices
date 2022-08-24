package com.value.buildingblocks.backend.internalrequest;

import com.value.buildingblocks.backend.security.auth.AuthLevel;
import com.value.buildingblocks.backend.security.auth.AuthStatus;
import com.value.buildingblocks.backend.security.auth.DefaultAuthStatus;
import javax.servlet.http.HttpServletRequest;

public class DefaultInternalRequestContext implements InternalRequestContext {
  private String userToken;
  
  private String remoteUser;
  
  private String remoteAddress;
  
  private long requestTime;
  
  private String userAgent;
  
  private String channelId;
  
  private String requestUuid;
  
  private AuthStatus authStatus;
  
  public DefaultInternalRequestContext() {
    this.authStatus = new DefaultAuthStatus(AuthLevel.REJECTED, "Default auth level: Rejected");
  }
  
  public DefaultInternalRequestContext(String userToken, String remoteUser, String remoteAddress, long requestTime, String userAgent, String channelId, String requestUuid) {
    this.userToken = userToken;
    this.remoteUser = remoteUser;
    this.remoteAddress = remoteAddress;
    this.requestTime = requestTime;
    this.userAgent = userAgent;
    this.channelId = channelId;
    this.requestUuid = requestUuid;
    this.authStatus = new DefaultAuthStatus(AuthLevel.REJECTED, "Default auth level: Rejected");
  }
  
  public static DefaultInternalRequestContext contextFrom(HttpServletRequest request, String requestUuid) {
    long systemTime = System.currentTimeMillis() / 1000L;
    if (request.getRequestURI().startsWith(request.getContextPath() + "/service-api/")) {
      Long requestTime = null;
      try {
        requestTime = Long.valueOf(request.getHeader("X-CXT-RequestTime"));
      } catch (NumberFormatException nfe) {
        requestTime = Long.valueOf(systemTime);
      } 
      return new DefaultInternalRequestContext(request
          .getHeader("X-CXT-User-Token"), request
          .getHeader("X-CXT-Remote-User"), request
          .getHeader("x-forwarded-for"), requestTime
          .longValue(), request
          .getHeader("X-CXT-UserAgent"), request
          .getHeader("X-CXT-ChannelID"), request
          .getHeader("X-CXT-RequestUUID"));
    } 
    String userToken = request.getHeader("Authorization");
    String userAgent = request.getHeader("User-Agent");
    return new DefaultInternalRequestContext(userToken, request.getRemoteUser(), request
        .getHeader("x-forwarded-for"), systemTime, userAgent, "channelId", requestUuid);
  }
  
  public String getUserToken() {
    return this.userToken;
  }
  
  public void setUserToken(String userToken) {
    this.userToken = userToken;
  }
  
  public String getRemoteUser() {
    return this.remoteUser;
  }
  
  public void setRemoteUser(String remoteUser) {
    this.remoteUser = remoteUser;
  }
  
  public String getRemoteAddress() {
    return this.remoteAddress;
  }
  
  public void setRemoteAddress(String remoteAddress) {
    this.remoteAddress = remoteAddress;
  }
  
  public long getRequestTime() {
    return this.requestTime;
  }
  
  public void setRequestTime(long requestTime) {
    this.requestTime = requestTime;
  }
  
  public String getUserAgent() {
    return this.userAgent;
  }
  
  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }
  
  public String getChannelId() {
    return this.channelId;
  }
  
  public void setChannelId(String channelId) {
    this.channelId = channelId;
  }
  
  public String getRequestUuid() {
    return this.requestUuid;
  }
  
  public void setRequestUuid(String requestUuid) {
    this.requestUuid = requestUuid;
  }
  
  public AuthStatus getAuthStatus() {
    return this.authStatus;
  }
  
  public void setAuthStatus(AuthStatus authStatus) {
    this.authStatus = authStatus;
  }
  
  public String toString() {
    return String.format("DefaultInternalRequestContext{userToken='%s', remoteUser='%s', remoteAddress='%s', requestTime=%d, userAgent='%s', channelId='%s', requestUuid='%s', authStatus=%s}", new Object[] { this.userToken, this.remoteUser, this.remoteAddress, 

          
          Long.valueOf(this.requestTime), this.userAgent, this.channelId, this.requestUuid, this.authStatus });
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (o == null || getClass() != o.getClass())
      return false; 
    DefaultInternalRequestContext that = (DefaultInternalRequestContext)o;
    if (this.requestTime != that.requestTime)
      return false; 
    if ((this.userToken != null) ? !this.userToken.equals(that.userToken) : (that.userToken != null))
      return false; 
    if ((this.remoteUser != null) ? !this.remoteUser.equals(that.remoteUser) : (that.remoteUser != null))
      return false; 
    if ((this.remoteAddress != null) ? !this.remoteAddress.equals(that.remoteAddress) : (that.remoteAddress != null))
      return false; 
    if ((this.userAgent != null) ? !this.userAgent.equals(that.userAgent) : (that.userAgent != null))
      return false; 
    if ((this.channelId != null) ? !this.channelId.equals(that.channelId) : (that.channelId != null))
      return false; 
    if ((this.requestUuid != null) ? !this.requestUuid.equals(that.requestUuid) : (that.requestUuid != null))
      return false; 
    if ((this.authStatus != null) ? !this.authStatus.equals(that.authStatus) : (that.authStatus != null))
      return false;
    return true;
  }
  
  public int hashCode() {
    int result = (this.userToken != null) ? this.userToken.hashCode() : 0;
    result = 31 * result + ((this.remoteUser != null) ? this.remoteUser.hashCode() : 0);
    result = 31 * result + ((this.remoteAddress != null) ? this.remoteAddress.hashCode() : 0);
    result = 31 * result + (int)(this.requestTime ^ this.requestTime >>> 32L);
    result = 31 * result + ((this.userAgent != null) ? this.userAgent.hashCode() : 0);
    result = 31 * result + ((this.channelId != null) ? this.channelId.hashCode() : 0);
    result = 31 * result + ((this.requestUuid != null) ? this.requestUuid.hashCode() : 0);
    result = 31 * result + ((this.authStatus != null) ? this.authStatus.hashCode() : 0);
    return result;
  }
}
