package com.example.dto.request;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
  private Long userId;
  private String userName;
  private String oldPassword;
  private String newPassword;
  private String confirmPassword;
}
