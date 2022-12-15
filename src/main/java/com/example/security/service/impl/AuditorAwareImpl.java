package com.example.security.service.impl;

import com.example.model.Users;
import com.example.repository.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
  @Autowired
  UserDAO userDAO;
  @Autowired
  AuthenticationManager authenticationManager;
  @Override
  public Optional<String> getCurrentAuditor() {
    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    return Optional.of(userDAO.findByUsername(username).get().getUsername());
  }
}
