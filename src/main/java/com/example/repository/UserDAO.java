package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.model.Users;
@Repository
public interface UserDAO extends JpaRepository<Users, Long>{
	Optional<Users> findByUsername(String username);
	Boolean existsByUsername(String userName);
	Boolean existsByEmail(String email);
	@Query("SELECT u FROM Users u WHERE u.verificationCode = ?1")
	Users findByVerificationCode(String code);
	@Query(value="SELECT * FROM USERS u WHERE u.Id =:userId",nativeQuery = true)
	List<Users> findUserById(@Param("userId") Long userId);

	@Query(value="Select u from Users u where UPPER(u.username) like %:userName% ")
	List<Users> findUserName(@Param("userName") String userName);

	@Query("SELECT u FROM Users u WHERE u.email = ?1")
	public Users findByEmail(String email);

	@Query("SELECT u FROM Users u WHERE u.resetPasswordToken = ?1")
	public Users findByResetPasswordToken(String token);


}
