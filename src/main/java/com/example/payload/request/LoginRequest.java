package com.example.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class LoginRequest {
	@NotBlank
	private String username;

	@NotBlank
	private String password;


}