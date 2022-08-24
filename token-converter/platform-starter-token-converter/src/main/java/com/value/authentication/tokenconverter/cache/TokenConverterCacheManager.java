package com.value.authentication.tokenconverter.cache;

import com.value.buildingblocks.multitenancy.caching.TenantAwareCacheKeyDecorator;
import com.value.authentication.tokenconverter.configuration.TokenConverterProperties;
import com.value.authentication.tokenconverter.exception.TokenConverterConfigurationException;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.NoOpCache;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class TokenConverterCacheManager implements CacheManager {
  public static final String USER_INFO_BY_INTERNAL_ID_CACHE = "userInfoByInternalIdCache";
  
  public static final String USER_INFO_BY_EXTERNAL_ID_CACHE = "userInfoByExternalIdCache";
  
  public static final String ACCESS_TOKEN_CACHE = "accessTokenCache";
  
  private static final Logger log = LoggerFactory.getLogger(TokenConverterCacheManager.class);
  
  private final Cache userInfoByExternalIdCache;
  
  private final Cache userInfoByInternalIdCache;
  
  private final Cache accessTokenCache;
  
  public TokenConverterCacheManager(TokenConverterProperties tokenConverterProperties) {
    this.userInfoByExternalIdCache = userInfoCache("userInfoByExternalIdCache", tokenConverterProperties);
    this.userInfoByInternalIdCache = userInfoCache("userInfoByInternalIdCache", tokenConverterProperties);
    this.accessTokenCache = accessTokenCache(tokenConverterProperties);
  }
  
  @Nullable
  public Cache getCache(@NonNull String name) {
    switch (name) {
      case "userInfoByExternalIdCache":
        return this.userInfoByExternalIdCache;
      case "userInfoByInternalIdCache":
        return this.userInfoByInternalIdCache;
      case "accessTokenCache":
        return this.accessTokenCache;
    } 
    log.info("An unknown cache has been requested to the cache manager. Name: [{}]", name);
    return null;
  }

  public Collection<String> getCacheNames() {
    return Collections.unmodifiableList(
        Arrays.asList("userInfoByExternalIdCache", "userInfoByInternalIdCache", "accessTokenCache"));
  }
  
  private Cache accessTokenCache(TokenConverterProperties tokenConverterProperties) {
    TokenConverterProperties.Cache cache = tokenConverterProperties.getCache();
    if (!cache.isEnabled())
      return new NoOpCache("accessTokenCache");
    return new TenantAwareCacheKeyDecorator(new CaffeineCache("accessTokenCache",
          createNewCaffeineFromTokenProperties(cache.getCacheSpecs(), cache.getTtl(), cache.getMaximumSize()), false));
  }
  
  private Cache userInfoCache(String cacheName, TokenConverterProperties tokenConverterProperties) {
    TokenConverterProperties.Dbs dbs = tokenConverterProperties.getDbs();
    if (!dbs.isPropagationEnabled())
      return new NoOpCache(cacheName);
    return new TenantAwareCacheKeyDecorator(new CaffeineCache(cacheName,
          createNewCaffeineFromTokenProperties(dbs.getCacheSpecs(), dbs.getTtl(), dbs.getMaximumSize()), false));
  }
  
  private com.github.benmanes.caffeine.cache.Cache<Object, Object> createNewCaffeineFromTokenProperties(String cacheSpecs, String ttl, int maximumSIze) {
    if (cacheSpecs != null)
      return Caffeine.from(cacheSpecs).build(); 
    return Caffeine.newBuilder().expireAfterWrite(parseTime(ttl)).maximumSize(maximumSIze).build();
  }
  
  private Duration parseTime(String ttl) {
    int lastCharPos = ttl.length() - 1;
    try {
      int value = Integer.parseInt(ttl.substring(0, lastCharPos));
      char lastChar = Character.toLowerCase(ttl.charAt(lastCharPos));
      switch (lastChar) {
        case 'd':
          return Duration.of(value, ChronoUnit.DAYS);
        case 'h':
          return Duration.of(value, ChronoUnit.HOURS);
        case 'm':
          return Duration.of(value, ChronoUnit.MINUTES);
        case 's':
          return Duration.of(value, ChronoUnit.SECONDS);
      } 
      log.warn("Unparseable time duration [{}]", lastChar);
    } catch (Exception e) {
      log.warn("Exception thrown during TTL parsing. Unparseable duration [{}]", ttl, e);
    } 
    throw new TokenConverterConfigurationException(String.format("Wrong configuration value for `value.token-converter.dbs.ttl`: [%s]", new Object[] { ttl }));
  }
}
