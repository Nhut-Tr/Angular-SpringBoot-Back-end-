package com.example.security.service;

import com.example.model.Products;
import com.example.repository.ProductsDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
  @Autowired
  ProductsDAO productsDAO;

  public List<Products> getAllProducts() {
    return productsDAO.findAll();
  }
  public Products getProductsById(Long productId) throws Exception {
    return productsDAO.findById(productId).orElseThrow(() -> new Exception("Product is not found"));
  }
}
