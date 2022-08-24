package com.value.buildingblocks.jwt.core.type;

import com.value.buildingblocks.jwt.core.exception.TokenKeyException;
import java.lang.reflect.Constructor;
import java.security.Provider;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SecurityProviderFactory {
  private static final Logger logger = LoggerFactory.getLogger(SecurityProviderFactory.class);
  
  private static final String ORACLE_PKCS11 = "sun.security.pkcs11.SunPKCS11";
  
  private static final String IBM_PKCS11 = "com.ibm.crypto.pkcs11impl.provider.IBMPKCS11Impl";
  
  public static Provider getProviderPkcs11(String configFile) {
    Optional<Provider> provider = getSecurityProviderPkcs11(configFile, "sun.security.pkcs11.SunPKCS11");
    if (provider.isPresent())
      return provider.get(); 
    provider = getSecurityProviderPkcs11(configFile, "com.ibm.crypto.pkcs11impl.provider.IBMPKCS11Impl");
    if (provider.isPresent())
      return provider.get(); 
    throw new TokenKeyException("Unsupported PKCS11 Security Provider (currently supported: Oracle, IBM)");
  }
  
  private static Optional<Provider> getSecurityProviderPkcs11(String configFile, String fromClass) {
    Class<?> clazz;
    Constructor<?> constructor;
    try {
      clazz = Class.forName(fromClass);
    } catch (ClassNotFoundException e) {
      logger.debug("Can't find PKCS11 Security Provider {}", fromClass);
      return Optional.empty();
    } 
    try {
      constructor = clazz.getDeclaredConstructor(new Class[] { String.class });
    } catch (NoSuchMethodException e) {
      logger.debug("Can't access PKCS11 Security Provider class constructor", e);
      return Optional.empty();
    } 
    try {
      return Optional.ofNullable((Provider)constructor.newInstance(new Object[] { configFile }));
    } catch (IllegalAccessException|InstantiationException|java.lang.reflect.InvocationTargetException e) {
      logger.debug("Can't create instance of PKCS11 Security Provider", e);
      return Optional.empty();
    } 
  }
}
