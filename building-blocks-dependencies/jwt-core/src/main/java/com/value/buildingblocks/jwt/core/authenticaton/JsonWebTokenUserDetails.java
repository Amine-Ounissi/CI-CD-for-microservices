package com.value.buildingblocks.jwt.core.authenticaton;

import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class JsonWebTokenUserDetails implements UserDetails {
  private String userName;
  
  private Collection<? extends GrantedAuthority> authorities;
  
  private boolean accountNonExpired = true;
  
  private boolean accountNonLocked = true;
  
  private boolean credentialsNonExpired = true;
  
  private boolean enabled = true;
  
  public JsonWebTokenUserDetails(String userName, Collection<? extends GrantedAuthority> authorities) {
    this.userName = userName;
    this.authorities = Collections.unmodifiableCollection(authorities);
  }
  
  public JsonWebTokenUserDetails(String userName, Collection<? extends GrantedAuthority> authorities, boolean accountNonExpired, boolean accountNonLocked, boolean credentialsNonExpired, boolean enabled) {
    this(userName, authorities);
    this.accountNonExpired = accountNonExpired;
    this.accountNonLocked = accountNonLocked;
    this.credentialsNonExpired = credentialsNonExpired;
    this.enabled = enabled;
  }
  
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.authorities;
  }
  
  public String getPassword() {
    return null;
  }
  
  public String getUsername() {
    return this.userName;
  }
  
  public boolean isAccountNonExpired() {
    return this.accountNonExpired;
  }
  
  public boolean isAccountNonLocked() {
    return this.accountNonLocked;
  }
  
  public boolean isCredentialsNonExpired() {
    return this.credentialsNonExpired;
  }
  
  public boolean isEnabled() {
    return this.enabled;
  }
}
