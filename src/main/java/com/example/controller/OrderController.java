package com.example.controller;

import com.example.dto.response.ApiResponse;
import com.example.model.*;
import com.example.repository.CartDAO;
import com.example.repository.CheckoutDAO;
import com.example.repository.UserDAO;
import com.example.security.service.CartService;
import com.example.security.service.ProductService;
import com.example.security.service.ShoppingConfiguration;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins = "http://localhost:4200")
public class OrderController {

  @Autowired
  CheckoutDAO checkoutDAO;

  @Autowired
  UserDAO userDAO;
  @Autowired
  CartService cartService;


  @Autowired
  CartDAO cartDAO;

  @RequestMapping("/check-out")
  public ResponseEntity<?> checkout_order(@RequestBody HashMap<String, String> addCartRequest) {
    try {
      String keys[] = {"userId", "totalPrice", "deliveryAddress"};
      if (ShoppingConfiguration.validationWithHashMap(keys, addCartRequest)) {


      }
      Long userId = Long.parseLong(addCartRequest.get("userId"));
      Users user = userDAO.findUserById(userId).get(0);
      Double totalAmt = Double.parseDouble(addCartRequest.get("totalPrice"));
      if (cartService.checkTotalAmountAgainstCart(totalAmt, userId)) {
        List<Cart> cartItems = cartService.getCartByUserId(userId);
        List<CheckoutCart> tmp = new ArrayList<>();
        for (Cart addCart : cartItems) {
          Products product = addCart.getProducts();
          CheckoutCart cart = new CheckoutCart();
          double price;
          price = (addCart.getQuantity() * product.getPrice());
          cart.setPrice(price);
          cart.setUserId(userId);
          cart.setProducts(product);
          cart.setQuantity(addCart.getQuantity());
          cart.setDeliveryAddress(addCartRequest.get("deliveryAddress"));
          tmp.add(cart);
        }
        cartService.saveProductsForCheckout(tmp);
        return ResponseEntity.ok(new ApiResponse("Order successfully", ""));
      } else {
        throw new Exception("Total amount is mismatch");
      }
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), ""));
    }
  }

  @RequestMapping("get-orders-by-user-id")
  public ResponseEntity<?> getOrdersByUserId(@RequestBody HashMap<String, String> ordersRequest) {
    try {
      String keys[] = {"userId"};
      return ResponseEntity.ok(new ApiResponse("Get Order successfully", ""));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), ""));
    }

  }

  @GetMapping("/get-history-by-user-id")
  public ResponseEntity<?> getHistoryByUserId(@RequestParam Long userId) {
    try {
      List<CheckoutCart> obj = cartService.getHistoryByUserId((userId));
      return ResponseEntity.ok(obj);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), ""));
    }
  }

  @GetMapping("/get-best-sale")
  public List<CheckoutCart> getBestSale() {
    return checkoutDAO.getBestSale();
  }


}
