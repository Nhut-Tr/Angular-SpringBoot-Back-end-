package com.example.repository;

import java.util.List;
import java.util.Optional;

import com.example.model.Orders;
import org.kolobok.annotation.FindWithOptionalParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.model.Users;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserDAO extends JpaRepository<Users, Long>{
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	Optional<Users> findByUsername(String username);
	Boolean existsByUsername(String userName);
	Boolean existsByEmail(String email);
	@Query("SELECT u FROM Users u WHERE u.verificationCode = ?1")
	Users findByVerificationCode(String code);
	@Query(value="SELECT * FROM USERS u WHERE u.Id =:userId",nativeQuery = true)
	List<Users> findUserById(@Param("userId") Long userId);

	@Query(value="Select u from Users u where UPPER(u.username) like %:userName% ")
	Page<Users> findUserName(@Param("userName") String userName, Pageable pageable);


	@Query(value = "select u from Users u join u.roles r where r.id = ?1")
	Page<Users> findByRolesId(@Param("id") Integer id, Pageable pageable);

	@Query(value="Select u from Users u where UPPER(u.email) like %:email% ")
	Page<Users> findUserByEmail(@Param("email") String email, Pageable pageable);

	@FindWithOptionalParams
	Page<Users> findByUsernameContainingAndEmailContainingAndRolesIdAndEnabled(String userName ,String email,Integer roleId,Boolean enabled,Pageable pageable);
	@FindWithOptionalParams
	Page<Users> findByUsernameContainingAndEmailContainingAndEnabled(String userName ,String email,Boolean enabled,Pageable pageable);
	@FindWithOptionalParams
	Page<Users> findByUsernameContainingAndEmailContainingAndRolesId(String userName ,String email,Integer roleId,Pageable pageable);

	@FindWithOptionalParams
	Page<Users> findByUsernameContainingAndEmailContaining(String userName ,String email,Pageable pageable);

	//	@Query(value="Select u from Users u join u.roles r where (:userName is null or UPPER(u.username) like %:userName%) and (:email is null or UPPER(u.email) like %:email%) and (:roleName is null or r.name = :roleName) and (:enabled is null or u.enabled=:enabled)")
//	Page<Users> findUserAll(@Param("userName") String userName,@Param("email") String email,@Param("roleName") String roleName,@Param("enabled") Boolean enabled, Pageable pageable);
	Page<Users> findByEnabled(Boolean enabled, Pageable pageable);
	@Query("SELECT u FROM Users u WHERE u.email = ?1")
	public Users findByEmail(String email);

	@Query("SELECT u FROM Users u WHERE u.resetPasswordToken = ?1")
	public Users findByResetPasswordToken(String token);


	Page<Users> findAll(Pageable pageable);
	List<Users> findAllByEnabled(Boolean enabled);

}
