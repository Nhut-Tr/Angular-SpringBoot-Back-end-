package com.example.dto.request;

import javax.validation.constraints.NotBlank;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class LoginRequest {
	@NotBlank
	private String userName;

	@NotBlank
	private String password;



}
