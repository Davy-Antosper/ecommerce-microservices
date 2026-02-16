package com.ecommerce.cart.service;

import com.ecommerce.cart.domain.entity.Cart;
import com.ecommerce.cart.domain.entity.CartItem;
import com.ecommerce.cart.domain.exception.CartNotFoundException;
import com.ecommerce.cart.domain.exception.InvalidCartOperationException;
import com.ecommerce.cart.domain.service.CartServiceImp;
import com.ecommerce.cart.dto.request.AddItemRequest;
import com.ecommerce.cart.dto.request.UpdateQuantityRequest;
import com.ecommerce.cart.dto.response.CartResponse;
import com.ecommerce.cart.mapper.MapperToResponse;
import com.ecommerce.cart.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private MapperToResponse response;
    @InjectMocks
    private CartServiceImp cartService;

    private Cart testCart;
    private String testCartId = "CART-TEST-123";
    private String testUserId = "user123";

    @BeforeEach
    void setUp() {
        // Configurer les propriétés
        ReflectionTestUtils.setField(cartService, "maxItems", 100);
        ReflectionTestUtils.setField(cartService, "timeBeforeCartToExpire", 7L);

        // Créer un panier de test
        testCart = Cart.builder()
                .cartId(testCartId)
                .userId(testUserId)
                .items(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
        testCart.calculateTotals();
    }

    @Test
    void createCart_ShouldReturnNewCart() {
        // Given
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // When
        CartResponse result = cartService.createCart(testUserId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(testUserId);
        assertThat(result.getItems()).isEmpty();
        assertThat(result.getTotalItems()).isZero();

        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void getCart_WhenCartExists_ShouldReturnCart() {
        // Given
        when(cartRepository.findById(testCartId)).thenReturn(Optional.of(testCart));

        // When
        CartResponse result = cartService.getCart(testCartId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCartId()).isEqualTo(testCartId);
        assertThat(result.getUserId()).isEqualTo(testUserId);

        verify(cartRepository, times(1)).findById(testCartId);
    }

    @Test
    void getCart_WhenCartNotExists_ShouldThrowException() {
        // Given
        when(cartRepository.findById(testCartId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> cartService.getCart(testCartId))
                .isInstanceOf(CartNotFoundException.class)
                .hasMessageContaining("Cart not found");

        verify(cartRepository, times(1)).findById(testCartId);
    }

    @Test
    void addItem_WhenCartExists_ShouldAddItemSuccessfully() {
        // Given
        AddItemRequest request = AddItemRequest.builder()
                .productId("PROD-001")
                .productName("iPhone 15 Pro")
                .quantity(2)
                .unitPrice(new BigDecimal("999.99"))
                .build();

        when(cartRepository.findById(testCartId)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // When
        CartResponse result = cartService.addItem(testCartId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getTotalItems()).isEqualTo(2);
        assertThat(result.getTotal()).isEqualByComparingTo(new BigDecimal("1999.98"));

        verify(cartRepository, times(1)).findById(testCartId);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void addItem_WhenExceedsMaxItems_ShouldThrowException() {
        // Given
        ReflectionTestUtils.setField(cartService, "maxItems", 2);

        AddItemRequest request = AddItemRequest.builder()
                .productId("PROD-001")
                .productName("iPhone 15 Pro")
                .quantity(5)
                .unitPrice(new BigDecimal("999.99"))
                .build();

        when(cartRepository.findById(testCartId)).thenReturn(Optional.of(testCart));

        // When & Then
        assertThatThrownBy(() -> cartService.addItem(testCartId, request))
                .isInstanceOf(InvalidCartOperationException.class)
                .hasMessageContaining("Cart limit");

        verify(cartRepository, times(1)).findById(testCartId);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void addItem_WhenSameProductExists_ShouldIncreaseQuantity() {
        // Given
        CartItem existingItem = CartItem.builder()
                .itemId("ITEM-001")
                .productId("PROD-001")
                .productName("iPhone 15 Pro")
                .quantity(2)
                .unitPrice(new BigDecimal("999.99"))
                .available(true)
                .build();

        testCart.getItems().add(existingItem);
        testCart.calculateTotals();

        AddItemRequest request = AddItemRequest.builder()
                .productId("PROD-001")
                .productName("iPhone 15 Pro")
                .quantity(3)
                .unitPrice(new BigDecimal("999.99"))
                .build();

        when(cartRepository.findById(testCartId)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // When
        CartResponse result = cartService.addItem(testCartId, request);

        // Then
        assertThat(result.getItems()).hasSize(1); // Toujours 1 item
        assertThat(result.getTotalItems()).isEqualTo(5); // 2 + 3 = 5
    }

    @Test
    void updateItemQuantity_WhenProductExists_ShouldUpdateSuccessfully() {
        // Given
        CartItem item = CartItem.builder()
                .itemId("ITEM-001")
                .productId("PROD-001")
                .productName("iPhone 15 Pro")
                .quantity(2)
                .unitPrice(new BigDecimal("999.99"))
                .available(true)
                .build();

        testCart.getItems().add(item);
        testCart.calculateTotals();

        UpdateQuantityRequest request = UpdateQuantityRequest.builder()
                .quantity(5)
                .build();

        when(cartRepository.findById(testCartId)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // When
        CartResponse result = cartService.updateItemQuantity(testCartId, "PROD-001", request);

        // Then
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(5);
        assertThat(result.getTotalItems()).isEqualTo(5);
    }

    @Test
    void updateItemQuantity_WhenProductNotExists_ShouldThrowException() {
        // Given
        UpdateQuantityRequest request = UpdateQuantityRequest.builder()
                .quantity(5)
                .build();

        when(cartRepository.findById(testCartId)).thenReturn(Optional.of(testCart));

        // When & Then
        assertThatThrownBy(() -> cartService.updateItemQuantity(testCartId, "PROD-999", request))
                .isInstanceOf(InvalidCartOperationException.class)
                .hasMessageContaining("Product not found in cart");
    }

    @Test
    void removeItem_WhenProductExists_ShouldRemoveSuccessfully() {
        // Given
        CartItem item = CartItem.builder()
                .itemId("ITEM-001")
                .productId("PROD-001")
                .productName("iPhone 15 Pro")
                .quantity(2)
                .unitPrice(new BigDecimal("999.99"))
                .available(true)
                .build();

        testCart.getItems().add(item);
        testCart.calculateTotals();

        when(cartRepository.findById(testCartId)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // When
        CartResponse result = cartService.removeItem(testCartId, "PROD-001");

        // Then
        assertThat(result.getItems()).isEmpty();
        assertThat(result.getTotalItems()).isZero();
    }

    @Test
    void clearCart_ShouldRemoveAllItems() {
        // Given
        testCart.getItems().add(CartItem.builder()
                .productId("PROD-001")
                .quantity(2)
                .unitPrice(new BigDecimal("999.99"))
                .build());
        testCart.getItems().add(CartItem.builder()
                .productId("PROD-002")
                .quantity(1)
                .unitPrice(new BigDecimal("499.99"))
                .build());

        when(cartRepository.findById(testCartId)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // When
        cartService.clearCart(testCartId);

        // Then
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void deleteCart_WhenCartExists_ShouldDeleteSuccessfully() {
        // Given
        when(cartRepository.existsById(testCartId)).thenReturn(true);
        doNothing().when(cartRepository).deletedById(testCartId);

        // When
        cartService.deleteCart(testCartId);

        // Then
        verify(cartRepository, times(1)).existsById(testCartId);
        verify(cartRepository, times(1)).deletedById(testCartId);
    }

    @Test
    void deleteCart_WhenCartNotExists_ShouldThrowException() {
        // Given
        when(cartRepository.existsById(testCartId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> cartService.deleteCart(testCartId))
                .isInstanceOf(CartNotFoundException.class);

        verify(cartRepository, never()).deletedById(any());
    }
}