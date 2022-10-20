package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Products;

public interface ProductsDAO extends JpaRepository<Products,Long>{
	List<Products> findByName(String name); 
}
