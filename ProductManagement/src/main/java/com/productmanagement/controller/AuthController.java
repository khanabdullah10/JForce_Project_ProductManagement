package com.productmanagement.controller;

import com.productmanagement.dto.ApiResponse;
import com.productmanagement.dto.RegisterRequest;
import com.productmanagement.dto.UserResponse;
import com.productmanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse user = userService.register(request);
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("message", "User registered successfully");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Registration successful", data));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getCurrentUser(Authentication authentication) {
        UserResponse user = userService.getUserResponseByUsername(authentication.getName());
        return ResponseEntity.ok(new ApiResponse(true, "User retrieved successfully", user));
    }
}
