package com.example.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import com.example.model.Orders;
import com.example.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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
    product.setStatus(true);
    Products savedProduct = productDAO.save(product);
    URI productURI = URI.create("/products/" + savedProduct.getId());
    return ResponseEntity.created(productURI).body(savedProduct);
  }

  @GetMapping("/search-product-name")
  public Page<Products> findProductName(@RequestParam("name") String name, @RequestParam("page") int page, @RequestParam("size") int size) {
		Pageable pageable = PageRequest.of(page,size);
    return productDAO.findProductName(name, true,pageable);
  }
  @GetMapping("/search-product-name-admin")
  public Page<Products> findProductNameAdmin(@RequestParam("name") String name, @RequestParam("page") int page, @RequestParam("size") int size) {
    Pageable pageable = PageRequest.of(page,size);
    return productDAO.findProductNameAdmin(name,pageable);
  }

  @GetMapping("/product-list")
  public Page<Products> list(@RequestParam("page") int page, @RequestParam("size") int size) {
    Pageable pageable = PageRequest.of(page,size);
		return productDAO.findAllByStatus(true,pageable);
  }

  @GetMapping("/product-list-deactivated")
  public Page<Products> listDeactivated(@RequestParam("page") int page, @RequestParam("size") int size) {
    Pageable pageable = PageRequest.of(page,size);
    return productDAO.findAllByStatus(false,pageable);
  }

  @GetMapping("/admin/product-list")
  public Page<Products> listProduct(@RequestParam("page") int page, @RequestParam("size") int size) {
    Pageable pageable = PageRequest.of(page,size);
    return productDAO.findAll(pageable);
  }

  //get product by id rest api
  @GetMapping("/product/{id}")
  public ResponseEntity<Products> getProductById(@PathVariable Long id) {
    Optional<Products> product = productDAO.findById(id);
    if (product.isPresent()) {
      return new ResponseEntity<>(product.get(), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  // Update product
  @PutMapping("/product/{id}")
  public ResponseEntity<Products> updateProduct(@PathVariable Long id, @RequestBody Products product) {
    Optional<Products> productData = productDAO.findById(id);
    if (productData.isPresent()) {
      Products products = productData.get();
      products.setName(product.getName());
      products.setPrice(product.getPrice());
      products.setDescription(product.getDescription());
      products.setImg(product.getImg());
      products.setQuantity(product.getQuantity());
      products.setStatus(product.getStatus());
      return new ResponseEntity<>(productDAO.save(products), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }


  //Delete soft
  @PutMapping("/product-status/{id}")
  public ResponseEntity<Products> updateStatus(@PathVariable(name = "id") Long id) {
    Optional<Products> productsData = productDAO.findById(id);
    if (productsData.isPresent()) {
      Products pro = productsData.get();
      pro.setStatus(false);
      return new ResponseEntity<>(productDAO.save(pro), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }
  @GetMapping("/search-all-product")
  public Page<Products> findProductAll(@Param("name") String name,@Param("minPrice") Double minPrice,@Param("maxPrice") Double maxPrice,@Param("status") String status, @RequestParam("page") int page, @RequestParam("size") int size) {
    Pageable pageable = PageRequest.of(page,size);
    if(status.isEmpty()){
      return productDAO.findByNameIsContainingAndPriceGreaterThanEqualAndPriceLessThanEqual(name,minPrice,maxPrice,pageable);
    }
    return productDAO.findByNameIsContainingAndPriceGreaterThanEqualAndPriceLessThanEqualAndStatus(name,minPrice,maxPrice,Boolean.parseBoolean(status),pageable);
  }

}
