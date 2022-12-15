package com.example.repository;

import java.util.List;
import java.util.Optional;

import com.example.model.Cart;
import com.example.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Products;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface ProductsDAO extends JpaRepository<Products,Long>{
	List<Products> findByName(String name);
	Optional<Products> findById(Long id);

	List<Products> findAllByStatus(Boolean status);
	@Query(value="Select * from ProductNG p where UPPER(p.name) like %:name% and p.status=:status",nativeQuery = true)
	List<Products> findProductName(@Param("name") String name,@Param("status") Boolean status);



}
