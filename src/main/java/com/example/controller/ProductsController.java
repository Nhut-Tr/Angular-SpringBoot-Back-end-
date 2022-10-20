package com.example.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Products;
import com.example.repository.ProductsDAO;
@RequestMapping("/")
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ProductsController {
	@Autowired
	private ProductsDAO productDAO;

	@PostMapping("/product")
	public ResponseEntity<Products> create(@RequestBody @Valid Products product) {
		Products savedProduct = productDAO.save(product);
		URI productURI = URI.create("/products/" + savedProduct.getId());
		return ResponseEntity.created(productURI).body(savedProduct);
	}

	@GetMapping("/product")
	public List<Products> list() {
		return productDAO.findAll();
	}
	
	
}
