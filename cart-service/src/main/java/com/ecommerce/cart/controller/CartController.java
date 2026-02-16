package com.ecommerce.cart.controller;

import com.ecommerce.cart.domain.service.CartService;
import com.ecommerce.cart.dto.request.AddItemRequest;
import com.ecommerce.cart.dto.request.UpdateQuantityRequest;
import com.ecommerce.cart.dto.response.CartResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartResponse> createCart(@RequestParam(required = false) String userId) {

        log.info("REST request to create cart for user: {}", userId);
        CartResponse cart = cartService.createCart(userId);

        return new ResponseEntity<>(cart, HttpStatus.CREATED);
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartResponse> addItem(
            @PathVariable String cartId,
            @Valid @RequestBody AddItemRequest request) {

        log.info("REST request to add item to cart {}: {}", cartId, request);
        CartResponse cart = cartService.addItem(cartId, request);

        return ResponseEntity.ok(cart);
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable String cartId) {

        log.info("REST request to get cart: {}", cartId);
        CartResponse cart = cartService.getCart(cartId);

        return ResponseEntity.ok(cart);
    }


    @PutMapping("/{cartId}/items/{productId}")
    public ResponseEntity<CartResponse> updateItemQuantity(@PathVariable String cartId, @PathVariable String productId,
            @Valid @RequestBody UpdateQuantityRequest request) {

        log.info("REST request to update quantity in cart {}: productId={}, quantity={}",
                cartId, productId, request.getQuantity());

        CartResponse cart = cartService.updateItemQuantity(cartId, productId, request);

        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<CartResponse> removeItem(
            @PathVariable String cartId,
            @PathVariable String productId) {

        log.info("REST request to remove item from cart {}: productId={}", cartId, productId);
        CartResponse cart = cartService.removeItem(cartId, productId);

        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/{cartId}/items")
    public ResponseEntity<Void> clearCart(@PathVariable String cartId) {

        log.info("REST request to clear cart: {}", cartId);
        cartService.clearCart(cartId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> deleteCart(@PathVariable String cartId) {

        log.info("REST request to delete cart: {}", cartId);
        cartService.deleteCart(cartId);

        return ResponseEntity.noContent().build();
    }
}
