package com.example.security.service.impl;

import com.example.model.Cart;
import com.example.model.CheckoutCart;
import com.example.model.Products;
import com.example.repository.CartDAO;
import com.example.repository.CheckoutDAO;
import com.example.security.service.CartService;
import com.example.security.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service


public class CartServiceImpl implements CartService {
  @Autowired
  CartDAO cartDAO;

  @Autowired
  CheckoutDAO checkoutDAO;
  @Autowired
  ProductService productService;

  private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);


  @Override
  public List<Cart> addCartByUserIdAndProductId(Long productId, Long userId, Double quantity, Double price) throws Exception {
    try {
      Cart cartsList = cartDAO.getByUserIdAndProductsId(userId,productId);
      if(cartsList != null && cartsList.getProducts().getQuantity()<=cartsList.getQuantity() ){
        throw new RuntimeException("Sorry we only have " + Math.round(cartsList.getProducts().getQuantity()) +" items in stock!");
      }
      Products products = productService.getProductsById(productId);
      if(cartsList == null && products.getQuantity() < quantity ){
        throw new RuntimeException("Sorry we only have " + Math.round(products.getQuantity()) +" items in stock!");
      }
      List<Cart> cartList = cartDAO.getCartByProductIdAndUserId(userId, productId);
       if (!cartList.isEmpty()) {
         List<Cart> carts = cartList.stream().peek(e -> e.setQuantity(e.getQuantity() + 1)).toList();
        return cartDAO.saveAll(carts);
      }

        Cart obj = new Cart();
        obj.setQuantity(1.0);
        obj.setUserId(userId);
        Products pro = productService.getProductsById(productId);
        obj.setProducts(pro);
        //TODO price has to check with qty
        obj.setPrice(price);
//            obj.setCreate_date(create_date);

        cartDAO.save(obj);

      return this.getCartByUserId(userId);
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("" + e.getMessage());
      throw new Exception(e.getMessage());
    }

  }


  @Override
  public void updateQtyByCartId(Long cartId, Double quantity, Double price, Long userId) throws Exception {
   Cart cartList = cartDAO.findById(cartId).orElseThrow(()->new EntityNotFoundException("NOT FOUND CART!"));
    if(cartList.getProducts().getQuantity()<quantity){
      throw new RuntimeException("Sorry we only have " + Math.round(cartList.getProducts().getQuantity()) +" items in stock!");
    }
    cartDAO.updateQuantityByCartId(cartId, price, quantity);

  }

  @Override
  public List<Cart> getCartByUserId(Long userId) {
    return cartDAO.getCartByUserId(userId);
  }

  @Override
  public List<CheckoutCart> getHistoryByUserId(Long userId) {
    return checkoutDAO.getByUserId(userId);
  }

  @Override
  public List<CheckoutCart> getHistoryByOrderId(Long orderId) {
    return checkoutDAO.getHistoryByOrderId(orderId);
  }


  @Override
  public List<Cart> removeAllCartByUserId(Long userId) {
    cartDAO.deleteAllCartByUserId(userId);
    return null;
  }

  @Override
  public Boolean checkTotalAmountAgainstCart(Double totalAmount, Long userId) {
    Double totalAmount1 = cartDAO.getTotalAmountByUserId(userId);
    if (Double.compare(totalAmount1, totalAmount) == 0) {
      return true;
    }
    System.out.print("Error from request " + totalAmount1 + " --db-- " + totalAmount);
    return false;
  }

  @Override
  public List<CheckoutCart> getAllCheckoutByUserId(Long userId) {
    return checkoutDAO.getByUserId(userId);
  }

  @Override
  public List<CheckoutCart> saveProductsForCheckout(List<CheckoutCart> tmp) throws Exception {
    try {
      Long userId = tmp.get(0).getUserId();
      if (tmp.size() > 0) {
        checkoutDAO.saveAll(tmp);
        this.removeAllCartByUserId(userId);
        return this.getAllCheckoutByUserId(userId);
      } else {
        throw new Exception("Should not be empty");
      }
    } catch (Exception e) {
      throw new Exception("Error while checkout " + e.getMessage());
    }

  }


}
