package com.example.security.service;

import com.example.model.Cart;

import com.example.model.CheckoutCart;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CartService {
    List<Cart> addCartByUserIdAndProductId(Long productId, Long userId, Double quantity, Double price) throws Exception;

    void updateQtyByCartId(Long cartId,Double quantity,Double price,Long userId) throws Exception;
    List<Cart> getCartByUserId(Long userId);
    List<CheckoutCart> getHistoryByUserId(Long userId);
    List<Cart> removeAllCartByUserId(Long userId);
    Boolean checkTotalAmountAgainstCart(Double totalAmount,Long userId);
    List<CheckoutCart> getAllCheckoutByUserId(Long userId);
    List<CheckoutCart> saveProductsForCheckout(List<CheckoutCart> tmp)  throws Exception;

}
