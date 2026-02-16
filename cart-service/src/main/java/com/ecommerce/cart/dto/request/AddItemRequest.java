package com.ecommerce.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddItemRequest {
    @NotBlank(message = "Product Id Is required")
    private String productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1)
    private Integer quantity;

    private String productName;
    private BigDecimal unitPrice;
    private String productImage;
}