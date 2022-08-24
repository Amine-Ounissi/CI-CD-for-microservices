package com.value.buildingblocks.logging;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class CrlfMessageConverter extends MessageConverter {
  private static String target = "\n";

  private static String replacement = "\n+[ ";

  public String convert(ILoggingEvent event) {
    return replace(super.convert(event));
  }

  private String replace(String incoming) {
    if (incoming == null)
      return incoming;
    incoming = incoming.replace("\r", "");
    return incoming.replace(target, replacement);
  }

  public static String getTarget() {
    return target;
  }

  public static void setTarget(String target) {
    CrlfMessageConverter.target = target;
  }

  public static String getReplacement() {
    return replacement;
  }

  public static void setReplacement(String replacement) {
    CrlfMessageConverter.replacement = replacement;
  }
}
