package com.ecommerce.cart.client;

import com.ecommerce.cart.dto.external.ProductDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "catalog-service",fallback = CatalogClientFallback.class)
public interface CatalogClient {

    @GetMapping("/api/v1/products/{productId}")
    @CircuitBreaker(name = "catalogService", fallbackMethod = "getProductFallback")
    @Retry(name = "catalogService")
    ProductDTO getProduct(@PathVariable("productId") String productId);

    @GetMapping("/api/v1/products/{productId}/availability")
    @CircuitBreaker(name = "catalogService")
    @Retry(name = "catalogService")
    Boolean checkAvailability(@PathVariable("productId") String productId, @RequestParam("quantity") int quantity
    );
}