package com.productmanagement.controller;

import com.productmanagement.dto.AddressRequest;
import com.productmanagement.dto.AddressResponse;
import com.productmanagement.dto.ApiResponse;
import com.productmanagement.service.AddressService;
import com.productmanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@PreAuthorize("hasRole('USER')")
public class AddressController {

    private final AddressService addressService;
    private final UserService userService;

    public AddressController(AddressService addressService, UserService userService) {
        this.addressService = addressService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addAddress(@Valid @RequestBody AddressRequest request,
                                                  Authentication authentication) {
        Long userId = userService.getUserByUsername(authentication.getName()).getId();
        AddressResponse address = addressService.addAddress(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Address added successfully", address));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getUserAddresses(Authentication authentication) {
        Long userId = userService.getUserByUsername(authentication.getName()).getId();
        List<AddressResponse> addresses = addressService.getUserAddresses(userId);
        return ResponseEntity.ok(new ApiResponse(true, "Addresses retrieved successfully", addresses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getAddressById(@PathVariable Long id,
                                                       Authentication authentication) {
        Long userId = userService.getUserByUsername(authentication.getName()).getId();
        AddressResponse address = addressService.getAddressById(id, userId);
        return ResponseEntity.ok(new ApiResponse(true, "Address retrieved successfully", address));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateAddress(@PathVariable Long id,
                                                      @Valid @RequestBody AddressRequest request,
                                                      Authentication authentication) {
        Long userId = userService.getUserByUsername(authentication.getName()).getId();
        AddressResponse address = addressService.updateAddress(id, userId, request);
        return ResponseEntity.ok(new ApiResponse(true, "Address updated successfully", address));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteAddress(@PathVariable Long id,
                                                     Authentication authentication) {
        Long userId = userService.getUserByUsername(authentication.getName()).getId();
        addressService.deleteAddress(id, userId);
        return ResponseEntity.ok(new ApiResponse(true, "Address deleted successfully", null));
    }
}
