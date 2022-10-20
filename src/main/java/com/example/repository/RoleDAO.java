package com.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.ERole;
import com.example.model.Role;

@Repository
public interface RoleDAO extends JpaRepository<Role, Integer>{
	Optional<Role> findByName(ERole name);
}
