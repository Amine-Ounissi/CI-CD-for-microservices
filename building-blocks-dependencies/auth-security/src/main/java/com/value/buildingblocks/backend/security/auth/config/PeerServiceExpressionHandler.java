package com.value.buildingblocks.backend.security.auth.config;

import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

public abstract class PeerServiceExpressionHandler extends DefaultWebSecurityExpressionHandler {

  protected abstract boolean isPeerService(Authentication paramAuthentication);

  protected final StandardEvaluationContext createEvaluationContextInternal(Authentication auth,
    FilterInvocation invocation) {
    StandardEvaluationContext ctx = super.createEvaluationContextInternal(auth, invocation);
    ctx.setVariable("peerService", isPeerService(auth));
    return ctx;
  }
}
