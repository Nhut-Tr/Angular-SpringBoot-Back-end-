package com.example.security.service.impl;

import com.example.model.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
  private static final long serialVersionUID = 1L;

  private Long id;

  private String userName;

  private String email;
  private String fullName;
  @JsonIgnore
  private String password;

  private Boolean isEnabled;

  private String avatar;
  private Collection<? extends GrantedAuthority> authorities;

  public UserDetailsImpl(Long id, String userName, String email, String password, String fullName,
                         Boolean isEnabled,String avatar,
                         Collection<? extends GrantedAuthority> authorities) {
    this.id = id;
    this.userName = userName;
    this.email = email;
    this.password = password;
    this.authorities = authorities;
    this.fullName = fullName;
    this.isEnabled = isEnabled;
    this.avatar = avatar;
  }

  public static UserDetailsImpl build(Users user) {
    List<GrantedAuthority> authorities = user.getRoles().stream()
        .map(role -> new SimpleGrantedAuthority(role.getName().name()))
        .collect(Collectors.toList());

    return new UserDetailsImpl(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getPassword(),
        user.getFullName(),
        user.getEnabled(),
        user.getAvatar(),
        authorities);
  }


  private Users users;

  public UserDetailsImpl(Users users) {
    this.users = users;
  }


  @Override
  public boolean isEnabled() {
    return isEnabled;
  }


  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }
  public String getAvatar(){
    return avatar;
  }
  @Override
  public String getPassword() {
    return password;
  }

	public String getFullName(){
		return fullName;
	}

  @Override
  public String getUsername() {
    return userName;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    UserDetailsImpl user = (UserDetailsImpl) o;
    return Objects.equals(id, user.id);
  }
}