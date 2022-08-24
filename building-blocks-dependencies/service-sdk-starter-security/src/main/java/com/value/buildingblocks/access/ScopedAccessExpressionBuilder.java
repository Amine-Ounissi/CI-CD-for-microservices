package com.value.buildingblocks.access;

import java.util.Arrays;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;

public class ScopedAccessExpressionBuilder {
  private static final String EXP_HAS_ALL_SCOPES = "hasUserScopes(";
  
  private static final String EXP_HAS_ANY_SCOPES = "hasAnyUserScopes(";
  
  private static final String EXP_AND = " and ";
  
  private ScopedAccessProperties.AccessRule rule;
  
  public ScopedAccessExpressionBuilder withAccessRule(ScopedAccessProperties.AccessRule rule) {
    this.rule = rule;
    return this;
  }
  
  public String build() {
    UnaryOperator<String> addQuotes = s -> "'" + s + "'";
    StringBuilder builder = new StringBuilder();
    if (this.rule != null) {
      if (ArrayUtils.isNotEmpty((Object[])this.rule.getRequire())) {
        String allowed = Arrays.<String>stream(this.rule.getRequire()).map(addQuotes).collect(Collectors.joining(","));
        builder.append("hasAnyUserScopes(").append(allowed);
        builder.append(")");
      } 
      if (ArrayUtils.isNotEmpty((Object[])this.rule.getDeny())) {
        maybeAnd(builder);
        String denied = Arrays.<String>stream(this.rule.getDeny()).map(addQuotes).collect(Collectors.joining(","));
        builder.append("!hasAnyUserScopes(").append(denied);
        builder.append(")");
      } 
      if (this.rule.getExpression() != null && !this.rule.getExpression().isEmpty()) {
        maybeAnd(builder);
        if (builder.length() > 0) {
          builder.append("(").append(this.rule.getExpression()).append(")");
        } else {
          builder.append(this.rule.getExpression());
        } 
      } 
    } 
    return builder.toString();
  }
  
  private void maybeAnd(StringBuilder builder) {
    if (builder.length() > 0)
      builder.append(" and "); 
  }
}
