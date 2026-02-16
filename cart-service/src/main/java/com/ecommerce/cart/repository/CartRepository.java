package com.ecommerce.cart.repository;

import com.ecommerce.cart.domain.entity.Cart;

import java.util.Optional;

public interface CartRepository {
    Cart save(Cart cart);
    Optional<Cart> findById(String cartId);
    void deletedById(String cartId);
    boolean existsById(String cartId);
}
