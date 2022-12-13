package com.example.security.service;

import com.example.model.Orders;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public interface OrdersService {

  List<Orders> CheckoutToOrder(Long userId, Double totalAmt, HashMap<String, String> addCartRequest);
}
