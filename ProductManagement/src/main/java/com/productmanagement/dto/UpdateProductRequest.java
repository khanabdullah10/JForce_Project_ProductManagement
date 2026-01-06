package com.productmanagement.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProductRequest {
    private String name;
    private String description;
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    private Long categoryId;
    private Boolean enabled;
    private Integer quantity;
}

