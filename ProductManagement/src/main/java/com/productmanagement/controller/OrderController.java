package com.productmanagement.controller;

import com.productmanagement.dto.ApiResponse;
import com.productmanagement.dto.CheckoutRequest;
import com.productmanagement.dto.OrderResponse;
import com.productmanagement.service.OrderService;
import com.productmanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> placeOrder(@Valid @RequestBody CheckoutRequest request,
                                                   Authentication authentication) {
        Long userId = userService.getUserByUsername(authentication.getName()).getId();
        OrderResponse order = orderService.placeOrder(userId, request.getAddressId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Order placed successfully", order));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> getUserOrders(Authentication authentication) {
        Long userId = userService.getUserByUsername(authentication.getName()).getId();
        List<OrderResponse> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(new ApiResponse(true, "Orders retrieved successfully", orders));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> getOrderById(@PathVariable Long id,
                                                     Authentication authentication) {
        Long userId = userService.getUserByUsername(authentication.getName()).getId();
        OrderResponse order = orderService.getOrderById(id, userId);
        return ResponseEntity.ok(new ApiResponse(true, "Order retrieved successfully", order));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(new ApiResponse(true, "All orders retrieved successfully", orders));
    }
}
