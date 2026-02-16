package com.ecommerce.cart.domain.service;

import com.ecommerce.cart.client.CatalogClient;
import com.ecommerce.cart.domain.entity.Cart;
import com.ecommerce.cart.domain.entity.CartItem;
import com.ecommerce.cart.domain.exception.CartNotFoundException;
import com.ecommerce.cart.domain.exception.InvalidCartOperationException;
import com.ecommerce.cart.dto.external.ProductDTO;
import com.ecommerce.cart.dto.request.AddItemRequest;
import com.ecommerce.cart.dto.request.UpdateQuantityRequest;
import com.ecommerce.cart.dto.response.CartResponse;
import com.ecommerce.cart.mapper.MapperToResponse;
import com.ecommerce.cart.repository.CartRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImp implements CartService{
    private final CartRepository cartRepository;
    private final MapperToResponse response;
    private final CatalogClient catalogClient;

    @Value("${cart.max-items}")
    private int maxItems;

    @Value("${cart.ttl-days}")
    private long timeBeforeCartToExpire;
    @Override
    public CartResponse createCart(String userId) {
        log.info("Creating new Cart for user : {}",userId);
        Cart cart = Cart.builder()
                .cartId(Cart.generateCartId())
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(timeBeforeCartToExpire))
                .build();
        cart.calculateTotals();
        Cart savedCart = cartRepository.save(cart);
        log.info("Cart created..!!:{}",savedCart.getCreatedAt());

        return response.mapToResponse(savedCart);
    }

    @Override
    public CartResponse getCart(String cartId) {
        log.info("Retrieving cart: {}", cartId);

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found: " + cartId));
        return response.mapToResponse(cart);
    }

    @Override
    public CartResponse addItem(String cartId, AddItemRequest request) {
        log.info("Adding item to cart {}: productId={}, quantity={}",
                cartId, request.getProductId(), request.getQuantity());

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found: " + cartId));

        // ========== VALIDATION VIA CATALOG SERVICE ==========
        ProductDTO product = validateProduct(request.getProductId());

        boolean isAvailable = validateAvailability(request.getProductId(), request.getQuantity());
        if (!isAvailable) {
            throw new InvalidCartOperationException(
                    "Product " + request.getProductId() + " is not available in requested quantity");
        }

        if (cart.getTotalItems() + request.getQuantity() > maxItems) {
            throw new InvalidCartOperationException(
                    "Cannot add item. Cart limit of " + maxItems );
        }

        CartItem cartItem = CartItem.builder()
                .itemId(CartItem.generateItemId())
                .productId(product.getProductId())
                .productName(product.getName())
                .productImage(product.getImageUrl())
                .quantity(request.getQuantity())
                .unitPrice(product.getPrice())
                .available(true)
                .build();

        cart.addItem(cartItem);

        Cart savedCart = cartRepository.save(cart);

        log.info("Item added to cart....!! {}", cartId);

        return response.mapToResponse(savedCart);
    }

    @Override
    public CartResponse updateItemQuantity(String cartId, String productId, UpdateQuantityRequest request) {
        log.info("Updating item quantity in cart {}: productId={}, newQuantity={}",
                cartId, productId, request.getQuantity());

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found: " + cartId));

        // Validation via Catalog
        boolean isAvailable = validateAvailability(productId, request.getQuantity());
        if (!isAvailable) {
            throw new InvalidCartOperationException(
                    "Product not available in requested quantity");
        }

        boolean productExists = false;
        for (CartItem item : cart.getItems()) {
            if (item.getProductId().equals(productId)) {
                productExists = true;
                break;
            }
        }
        if (!productExists) {
            throw new InvalidCartOperationException(
                    "Product not found in cart: " + productId);
        }

        cart.updateItemQuantity(productId, request.getQuantity());

        Cart savedCart = cartRepository.save(cart);

        log.info("Item quantity updated...!!! in cart {}", cartId);

        return response.mapToResponse(savedCart);
    }

    @Override
    public CartResponse removeItem(String cartId, String productId) {
        log.info("Removing item from cart {}: productId={}", cartId, productId);

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found: " + cartId));

        cart.removeItem(productId);

        Cart savedCart = cartRepository.save(cart);

        log.info("Item removed successfully from cart {}", cartId);

        return response.mapToResponse(savedCart);
    }

    @Override
    public void clearCart(String cartId) {
        log.info("Clearing cart: {}", cartId);

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found: " + cartId));

        cart.clear();
        cartRepository.save(cart);

        log.info("Cart cleared successfully: {}", cartId);
    }

    @Override
    public void deleteCart(String cartId) {
        log.info("Deleting cart: {}", cartId);

        if (!cartRepository.existsById(cartId)) {
            throw new CartNotFoundException("Cart not found: " + cartId);
        }

        cartRepository.deletedById(cartId);

        log.info("Cart deleted successfully: {}", cartId);
    }




    // ========== HELPER METHODS - CATALOG VALIDATION ==========

    private ProductDTO validateProduct(String productId) {
        try {
            log.debug("Validating product via Catalog Service: {}", productId);
            ProductDTO product = catalogClient.getProduct(productId);
            log.debug("Product validated: {}", product.getName());
            return product;
        } catch (FeignException.NotFound e) {
            log.error("Product not found in catalog: {}", productId);
            throw new InvalidCartOperationException("Product not found: " + productId);
        } catch (FeignException e) {
            log.error("Error communicating with Catalog Service: {}", e.getMessage());
            throw new InvalidCartOperationException(
                    "Unable to validate product. Please try again later.");
        }
    }

    private boolean validateAvailability(String productId, int quantity) {
        try {
            log.debug("Checking availability: productId={}, quantity={}", productId, quantity);
            return catalogClient.checkAvailability(productId, quantity);
        } catch (FeignException e) {
            log.error("Error checking availability: {}", e.getMessage());
            // En cas d'erreur, on refuse l'ajout (fail-safe)
            return false;
        }
    }
}
