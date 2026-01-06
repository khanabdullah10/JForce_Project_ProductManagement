package com.productmanagement.controller;

import com.productmanagement.dto.ApiResponse;
import com.productmanagement.dto.CartItemRequest;
import com.productmanagement.dto.CartResponse;
import com.productmanagement.service.CartService;
import com.productmanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasRole('USER')")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getCart(Authentication authentication) {
        Long userId = userService.getUserByUsername(authentication.getName()).getId();
        CartResponse cart = cartService.getCart(userId);
        return ResponseEntity.ok(new ApiResponse(true, "Cart retrieved successfully", cart));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse> addItemToCart(@Valid @RequestBody CartItemRequest request,
                                                       Authentication authentication) {
        Long userId = userService.getUserByUsername(authentication.getName()).getId();
        CartResponse cart = cartService.addItemToCart(userId, request);
        return ResponseEntity.ok(new ApiResponse(true, "Item added to cart successfully", cart));
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse> updateCartItem(@PathVariable Long cartItemId,
                                                       @RequestParam Integer quantity,
                                                       Authentication authentication) {
        Long userId = userService.getUserByUsername(authentication.getName()).getId();
        CartResponse cart = cartService.updateCartItem(userId, cartItemId, quantity);
        return ResponseEntity.ok(new ApiResponse(true, "Cart item updated successfully", cart));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse> removeCartItem(@PathVariable Long cartItemId,
                                                       Authentication authentication) {
        Long userId = userService.getUserByUsername(authentication.getName()).getId();
        CartResponse cart = cartService.removeCartItem(userId, cartItemId);
        return ResponseEntity.ok(new ApiResponse(true, "Item removed from cart successfully", cart));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> clearCart(Authentication authentication) {
        Long userId = userService.getUserByUsername(authentication.getName()).getId();
        cartService.clearCart(userId);
        return ResponseEntity.ok(new ApiResponse(true, "Cart cleared successfully", null));
    }
}
