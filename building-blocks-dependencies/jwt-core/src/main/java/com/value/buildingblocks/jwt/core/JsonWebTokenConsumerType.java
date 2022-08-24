package com.value.buildingblocks.jwt.core;

import com.value.buildingblocks.jwt.core.exception.JsonWebTokenException;

public interface JsonWebTokenConsumerType<T, R> {
  R parseToken(T paramT) throws JsonWebTokenException;
}
