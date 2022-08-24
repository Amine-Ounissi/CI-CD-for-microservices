package com.value.buildingblocks.common;

public class HttpCommunicationConstants {
  public static final String HTTP_SCHEME = "http://";
  
  public static final String X_CXT_REMOTE_USER = "X-CXT-Remote-User";
  
  public static final String X_CXT_USER_TOKEN = "X-CXT-User-Token";
  
  public static final String X_FORWARDED_FOR = "x-forwarded-for";
  
  public static final String X_CXT_REQUESTTIME = "X-CXT-RequestTime";
  
  public static final String X_CXT_USERAGENT = "X-CXT-UserAgent";
  
  public static final String X_CXT_CHANNELID = "X-CXT-ChannelID";
  
  public static final String X_CXT_REQUESTUUID = "X-CXT-RequestUUID";
  
  public static final String X_CXT_AUTHSTATUS = "X-CXT-AuthStatus";
  
  protected HttpCommunicationConstants() {
    throw new AssertionError("No HttpCommunicationConstants instances for you!");
  }
}
