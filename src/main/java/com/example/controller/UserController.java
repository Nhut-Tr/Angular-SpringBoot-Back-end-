package com.example.controller;

import java.util.*;

import com.example.model.Users;
import com.example.repository.RoleDAO;
import com.example.repository.UserDAO;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
  public List<Users> findUserName(@Param("userName") String userName){
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
  @PutMapping("/update-user/{id}")
  public ResponseEntity<Users> updateProfile(@PathVariable(name = "id") Long id, @RequestBody Users users) {
    Optional<Users> usersData = userDAO.findById(id);
    if (usersData.isPresent()) {
      Users user = usersData.get();
      user.setUsername(users.getUsername());
      user.setFullName(users.getFullName());
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
