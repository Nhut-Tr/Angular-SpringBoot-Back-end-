package com.example.controller;

import java.util.*;

import com.example.dto.request.SignupRequest;
import com.example.dto.request.UpdateProfileDTO;
import com.example.dto.response.MessageResponse;
import com.example.model.ERole;
import com.example.model.Role;
import com.example.model.Users;
import com.example.repository.RoleDAO;
import com.example.repository.UserDAO;
import com.example.security.service.IStorageService;
import com.example.security.service.UserDetailsImpl;
import com.example.security.service.UserDetailsServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RequestMapping("/")
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
  @Autowired
  private UserDAO userDAO;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  RoleDAO roleDAO;

  @Autowired
  IStorageService iStorageService;

  @GetMapping("/users")
  public List<Users> list() {
    return userDAO.findAll();
  }


  @GetMapping("/user/{id}")
  public ResponseEntity<Users> getUserById(@PathVariable Long id) {
    Optional<Users> users = userDAO.findById(id);
    if (users.isPresent()) {
      return new ResponseEntity<>(users.get(), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping("/search-user-name")
  public List<Users> findUserName(@Param("userName") String userName) {
    return userDAO.findUserName(userName);
  }

  @PutMapping("/admin/update-user/{id}")
  public ResponseEntity<Users> updateUser(@PathVariable(name = "id") Long id, @RequestBody Users users) {
    Optional<Users> usersData = userDAO.findById(id);
    if (usersData.isPresent()) {
      Users user = usersData.get();
      user.setUsername(users.getUsername());
      user.setFullName(users.getFullName());
      user.setRole(users.getRole());
      return new ResponseEntity<>(userDAO.save(user), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @PostMapping("/admin/add-user")
  public ResponseEntity<?> addUser(@RequestBody SignupRequest signUpRequest) {
    if (userDAO.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
    }

    if (userDAO.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
    }

    // Create new user's account
    Users user = new Users(signUpRequest.getUsername(), signUpRequest.getEmail(), signUpRequest.getFullName(), encoder.encode(signUpRequest.getPassword()));
    user.setFullName(signUpRequest.getFullName());
    user.setEnabled(true);
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

    user.setRole(roles);
    userDAO.save(user);
    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }
  private Users getPrincipal() {
    Users user = null;
    if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Users) {
      user = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    return user;
  }

  @PutMapping(value="/update-user",consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public ResponseEntity<Users> updateProfile(@RequestPart("user") String updateProfileRequest, @RequestPart(value = "file",required = false) MultipartFile file) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    UpdateProfileDTO updateProfileDTO = objectMapper.readValue(updateProfileRequest, UpdateProfileDTO.class);
//    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<Users> usersData = userDAO.findById(updateProfileDTO.getId());
    String avatar = iStorageService.storeFile(file);
    if (usersData.isPresent()) {
      Users user = usersData.get();
      user.setFullName(updateProfileDTO.getFullName());
      user.setAvatar(avatar);
      return new ResponseEntity<>(userDAO.save(user), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }


  @DeleteMapping("/user/{id}")
  public ResponseEntity<HttpStatus> deleteUser(@PathVariable Long id) {
    try {
      userDAO.deleteById(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception ex) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  @DeleteMapping("/user")
  public ResponseEntity<HttpStatus> deleteUser() {
    try {
      userDAO.deleteAll();
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception ex) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
