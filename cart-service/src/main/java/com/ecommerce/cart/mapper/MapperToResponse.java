package com.ecommerce.cart.mapper;

import com.ecommerce.cart.domain.entity.Cart;
import com.ecommerce.cart.domain.entity.CartItem;
import com.ecommerce.cart.dto.response.CartItemResponse;
import com.ecommerce.cart.dto.response.CartResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
@Component
public class MapperToResponse {
    public CartResponse mapToResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::mapItemToResponse)
                .collect(Collectors.toList());

        return CartResponse.builder()
                .cartId(cart.getCartId())
                .userId(cart.getUserId())
                .items(itemResponses)
                .totalItems(cart.getTotalItems())
                .subtotal(cart.getSubtotal())
                .total(cart.getTotal())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }
    public CartItemResponse mapItemToResponse(CartItem item) {
        return CartItemResponse.builder()
                .itemId(item.getItemId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .productImage(item.getProductImage())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .lineTotal(item.getLineTotal())
                .available(item.isAvailable())
                .build();
    }
}
