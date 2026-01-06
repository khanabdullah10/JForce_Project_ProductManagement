package com.productmanagement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckoutRequest {
    @NotNull(message = "Address ID is required")
    private Long addressId;
}

