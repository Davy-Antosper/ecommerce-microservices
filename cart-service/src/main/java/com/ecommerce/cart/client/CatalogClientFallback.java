package com.ecommerce.cart.client;

import com.ecommerce.cart.dto.external.ProductDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class CatalogClientFallback implements CatalogClient {

    @Override
    public ProductDTO getProduct(String productId) {
        log.warn("Fallback: Catalog Service unavailable for product: {}", productId);

        return ProductDTO.builder()
                .productId(productId)
                .name("Product temporarily unavailable")
                .description("Please try again later")
                .price(BigDecimal.ZERO)
                .available(false)
                .stockQuantity(0)
                .build();
    }

    @Override
    public Boolean checkAvailability(String productId, int quantity) {
        log.warn("Fallback: Catalog Service unavailable for availability check: {}", productId);
        return false;
    }
}