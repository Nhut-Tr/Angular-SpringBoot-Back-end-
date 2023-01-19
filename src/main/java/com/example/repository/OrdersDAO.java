package com.example.repository;

import com.example.model.Cart;
import com.example.model.CheckoutCart;
import com.example.model.Orders;
import com.example.model.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Repository
public interface OrdersDAO extends JpaRepository<Orders, Long> {
  @Query(value = "select distinct top 6 * from Orders o order by o.created_at desc", nativeQuery = true)
  List<Orders> getDateForChart();

  Page<Orders> findAll(Pageable pageable);

  @Query(value = "Select o from Orders o where cast(o.createdAt as date ) between cast(?1 as date) and cast(?2 as date) ")
  Page<Orders> findByCreatedAt(Date start, Date finish, Pageable pageable);

  Page<Orders> findByStatus(Integer status, Pageable pageable);

  @Query(value = "select o from Orders o where cast(o.createdAt as date) between cast(?1 as date) and cast(?2 as date) and o.status=?3")
  Page<Orders> findByStatusAndDate(Date start, Date finish, Integer status, Pageable pageable);

  @Query(value="select sum(c.price * c.quantity) from CheckoutCart c join c.orders o where o.id = ?1 group by o.id")
  Double totalAllByOrder(Long orderId);
}