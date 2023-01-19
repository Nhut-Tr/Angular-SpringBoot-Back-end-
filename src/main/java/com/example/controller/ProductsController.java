package com.example.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.*;

import javax.validation.Valid;

import com.example.dto.request.BestSaleDTO;
import com.example.model.Base;
import com.example.model.Orders;
import com.example.model.Users;
import com.example.repository.CheckoutDAO;
import com.example.repository.OrdersDAO;
import com.example.repository.UserDAO;
import com.example.security.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

  @Autowired
  private CheckoutDAO checkoutDAO;
  @Autowired
  private UserDAO userDAO;


  private ExportService exportService;

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


  @GetMapping("/export/excel")
  public ResponseEntity<InputStreamResource> exportBestSaleExcel() throws IOException {
    Pageable pageable = PageRequest.of(0,10);
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Optional<Users> users = userDAO.findByUsername(authentication.getName());
    List<BestSaleDTO> bestSaleDTOList = (List<BestSaleDTO>) checkoutDAO.getBestSale(pageable);
    ByteArrayInputStream bais = exportService.excelReport(bestSaleDTOList);
    HttpHeaders headers = new HttpHeaders();
    Date createdAt = new Date();
    headers.add("Content-Disposition","inline; filename=bestSale.xlsx");
    return ResponseEntity.ok().headers(headers).body(new InputStreamResource(bais));
  }
}
