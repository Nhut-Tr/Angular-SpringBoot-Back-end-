package com.example.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import com.example.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.model.Products;
import com.example.repository.ProductsDAO;
import org.springframework.web.server.ResponseStatusException;

@RequestMapping("/controller")
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

	@GetMapping("/search-product-name")
	public List<Products> findProductName(@Param("name") String name){
		return productDAO.findProductName(name);
	}
	@GetMapping("/product-list")
	public List<Products> list() {
		return productDAO.findAll();
	}
	
	//get product by id rest api
	@GetMapping("/product/{id}")
	public ResponseEntity<Products> getProductById(@PathVariable Long id){
		Optional<Products> product = productDAO.findById(id);
		if(product.isPresent()){
			return new ResponseEntity<>(product.get(),HttpStatus.OK);
		}else{
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	// Update product
	@PutMapping("/product/{id}")
	public ResponseEntity<Products> updateProduct(@PathVariable Long id,@RequestBody Products product){
		Optional<Products> productData = productDAO.findById(id);
		if(productData.isPresent()){
			Products products = productData.get();
			products.setName(product.getName());
			products.setPrice(product.getPrice());
			products.setDescription(product.getDescription());
			products.setImg(product.getImg());
			return new ResponseEntity<>(productDAO.save(products),HttpStatus.OK);
		}else{
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	//delete product by id
	@DeleteMapping("/product/{id}")
	public ResponseEntity<HttpStatus> deleteProduct(@PathVariable Long id){
		try {
			productDAO.deleteById(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}catch(Exception ex){
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	//delete product
	@DeleteMapping("/product")
	public ResponseEntity<HttpStatus> deleteProduct(){
		try {
			productDAO.deleteAll();
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}catch(Exception ex){
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
