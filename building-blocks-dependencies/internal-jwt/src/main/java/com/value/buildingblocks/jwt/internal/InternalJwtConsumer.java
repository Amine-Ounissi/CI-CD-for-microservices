package com.value.buildingblocks.jwt.internal;

import com.value.buildingblocks.jwt.core.exception.JsonWebTokenException;
import com.value.buildingblocks.jwt.internal.exception.InternalJwtException;
import com.value.buildingblocks.jwt.internal.token.InternalJwt;

public interface InternalJwtConsumer {
  InternalJwt parseToken(String paramString) throws InternalJwtException, JsonWebTokenException;
}
