package com.example.security.service.impl;

import com.example.model.Cart;
import com.example.model.CheckoutCart;
import com.example.model.Products;
import com.example.repository.CartDAO;
import com.example.repository.CheckoutDAO;
import com.example.security.service.CartService;
import com.example.security.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service


public class CartServiceImpl implements CartService {
    @Autowired
    CartDAO cartDAO;

    @Autowired
    CheckoutDAO checkoutDAO;
    @Autowired
    ProductService productService;

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);


    @Override
    public List<Cart> addCartByUserIdAndProductId(Long productId, Long userId, Double quantity, Double price) throws Exception {
        try {
            if(!cartDAO.getCartByProductIdAndUserId(userId, productId).isEmpty()){
                throw new Exception("Product is already exist.");
            }
            Cart obj = new Cart();
            obj.setQuantity(1.0);
            obj.setUser_id(userId);
            Products pro = productService.getProductsById(productId);
            obj.setProducts(pro);
            //TODO price has to check with qty
            obj.setPrice(price);
//            obj.setCreate_date(create_date);
            cartDAO.save(obj);
            return this.getCartByUserId(userId);
        }catch(Exception e) {
            e.printStackTrace();
            logger.error(""+e.getMessage());
            throw new Exception(e.getMessage());
        }

    }


    @Override
    public void updateQtyByCartId(Long cartId, Double quantity, Double price,Long userId) throws Exception {
        cartDAO.updateQuantityByCartId(cartId,price,quantity);

    }

    @Override
    public List<Cart> getCartByUserId(Long userId) {
        return cartDAO.getCartByUserId(userId);
    }

    @Override
    public List<CheckoutCart> getHistoryByUserId(Long userId){
        return  checkoutDAO.getByUserId(userId);
    }


    @Override
    public List<Cart> removeAllCartByUserId(Long userId) {
        cartDAO.deleteAllCartByUserId(userId);
        return null;
    }

     @Override
    public Boolean checkTotalAmountAgainstCart(Double totalAmount, Long userId) {
         Double totalAmount1 = cartDAO.getTotalAmountByUserId(userId);
         if(Double.compare(totalAmount1,totalAmount)==0) {
             return true;
         }
         System.out.print("Error from request "+totalAmount1 +" --db-- "+ totalAmount);
         return false;
    }

    @Override
    public List<CheckoutCart> getAllCheckoutByUserId(Long userId) {
        return checkoutDAO.getByUserId(userId);
    }

    @Override
    public List<CheckoutCart> saveProductsForCheckout(List<CheckoutCart> tmp) throws Exception {
        try {
            Long userId = tmp.get(0).getUserId();
            if(tmp.size() >0) {
                checkoutDAO.saveAll(tmp);
                this.removeAllCartByUserId(userId);
                return this.getAllCheckoutByUserId(userId);
            }
            else {
                throw  new Exception("Should not be empty");
            }
        }catch(Exception e) {
            throw new Exception("Error while checkout "+e.getMessage());
        }

    }


}
