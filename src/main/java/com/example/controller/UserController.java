package com.example.controller;

import java.util.*;
import com.example.dto.request.SignupRequest;
import com.example.dto.request.UpdateProfileDTO;
import com.example.dto.response.MessageResponse;
import com.example.model.*;
import com.example.repository.RoleDAO;
import com.example.repository.UserDAO;
import com.example.security.service.IStorageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
  public Page<Users> list(@RequestParam("page") int page, @RequestParam("size") int size) {
    Pageable pageable = PageRequest.of(page,size);
    return userDAO.findAll(pageable);
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
  public Page<Users> findUserName(@Param("userName") String userName, @RequestParam("page") int page, @RequestParam("size") int size) {
    Pageable pageable = PageRequest.of(page,size);
    return userDAO.findUserName(userName,pageable);
  }

  @GetMapping("/search-role")
  public Page<Users> findByRole(@RequestParam("id") Integer id, @RequestParam("page") int page, @RequestParam("size") int size) {
    Pageable pageable = PageRequest.of(page,size);
    return userDAO.findByRolesId(id,pageable);
  }

  @GetMapping("/search-email")
  public Page<Users> findUserByEmail(@Param("email") String email, @RequestParam("page") int page, @RequestParam("size") int size) {
    Pageable pageable = PageRequest.of(page,size);
    return userDAO.findUserByEmail(email,pageable);
  }
  @GetMapping("/find-user-by-status")
  public Page<Users> findByStatus(@RequestParam("enabled") Boolean enabled, @RequestParam("page") int page, @RequestParam("size") int size){
    Pageable pageable = PageRequest.of(page,size);
    return userDAO.findByEnabled(enabled,pageable);
  }

  @GetMapping("/search-all")
  public Page<Users> findUserAll(@Param("userName") String userName,@Param("email") String email,@Param("roleId") String roleId,@Param("enabled") String enabled, @RequestParam("page") int page, @RequestParam("size") int size) {
    Pageable pageable = PageRequest.of(page,size);
    if(roleId=="" && enabled==""){
      return userDAO.findByUsernameContainingAndEmailContaining(userName,email,pageable);
    }
    if(roleId.equals("")){
       return userDAO.findByUsernameContainingAndEmailContainingAndEnabled(userName,email,Boolean.parseBoolean(enabled),pageable);
    }
    if(enabled.equals("")){
      return userDAO.findByUsernameContainingAndEmailContainingAndRolesId(userName,email,Integer.parseInt(roleId),pageable);
    }

    return userDAO.findByUsernameContainingAndEmailContainingAndRolesIdAndEnabled(userName,email,Integer.parseInt(roleId),Boolean.parseBoolean(enabled),pageable);
  }
  @PutMapping("/admin/update-user/{id}")
  public ResponseEntity<Users> updateUser(@PathVariable(name = "id") Long id, @RequestBody Users users) {
    Optional<Users> usersData = userDAO.findById(id);
    if (usersData.isPresent()) {
      Users user = usersData.get();
      user.setUsername(users.getUsername());
      user.setFullName(users.getFullName());
      user.setEnabled(users.getEnabled());
      user.setRoles(users.getRoles());
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

    user.setRoles(roles);
    userDAO.save(user);
    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }


  @PutMapping(value="/update-user",consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public ResponseEntity<Users> updateProfile(@RequestPart("user") String updateProfileRequest, @RequestPart(value = "file",required = false) MultipartFile file) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    UpdateProfileDTO updateProfileDTO = objectMapper.readValue(updateProfileRequest, UpdateProfileDTO.class);
    Optional<Users> usersData = userDAO.findById(updateProfileDTO.getId());
    String avatar="";
    if(file != null){
       avatar = iStorageService.storeFile(file);
    }

    if (usersData.isPresent()) {
      Users user = usersData.get();
      user.setFullName(updateProfileDTO.getFullName());
      if(file != null){
        user.setAvatar(avatar);
      }

      return new ResponseEntity<>(userDAO.save(user), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @PutMapping("/user-status/{id}")
  public ResponseEntity<Users> updateStatus(@PathVariable(name = "id") Long id) {
    Optional<Users> userData = userDAO.findById(id);
    if (userData.isPresent()) {
      Users users = userData.get();
      users.setEnabled(false);
      return new ResponseEntity<>(userDAO.save(users), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }
  @GetMapping("/user-list-deactivated")
  public List<Users> listDeactivated() {
    return userDAO.findAllByEnabled(false);
  }

  @GetMapping("/user-list-activated")
  public List<Users> listActivated() {
    return userDAO.findAllByEnabled(true);
  }



}
