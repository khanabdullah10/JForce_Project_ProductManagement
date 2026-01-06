package com.productmanagement.dto;

import lombok.Data;

@Data
public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;

    // Explicit constructors to ensure compatibility
    public ApiResponse() {
        // Default constructor
    }

    public ApiResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}
