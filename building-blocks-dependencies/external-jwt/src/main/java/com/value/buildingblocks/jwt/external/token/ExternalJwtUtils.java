package com.value.buildingblocks.jwt.external.token;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExternalJwtUtils {
  private static final Logger logger = LoggerFactory.getLogger(ExternalJwtUtils.class);
  
  public static boolean isTokenExpired(ExternalJwtClaimsSet claimsSet, ZonedDateTime currentTime)
    throws IllegalArgumentException {
    if (!claimsSet.getExpirationTime().isPresent()) {
      logger.warn("Json Web Token doesn't have expiration date");
      return true;
    } 
    long currentUtcTime = getUtcEpochSecond(currentTime);
    ZonedDateTime expirationTime = claimsSet.getExpirationTime().orElseThrow(() -> new IllegalArgumentException("Expiration time claim required."));
    long expirationUtcTime = getUtcEpochSecond(expirationTime);
    long notValidAfterUtcTime = getUtcEpochSecond(claimsSet.getNotAfterTime().orElse(expirationTime));
    return (currentUtcTime > notValidAfterUtcTime || currentUtcTime > expirationUtcTime);
  }
  
  public static Boolean isReadyForRenew(ExternalJwtClaimsSet claimsSet, ZonedDateTime currentTime, long renewPeriodSec)
    throws IllegalArgumentException {
    if (isTokenExpired(claimsSet, currentTime))
      return Boolean.valueOf(false); 
    long currentUtcTime = getUtcEpochSecond(currentTime);
    long expirationUtcTime = getUtcEpochSecond(claimsSet.getExpirationTime()
        .orElseThrow(() -> new IllegalArgumentException("Expiration time claim required.")));
    long renewTime = expirationUtcTime - renewPeriodSec;
    return Boolean.valueOf((currentUtcTime >= renewTime));
  }
  
  public static Optional<Integer> getExpiresInSec(ExternalJwtClaimsSet claimsSet) {
    Optional<ZonedDateTime> issuedAt = claimsSet.getIssuedAt();
    Optional<ZonedDateTime> expirationTime = claimsSet.getExpirationTime();
    if (issuedAt.isPresent() && expirationTime.isPresent())
      return Optional.of(Integer.valueOf((int)(((ZonedDateTime)expirationTime.get()).toEpochSecond() - ((ZonedDateTime)issuedAt.get()).toEpochSecond()))); 
    return Optional.empty();
  }
  
  public static Optional<Integer> getCookieExpiresInSec(ExternalJwtClaimsSet claimsSet, ZonedDateTime currentTime) {
    Optional<Integer> expiresInSec = getExpiresInSec(claimsSet);
    if (expiresInSec.isPresent()) {
      Optional<ZonedDateTime> notAfterTime = claimsSet.getNotAfterTime();
      if (notAfterTime.isPresent() && currentTime != null) {
        int expiresPermanentlyInSec = (int)(((ZonedDateTime)notAfterTime.get()).toEpochSecond() - currentTime.toEpochSecond());
        if (expiresPermanentlyInSec > 0 && expiresPermanentlyInSec < ((Integer)expiresInSec.get()).intValue())
          return Optional.of(Integer.valueOf(expiresPermanentlyInSec)); 
      } 
      return expiresInSec;
    } 
    return Optional.empty();
  }
  
  public static long getUtcEpochSecond(ZonedDateTime time) {
    return time.withZoneSameInstant(ZoneOffset.UTC).toEpochSecond();
  }
  
  public static ZonedDateTime getCurrentUtc() {
    return ZonedDateTime.now(ZoneOffset.UTC);
  }
  
  public static Optional<Instant> getTimeInstantClaim(ExternalJwtClaimsSet claimsSet, String claimName) {
    Optional<Object> claim = claimsSet.getClaim(claimName);
    if (claim.isPresent()) {
      if (claim.get() instanceof Date) {
        Date claimDate = (Date)claim.get();
        return Optional.of(Instant.ofEpochMilli(claimDate.getTime()));
      } 
      if (claim.get() instanceof Long)
        return Optional.of(Instant.ofEpochSecond(((Long)claim.get()).longValue())); 
    } 
    return Optional.empty();
  }
}
