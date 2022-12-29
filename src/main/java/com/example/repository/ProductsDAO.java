package com.example.repository;

import java.util.List;
import java.util.Optional;

import com.example.model.Cart;
import com.example.model.Users;
import org.kolobok.annotation.FindWithOptionalParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	Page<Products> findAllByStatus(Boolean status,Pageable pageable);
	Page<Products> findAll(Pageable pageable);

	@Query(value="Select p from Products p where UPPER(p.name) like %:name% and p.status=:status")
	Page<Products> findProductName(@Param("name") String name, @Param("status") Boolean status, Pageable pageable);

	@Query(value="Select p from Products p where UPPER(p.name) like %:name% ")
	Page<Products> findProductNameAdmin(@Param("name") String name, Pageable pageable);

	@FindWithOptionalParams
	Page<Products> findByNameIsContainingAndPriceGreaterThanEqualAndPriceLessThanEqualAndStatus(String name ,Double minPrice,Double maxPrice,Boolean status,Pageable pageable);

	@FindWithOptionalParams
	Page<Products> findByNameIsContainingAndPriceGreaterThanEqualAndPriceLessThanEqual(String name ,Double minPrice,Double maxPrice,Pageable pageable);

}
