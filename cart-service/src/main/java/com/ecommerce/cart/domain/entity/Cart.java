package com.ecommerce.cart.domain.entity;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cart implements Serializable {
    private String cartId;
    private String userId;

    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    private BigDecimal totalPrice;
    private BigDecimal subtotal;
    private BigDecimal total;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expiresAt;

    public void addItem(CartItem newItem) {
        CartItem existingItem = items.stream()
                .filter(item -> item.getProductId().equals(newItem.getProductId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + newItem.getQuantity();
            existingItem.setQuantity(newQuantity);
        } else {
            this.items.add(newItem);
        }
        this.updatedAt = LocalDateTime.now();
        calculateTotals();
    }

    public void removeItem(String productId) {
        this.items.removeIf(item -> item.getProductId().equals(productId));
        this.updatedAt = LocalDateTime.now();
        calculateTotals();
    }

    public void updateItemQuantity(String productId, int quantity) {
        CartItem updatedItem =  items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElse(null);
        if (updatedItem != null) {
            updatedItem.setQuantity(quantity);
            this.updatedAt = LocalDateTime.now();
            calculateTotals();
        }
    }
    public void clear() {
        this.items.clear();
        this.updatedAt = LocalDateTime.now();
        calculateTotals();
    }

    public void calculateTotals() {
        BigDecimal cumul = BigDecimal.ZERO;

        if (items != null) {
            for (CartItem item : items) {
                BigDecimal montantLigne = item.getLineTotal();
                cumul = cumul.add(montantLigne);
            }
        }
        this.subtotal = cumul;
        this.totalPrice = this.subtotal;
    }

    public int getTotalItems() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
    public static String generateCartId() {
        return "CART-" + UUID.randomUUID().toString();
    }

}