package com.value.buildingblocks.jwt.core;

import com.value.buildingblocks.jwt.core.exception.JsonWebTokenException;

public interface JsonWebTokenProducerType<T, R> {
  R createToken(T paramT) throws JsonWebTokenException;
}
