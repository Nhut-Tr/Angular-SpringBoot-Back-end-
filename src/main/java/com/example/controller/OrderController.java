package com.example.controller;

import com.example.dto.request.BestSaleDTO;
import com.example.dto.request.FindDateDTO;

import com.example.dto.response.ApiResponse;
import com.example.model.*;
import com.example.repository.*;
import com.example.security.service.CartService;
import com.example.security.service.ProductService;
import com.example.security.service.ShoppingConfiguration;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
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
  @Autowired
  ProductsDAO productsDAO;

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
          Products pro = productsDAO.findById(addCart.getProducts().getId()).orElseThrow(()->new EntityNotFoundException("PRODUCT NOT FOUND "));
          Products product = addCart.getProducts();
          CheckoutCart cart = new CheckoutCart();
          double price;
          price = (addCart.getQuantity() * product.getPrice());
          cart.setPrice(price);
          cart.setUserId(userId);
          cart.setProducts(product);
          Double productQuantity;
          if(product.getQuantity() < addCart.getQuantity()){
            throw new RuntimeException("Sorry "+product.getName()+" only have " + Math.round(product.getQuantity()) +" items in stock!");
          }
          productQuantity = pro.getQuantity() - addCart.getQuantity();
          cart.setQuantity(addCart.getQuantity());
          cart.setDeliveryAddress(addCartRequest.get("deliveryAddress"));
          cart.setPhoneNumber(addCartRequest.get("phoneNumber"));
          cart.setDescription(addCartRequest.get("description"));
          cart.setOrders(or);
          product.setQuantity(productQuantity);
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
  public List<BestSaleDTO> getBestSale() {
    Pageable pageable = PageRequest.of(0,6);
    return checkoutDAO.getBestSale(pageable);
  }


  @GetMapping("/list-checkout")
  public Page<Orders> getAllCheckout(@RequestParam("page") int page, @RequestParam("size") int size){
    Pageable pageable = PageRequest.of(page,size);
    return ordersDAO.findAll(pageable);
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

  @GetMapping("/get-date")
  public List<Orders> getDate(){
      return ordersDAO.getDateForChart();
  }

  @GetMapping("/get-total-sales")
  public Integer getTotalSales(){
    return checkoutDAO.getSum();
  }

  @PostMapping("/find-by-date")
  public Page<Orders> findDate(@RequestBody FindDateDTO findDateDTO, @RequestParam("page") int page, @RequestParam("size") int size){
    Pageable pageable = PageRequest.of(page,size);
    return ordersDAO.findByCreatedAt(findDateDTO.getStart(),findDateDTO.getFinish(),pageable);
  }

  @PostMapping("/find-by-status")
  public Page<Orders> findByStatus(@RequestBody Integer status, @RequestParam("page") int page, @RequestParam("size") int size){
    Pageable pageable = PageRequest.of(page,size);
    return ordersDAO.findByStatus(status,pageable);
  }

  @PostMapping("/find-by-status-and-date")
  public Page<Orders> findByStatusAndDate(@RequestBody FindDateDTO findDateDTO, @RequestParam("page") int page, @RequestParam("size") int size){
    Pageable pageable = PageRequest.of(page,size);
    return ordersDAO.findByStatusAndDate(findDateDTO.getStart(),findDateDTO.getFinish(),findDateDTO.getStatus(),pageable);
  }


}
