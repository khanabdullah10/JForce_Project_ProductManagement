package com.productmanagement.dto;

import com.productmanagement.entity.Order;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private Long userId;
    private String username;
    private AddressResponse address;
    private BigDecimal totalAmount;
    private Order.OrderStatus status;
    private LocalDateTime orderDate;
    private List<OrderItemResponse> items;
}

