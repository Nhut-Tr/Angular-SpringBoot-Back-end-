package com.example.dto.cart;

import javax.validation.constraints.NotNull;

public class AddToCartDTO {
    private Integer id;
    private @NotNull Integer product_id;
    private @NotNull Integer quantity;

    public AddToCartDTO(){
    }

    public Integer getId() {
        return id;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
