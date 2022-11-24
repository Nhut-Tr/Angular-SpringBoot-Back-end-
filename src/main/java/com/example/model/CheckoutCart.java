package com.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="Checkout_Cart")
public class CheckoutCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "Quantity")
    Double quantity;


    @Column(name = "Price")
    Double price;

    @Column(name = "Order_date")
    Date orderDate;


    @Column(name = "delivery_address")
    String deliveryAddress;




//    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

    @ManyToOne
    @JoinColumn(name = "product_id")
    Products products;

//    @OneToOne(cascade = CascadeType.ALL)
//    private Cart cart_id;

    Long userId;




}
