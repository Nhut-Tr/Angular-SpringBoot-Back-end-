package com.example.repository;

import com.example.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;


@Repository
public interface CartDAO extends JpaRepository<Cart, Long> {

  Cart getByUserIdAndProductsId(Long userId, Long productId);
  @Query("Select sum(addCart.price * addCart.quantity) FROM Cart addCart WHERE addCart.userId=:userId")
  Double getTotalAmountByUserId(@Param("userId") Long userId);

  @Transactional
  @Query(value = ("Select *  FROM Cart addCart WHERE addCart.user_id=:userId"), nativeQuery = true)
  List<Cart> getCartByUserId(@Param("userId") Long userId);

  @Query("Select addCart  FROM Cart addCart ")
  List<Cart> getCartByuserIdtest();


  @Query(value = "Select * FROM Cart addCart WHERE addCart.product_id= :productId and addCart.user_id=:userId", nativeQuery = true)
  List<Cart> getCartByProductIdAndUserId(@Param("userId") Long userId, @Param("productId") Long productId);

  @Modifying
  @Transactional
  @Query("DELETE  FROM Cart  WHERE id =:cartId  ")
  void deleteCartByIdAndUserId(@Param("cartId") Long cartId);

  @Modifying
  @Transactional
  @Query(value=("DELETE addCart FROM Cart addCart WHERE addCart.user_id=:userId"),nativeQuery = true)
  void deleteAllCartByUserId(@Param("userId") Long userId);


  @Modifying
  @Transactional
  @Query("update Cart addCart set addCart.quantity=:quantity,addCart.price=:price WHERE addCart.id=:cartId")
  void updateQuantityByCartId(@Param("cartId") Long cartId, @Param("price") double price, @Param("quantity") Double quantity);
}
