package com.value.buildingblocks.retry;

import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.NoBackOffPolicy;

public interface RetryBackOffPolicyFactory {
  default BackOffPolicy createBackOffPolicy(String service) {
    return (BackOffPolicy)new NoBackOffPolicy();
  }
}
