package com.productmanagement.controller;

import com.productmanagement.dto.ApiResponse;
import com.productmanagement.dto.ProductRequest;
import com.productmanagement.dto.ProductResponse;
import com.productmanagement.dto.UpdateProductRequest;
import com.productmanagement.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(new ApiResponse(true, "Products retrieved successfully", products));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse> getProductsByCategory(@PathVariable Long categoryId) {
        List<ProductResponse> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(new ApiResponse(true, "Products retrieved successfully", products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Product retrieved successfully", product));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Product created successfully", product));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable Long id,
                                                     @Valid @RequestBody UpdateProductRequest request) {
        ProductResponse product = productService.updateProduct(id, request);
        return ResponseEntity.ok(new ApiResponse(true, "Product updated successfully", product));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(new ApiResponse(true, "Product deleted successfully", null));
    }
}
