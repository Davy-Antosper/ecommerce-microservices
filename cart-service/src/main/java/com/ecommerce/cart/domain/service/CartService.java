package com.ecommerce.cart.domain.service;

import com.ecommerce.cart.dto.request.AddItemRequest;
import com.ecommerce.cart.dto.request.UpdateQuantityRequest;
import com.ecommerce.cart.dto.response.CartResponse;

public interface CartService {

    CartResponse createCart(String userId);
    CartResponse getCart(String cartId);
    CartResponse addItem(String cartId, AddItemRequest itemRequest);
    CartResponse updateItemQuantity(String cartId, String productId, UpdateQuantityRequest updateQuantityRequest);
    CartResponse removeItem(String cartId,String productId);
    void clearCart(String cartId);
    void deleteCart(String cartId);

}
