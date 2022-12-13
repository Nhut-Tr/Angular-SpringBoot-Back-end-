package com.example.controller;

import com.example.dto.request.RemoveCart;
import com.example.dto.response.ApiResponse;
import com.example.dto.response.MessageResponse;
import com.example.model.Cart;
import com.example.model.Products;
import com.example.repository.CartDAO;
import com.example.security.service.CartService;
import com.example.security.service.ShoppingConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private CartDAO cartDAO;


    @GetMapping("/cart-list")
    public List<Cart> list() {
        return cartDAO.findAll();
    }

    @RequestMapping("/add-product")
    public ResponseEntity<?> addCartWithProduct(@RequestBody HashMap<String,String> addCartRequest){
        try {
            String keys[] = {"productId","userId","quantity","price"};
            if(ShoppingConfiguration.validationWithHashMap(keys, addCartRequest)) {

            }
            Long productId = Long.parseLong(addCartRequest.get("productId"));
            Long userId =  Long.parseLong(addCartRequest.get("userId"));
            Double quantity =  Double.parseDouble(addCartRequest.get("quantity"));
            Double price = Double.parseDouble(addCartRequest.get("price"));
            SimpleDateFormat formatter1 = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
            LocalDate date = LocalDate.now();
//            Date create_date = formatter1.parse(addCartRequest.get("create_date"));
            List<Cart> obj = cartService.addCartByUserIdAndProductId(productId,userId,quantity,price);
            return ResponseEntity.ok(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), ""));
        }
    }

    @RequestMapping("/update-quantity-from-cart")
    public ResponseEntity<?> updateQuantityFromCart(@RequestBody HashMap<String,String> addCartRequest){
        try {
            String keys[] = {"id","userId","quantity","price"};
            if(ShoppingConfiguration.validationWithHashMap(keys, addCartRequest)) {

            }
            Long id = Long.parseLong(addCartRequest.get("id"));
            Long userId =  Long.parseLong(addCartRequest.get("userId"));
            Double quantity =  Double.parseDouble(addCartRequest.get("quantity"));
            Double price = Double.parseDouble(addCartRequest.get("price"));
            cartService.updateQtyByCartId(id, quantity, price,userId);
            List<Cart> obj = cartService.getCartByUserId(userId);
            return ResponseEntity.ok(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), ""));
        }
    }


    @DeleteMapping("/remove-product-from-cart/{id}")
    public ResponseEntity<HttpStatus> deleteCart(@PathVariable Long id){
        try {
            cartDAO.deleteCartByIdAndUserId(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch(Exception ex){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-cart-by-user-id")

    public ResponseEntity<?> getCartsByUserId(@RequestParam Long userId) {
        try {
            List<Cart> obj = cartService.getCartByUserId((userId));
            return ResponseEntity.ok(obj);
        }catch(Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Anh sai roi em xin loi anh di !"));
        }
    }


}
