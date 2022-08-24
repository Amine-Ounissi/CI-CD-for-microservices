package com.value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
class ValueScanWarning {
  private static final Logger log = LoggerFactory.getLogger(ValueScanWarning.class);
  
  @EventListener
  public void handleContextRefreshedEvent(ContextRefreshedEvent event) {
    log.info("\n* Component scan of \"com.value\" detected!\n* Component Scanning \"com.value\" is no longer required.\n* The Service SDK is now configured using spring.factories.\n* The @SpringBootApplication annotation will component scan subpackages from it's location.\n* Please remove the component scan of the \"com.value\" package.");
  }
}
