package com.value.authentication.tokenconverter.api;

import com.value.authentication.tokenconverter.configuration.TokenConverterProperties;
import com.value.authentication.tokenconverter.enhancer.ResponseEnhancer;
import com.value.authentication.tokenconverter.exception.TokenConverterValidationException;
import com.value.authentication.tokenconverter.internaltoken.InternalTokenHandler;
import com.value.authentication.tokenconverter.internaltoken.model.InternalTokenResponse;
import com.value.authentication.tokenconverter.verifyonly.VerifyOnlyService;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Generated;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

@RestController
public class TokenConverterController {
  @Generated
  private static final Logger log = LoggerFactory.getLogger(TokenConverterController.class);
  
  private final InternalTokenHandler internalTokenHandler;
  
  private final TokenConverterProperties tokenConverterProperties;
  
  private List<ResponseEnhancer> responseEnhancerList;
  
  private final VerifyOnlyService verifyOnlyService;
  
  public TokenConverterController(InternalTokenHandler internalTokenHandler, TokenConverterProperties tokenConverterProperties, List<ResponseEnhancer> responseEnhancerList, VerifyOnlyService verifyOnlyService) {
    this.internalTokenHandler = internalTokenHandler;
    this.tokenConverterProperties = tokenConverterProperties;
    this.responseEnhancerList = responseEnhancerList;
    this.verifyOnlyService = verifyOnlyService;
  }
  
  @RequestMapping({"/convert"})
  public void convert(HttpServletRequest request, HttpServletResponse response) throws TokenConverterValidationException {
    SecurityContext context = SecurityContextHolder.getContext();
    if (context == null || context
      .getAuthentication() == null || context
      .getAuthentication() instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)
      return; 
    Cookie userCtxCookie = WebUtils.getCookie(request, "USER_CONTEXT");
    String userCtxJwe = (userCtxCookie != null) ? DigestUtils.sha1Hex(userCtxCookie.getValue()) : null;
    String externalToken = request.getHeader(this.tokenConverterProperties.getAuthorizationRequestHeaderName());
    InternalTokenResponse internalTokenResponse = this.internalTokenHandler.create(externalToken, userCtxJwe, context.getAuthentication());
    String token = internalTokenResponse.getInternalToken();
    if (!CollectionUtils.isEmpty(internalTokenResponse.getClaims()) && 
      StringUtils.isNotBlank(token) && this.verifyOnlyService
      .isVerifyOnlyToken(internalTokenResponse.getClaims())) {
      this.verifyOnlyService.addVerifyOnlyHeader(response);
      log.debug("Setting token verified header in place of internal token");
    } else {
      response.addHeader(this.tokenConverterProperties.getAuthorizationResponseHeaderName(), "Bearer " + token);
      log.debug("Adding downstream bearer header for internal token with name [{}]", this.tokenConverterProperties
          .getAuthorizationResponseHeaderName());
    } 
    for (ResponseEnhancer item : this.responseEnhancerList) {
      log.debug("Enhancing response with {}", item.getClass());
      if (item.getHeaders() != null)
        item.getHeaders().forEach(response::addHeader); 
      if (item.getCookies() != null)
        item.getCookies().forEach(response::addCookie); 
    } 
  }
}
