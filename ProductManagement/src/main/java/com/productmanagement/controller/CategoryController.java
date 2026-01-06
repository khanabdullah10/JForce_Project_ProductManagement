package com.productmanagement.controller;

import com.productmanagement.dto.ApiResponse;
import com.productmanagement.dto.CategoryRequest;
import com.productmanagement.dto.CategoryResponse;
import com.productmanagement.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(new ApiResponse(true, "Categories retrieved successfully", categories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCategoryById(@PathVariable Long id) {
        CategoryResponse category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Category retrieved successfully", category));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse category = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Category created successfully", category));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse> updateCategory(@PathVariable Long id,
                                                       @Valid @RequestBody CategoryRequest request) {
        CategoryResponse category = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(new ApiResponse(true, "Category updated successfully", category));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(new ApiResponse(true, "Category deleted successfully", null));
    }
}
