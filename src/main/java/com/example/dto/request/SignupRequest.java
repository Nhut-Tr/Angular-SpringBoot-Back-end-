package com.example.dto.request;

import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
  @NotBlank
  private String userName;

  @NotBlank
  @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "wrong format, should be abc@abc.com ")
  private String email;

  private Set<String> role;

  @NotBlank
  private String password;

  @NotBlank
  private String fullName;




}
