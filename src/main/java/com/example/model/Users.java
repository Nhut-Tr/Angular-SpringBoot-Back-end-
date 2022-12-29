package com.example.model;


import java.io.Serializable;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(	name = "USERS", 
		uniqueConstraints = { 
			@UniqueConstraint(columnNames = "USERNAME"),
			@UniqueConstraint(columnNames = "EMAIL") 
		})

public class Users {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;

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

	@Column(name="Full_name")
	private String fullName;

	@Column(name="Verification_code",length = 64)
	private String verificationCode;

	@Column(name="RESET_PASSWORD_TOKEN")
	private String resetPasswordToken;

	@Column(name = "enabled")
	private Boolean enabled;

	@Column(name = "Avatar")
	private String avatar;


	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name= "User_Role",
    	joinColumns = @JoinColumn(name = "User_Id"),
    	inverseJoinColumns = @JoinColumn(name = "Role_Id"))
	private Set<Role> roles = new HashSet<>();


	@OneToMany(mappedBy = "userId")
	List<CheckoutCart> checkoutCarts;



	public Users( @NotBlank String username, @NotBlank  @Email String email,@NotBlank String fullName,
			@NotBlank String password ) {

		this.username = username;
		this.email = email;
		this.password = password;

	}





	
	
	
	
}
