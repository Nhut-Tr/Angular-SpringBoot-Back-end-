package com.example.repository;

import com.example.model.Cart;
import com.example.model.CheckoutCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckoutDAO extends JpaRepository<CheckoutCart,Long> {
    @Query(value=("Select *  FROM Checkout_Cart checkCart WHERE checkCart.user_id=:userId"),nativeQuery = true)
    List<CheckoutCart> getByUserId(@Param("userId") Long userId);

    @Query(value=("Select *  FROM Checkout_Cart checkCart WHERE checkCart.order_id=:orderId"),nativeQuery = true)
    List<CheckoutCart> getHistoryByOrderId(@Param("orderId") Long orderId);

    @Query(value ="Select * FROM Checkout_Cart c Order By Quantity desc",nativeQuery = true)
    List<CheckoutCart> getBestSale();
}
