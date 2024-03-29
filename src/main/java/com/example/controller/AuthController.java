package com.example.controller;

import com.example.dto.request.ChangePasswordRequest;
import com.example.dto.request.ResetPasswordRequest;
import com.example.model.ERole;
import com.example.model.Role;
import com.example.model.Users;
import com.example.dto.request.LoginRequest;
import com.example.dto.request.SignupRequest;
import com.example.dto.response.JwtResponse;
import com.example.dto.response.MessageResponse;
import com.example.repository.RoleDAO;
import com.example.repository.UserDAO;
import com.example.security.jwt.JwtUtils;
import com.example.security.service.IStorageService;
import com.example.security.service.impl.UserDetailsImpl;
import com.example.security.service.UserServices;
import com.example.security.service.impl.UserDetailsServiceImpl;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserDAO userDAO;

  @Autowired
  RoleDAO roleDAO;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  UserServices userServices;

  @Autowired
  IStorageService iStorageService;

  @Autowired
  UserDetailsServiceImpl userDetailsService;
  @Value("${server.frontend}")
  private String getSiteURL;

  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    try {
      Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword()));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      String jwt = jwtUtils.generateJwtToken(authentication);
      UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
      List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());
      userDetailsService.resetFailedAttempts(loginRequest.getUserName());
      return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), userDetails.getFullName(), roles, userDetails.getAvatar()));
    } catch (BadCredentialsException ex) {
      Users user = userDAO.findByUsername(loginRequest.getUserName()).orElseThrow(() -> new RuntimeException("USER NOT FOUND"));
      if (user != null) {
        if (user.getEnabled() && user.getAccountNonLocked()) {
          if (user.getFailedAttempt() < userDetailsService.MAX_FAILED_ATTEMPTS - 1) {
            userDetailsService.increaseFailedAttempts(user);
          } else {
            userDetailsService.lock(user);
            return ResponseEntity.badRequest().body(new MessageResponse("Your account has been locked.Try again after 24 hours"));
          }
        } else if (!user.getAccountNonLocked()) {
          if (userDetailsService.unlockWhenTimeExpired(user)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Your account has been unlocked. Please try to login again."));
          }
        }

      }
      if(!user.getAccountNonLocked()){
        return ResponseEntity.badRequest().body(new MessageResponse("Your account has been locked!"));
      }

      return ResponseEntity.badRequest().body(new MessageResponse("Error: Bad Credentials!"));
    }
  }


  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) throws MessagingException, UnsupportedEncodingException {
    if (userDAO.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
    }

    if (userDAO.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
    }
    // Create new user's account
    Users user = new Users(signUpRequest.getUsername(), signUpRequest.getEmail(), signUpRequest.getFullName(), encoder.encode(signUpRequest.getPassword()));
    user.setFullName(signUpRequest.getFullName());
    user.setFailedAttempt(0);
    user.setAccountNonLocked(true);
    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleDAO.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
          case "admin":
            Role adminRole = roleDAO.findByName(ERole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(adminRole);
            break;
          case "moderator":
            Role moderatorRole = roleDAO.findByName(ERole.ROLE_MODERATOR).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(moderatorRole);
            break;
          case "user":
            Role userRole = roleDAO.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        }
      });
    }

    user.setRoles(roles);
    userServices.register(user, getSiteURL);
    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }

  @GetMapping("/verify")
  public ResponseEntity<?> verifyUser(@Param("code") String code) {
    if (userServices.verify(code)) {
      return ResponseEntity.ok(new MessageResponse("Verify successfully!"));
    } else {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Verify fail!"));
    }
  }

  @PostMapping("/forget-password")
  public ResponseEntity<?> forgetPassword(@Valid @RequestBody String email) throws MessagingException, UnsupportedEncodingException {
    try {
      String token = RandomString.make(30);
      userServices.updateResetPasswordToken(token, email);
      String resetPasswordLink = getSiteURL + "/reset?token=" + token;
      userServices.sendEmail(email, resetPasswordLink);
      return ResponseEntity.ok(new MessageResponse("Email sent!"));
    } catch (Exception ex) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Not Found Email"));
    }

  }

  @GetMapping("/reset-password")
  public ResponseEntity<?> showResetPasswordForm(@Param(value = "token") String token) {
    Users users = userServices.getByResetPasswordToken(token);
    if (users == null) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid Token!"));
    }
    return ResponseEntity.ok(new MessageResponse("Reset successfully!"));
  }

  @PostMapping("/reset-password")
  public ResponseEntity<?> processResetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
    Users users = userServices.getByResetPasswordToken(resetPasswordRequest.getToken());
    if (users == null) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid Token!"));
    } else {
      userServices.updatePassword(users, resetPasswordRequest.getPassword());
      return ResponseEntity.ok(new MessageResponse("You have successfully changed your password!"));
    }
  }

  @PostMapping("/change-password")
  public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
    Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
        changePasswordRequest.getUserName(), changePasswordRequest.getOldPassword()));
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    if (userDetails == null) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Password not match!"));
    } else {
      userServices.changePassword(changePasswordRequest.getUserId(), changePasswordRequest.getNewPassword());
      return ResponseEntity.ok(new MessageResponse("Change password successfully!"));
    }

  }


}
