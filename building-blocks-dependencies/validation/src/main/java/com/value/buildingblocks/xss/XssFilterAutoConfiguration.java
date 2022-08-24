package com.value.buildingblocks.xss;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@ConfigurationProperties("buildingblocks.security.xss")
@ConditionalOnProperty(prefix = "buildingblocks.security.xss", name = {"autoconfig"}, havingValue = "true", matchIfMissing = true)
@PropertySource({"classpath:xssfilter.properties"})
public class XssFilterAutoConfiguration {
  private static final Logger log = LoggerFactory.getLogger(XssFilterAutoConfiguration.class);
  
  public static final List<String> DEFAULT_PATTERNS = Collections.unmodifiableList(Arrays.asList(new String[] { 
          "eval\\((.*?)\\)", "expression\\((.*?)\\)", "javascript:", "vbscript:", "onload(.*?)=", "prompt\\((.*?)\\)", "\\(\\s*alert\\s*\\).*?", "alert\\s*\\(.*?\\)", "echo\\((.*?)\\)", "<html(.*?)>", 
          "</html>", "<h1(.*?)>", "</h1>", "<style>", "<a\\s", "<div\\s", "<embed\\s", "<iframe", "<input\\s(.*?)\"", "<img", 
          "<layer\\s", "<link\\s", "<meta\\s", "<object\\s", "<script", "</script>", "<span\\s", "<var\\s", "<vmlframe ", "<xml\\s", 
          "<xss\\s", "<x\\s(.*?)>", "<\\?import (.*?)>", "<\\?xml(.*?)>", "<!--\\[(.*?)\\](.*?)>", "<!\\[(.*?)\\](.*?)-->", "Redirect\\s+?30\\d", "top\\[(.*?)]", "header\\((.*?)get\\[(.*?)]", "\\s*?(\"|'{2});!--(\"|'{2})(.*?)\\<(.*?)\\>=&\\{\\(\\)\\}", 
          "(data:)(.*?)(base64)", "<a " }));
  
  private List<String> disabledPatterns = new ArrayList<>();
  
  private List<String> customPatterns = new ArrayList<>();
  
  private List<String> excludePaths = new ArrayList<>();
  
  private boolean autoconfig = true;
  
  private boolean esapi = true;
  
  public List<Pattern> createPatterns() {
    List<Pattern> patterns = new ArrayList<>();
    List<String> patternStrings = new ArrayList<>();
    patternStrings.addAll(DEFAULT_PATTERNS);
    patternStrings.addAll(this.customPatterns);
    patternStrings.removeAll(this.disabledPatterns);
    for (String patternString : patternStrings) {
      try {
        patterns.add(Pattern.compile(patternString, 34));
      } catch (IllegalArgumentException e) {
        log.error("Exception whilst trying to load XSS Filter Patterns. Invalid pattern {}: {}", new Object[] { patternString, e
              .getMessage(), e });
      } 
    } 
    if (log.isDebugEnabled()) {
      log.debug("The following patterns will be used:");
      for (Pattern pattern : patterns)
        log.debug(pattern.pattern()); 
    } 
    return patterns;
  }
  
  @Bean
  public XssFilter xssFilter(@Qualifier("xssFilterExcludeRequestMatcher") RequestMatcher excludeRequestMatcher) {
    XssFilter xssFilter = new XssFilter();
    xssFilter.setPatterns(createPatterns());
    xssFilter.setUseEsapi(this.esapi);
    xssFilter.setExcludeRequestMatcher(excludeRequestMatcher);
    return xssFilter;
  }
  
  @Bean(name = {"xssFilterExcludeRequestMatcher"})
  @ConditionalOnMissingBean(name = {"xssFilterExcludeRequestMatcher"})
  public RequestMatcher xssFilterExcludeRequestMatcher() {
    if (this.excludePaths.isEmpty())
      return request -> false; 
    if (this.excludePaths.size() == 1)
      return (RequestMatcher)new AntPathRequestMatcher(this.excludePaths.get(0)); 
    return (RequestMatcher)new OrRequestMatcher(matchers(this.excludePaths));
  }
  
  private List<RequestMatcher> matchers(List<String> excludePaths) {
    List<RequestMatcher> matchers = new ArrayList<>(excludePaths.size());
    for (String path : excludePaths)
      matchers.add(new AntPathRequestMatcher(path)); 
    return matchers;
  }
  
  public List<String> getDisabledPatterns() {
    return this.disabledPatterns;
  }
  
  public void setDisabledPatterns(List<String> disabledPatterns) {
    this.disabledPatterns = disabledPatterns;
  }
  
  public List<String> getCustomPatterns() {
    return this.customPatterns;
  }
  
  public void setCustomPatterns(List<String> customPatterns) {
    this.customPatterns = customPatterns;
  }
  
  public boolean isEsapi() {
    return this.esapi;
  }
  
  public void setEsapi(boolean esapi) {
    this.esapi = esapi;
  }
  
  public List<String> getExcludePaths() {
    return this.excludePaths;
  }
  
  public void setExcludePaths(List<String> excludePaths) {
    this.excludePaths = excludePaths;
  }
  
  public boolean isAutoconfig() {
    return this.autoconfig;
  }
  
  public void setAutoconfig(boolean autoconfig) {
    this.autoconfig = autoconfig;
  }
}
