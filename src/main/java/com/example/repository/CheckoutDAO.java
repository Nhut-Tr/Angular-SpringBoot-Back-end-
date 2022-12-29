package com.example.repository;

import com.example.dto.request.BestSaleDTO;
import com.example.model.Cart;
import com.example.model.CheckoutCart;
import com.example.model.Orders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface CheckoutDAO extends JpaRepository<CheckoutCart, Long> {
  @Query(value = ("Select *  FROM Checkout_Cart checkCart WHERE checkCart.user_id=:userId"), nativeQuery = true)
  List<CheckoutCart> getByUserId(@Param("userId") Long userId);

  @Query(value = ("Select *  FROM Checkout_Cart checkCart WHERE checkCart.order_id=:orderId"), nativeQuery = true)
  List<CheckoutCart> getHistoryByOrderId(@Param("orderId") Long orderId);

  @Query(value = "Select new com.example.dto.request.BestSaleDTO(c.products.id,sum(c.quantity),c.products.name,c.products.price,c.products.img)  from CheckoutCart c join c.products  group by c.products.id,c.products.name,c.products.price,c.products.img  ")
  List<BestSaleDTO> getBestSale(Pageable pageable);

  @Query(value = "Select * FROM Orders", nativeQuery = true)
  List<Orders> getAllCheckout();


  @Query(value = "SELECT SUM(c.price)  FROM CheckoutCart c")
  Integer getSum();
}
