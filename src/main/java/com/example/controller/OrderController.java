package com.example.controller;

import com.example.dto.response.ApiResponse;
import com.example.model.*;
import com.example.repository.CartDAO;
import com.example.repository.CheckoutDAO;
import com.example.repository.OrdersDAO;
import com.example.repository.UserDAO;
import com.example.security.service.CartService;
import com.example.security.service.ProductService;
import com.example.security.service.ShoppingConfiguration;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.*;

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

  @Autowired
  OrdersDAO ordersDAO;

  @Transactional
  @RequestMapping("/check-out")
  public ResponseEntity<?> checkout_order(@RequestBody HashMap<String, String> addCartRequest) {
    try {
      String keys[] = {"userId", "totalPrice", "deliveryAddress","phoneNumber","description"};
      if (ShoppingConfiguration.validationWithHashMap(keys, addCartRequest)) {


      }
      Long userId = Long.parseLong(addCartRequest.get("userId"));
      Double totalAmt = Double.parseDouble(addCartRequest.get("totalPrice"));
      if (cartService.checkTotalAmountAgainstCart(totalAmt, userId)) {
        List<Cart> cartItems = cartService.getCartByUserId(userId);
        List<CheckoutCart> tmp = new ArrayList<>();
        Orders or = new Orders();
        or.setStatus(1);
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
          cart.setPhoneNumber(addCartRequest.get("phoneNumber"));
          cart.setDescription(addCartRequest.get("description"));
          cart.setOrders(or);
          tmp.add(cart);
          checkoutDAO.save(cart);
        }
          cartDAO.deleteAllCartByUserId(userId);
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
  @GetMapping("/get-history-by-order-id")
  public ResponseEntity<?> getHistoryByOrderId(@RequestParam Long orderId) {
    try {
      List<CheckoutCart> obj = cartService.getHistoryByOrderId((orderId));
      return ResponseEntity.ok(obj);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), ""));
    }
  }

  @GetMapping("/get-best-sale")
  public List<CheckoutCart> getBestSale() {
    return checkoutDAO.getBestSale();
  }


  @GetMapping("/list-checkout")
  public List<Orders> getAllCheckout(){
    return ordersDAO.findAll();
  }
  @GetMapping("/list-order/{id}")
  public ResponseEntity<Orders> getOrderById(@PathVariable Long id){
    Optional<Orders> order = ordersDAO.findById(id);
    if(order.isPresent()){
      return new ResponseEntity<>(order.get(), HttpStatus.OK);
    }else{
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @PutMapping("/update-order/{id}")
  public ResponseEntity<Orders> updateOrder(@PathVariable(name = "id") Long id, @RequestBody Orders orders) {
    Optional<Orders> ordersData = ordersDAO.findById(id);
    if (ordersData.isPresent()) {
      Orders order = ordersData.get();
      order.setStatus(orders.getStatus());
      return new ResponseEntity<>(ordersDAO.save(order), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

}
