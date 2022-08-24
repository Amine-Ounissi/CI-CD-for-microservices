package com.value.buildingblocks.jwt.external;

import com.value.buildingblocks.jwt.core.exception.JsonWebTokenException;
import com.value.buildingblocks.jwt.external.exception.ExternalJwtException;
import com.value.buildingblocks.jwt.external.token.ExternalJwt;

public interface ExternalJwtConsumer {
  ExternalJwt parseToken(String paramString) throws ExternalJwtException, JsonWebTokenException;
}
