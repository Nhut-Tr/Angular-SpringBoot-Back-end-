package com.example.model;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(	name = "USERS", 
		uniqueConstraints = { 
			@UniqueConstraint(columnNames = "USERNAME"),
			@UniqueConstraint(columnNames = "EMAIL") 
		})
public class Users {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long Id;

	@NotBlank
	@Column(name="USERNAME")
	private String username;

	@NotBlank
	@Email
	@Column(name="EMAIL")
	private String email;
 
	@NotBlank
	@Column(name="PASSWORD")
	private String password;
	
	

	@ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name= "User_Role",
    	joinColumns = @JoinColumn(name = "user_Id"),
    	inverseJoinColumns = @JoinColumn(name = "role_Id"))
	private Set<Role> roles = new HashSet<>();



	public Users( @NotBlank String username, @NotBlank  @Email String email,
			@NotBlank String password ) {
		
		this.username = username;
		this.email = email;
		this.password = password;
		
	}
	
	
	
	
	
}
