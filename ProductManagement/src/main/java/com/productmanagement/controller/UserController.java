package com.productmanagement.controller;

import com.productmanagement.dto.ApiResponse;
import com.productmanagement.dto.UserResponse;
import com.productmanagement.entity.Role;
import com.productmanagement.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse(true, "Users retrieved successfully", users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(new ApiResponse(true, "User retrieved successfully", user));
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<ApiResponse> updateUserRole(@PathVariable Long id,
                                                      @RequestBody Map<String, String> request) {
        String roleName = request.get("role");
        if (roleName == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Role is required", null));
        }

        Role.RoleType roleType;
        try {
            roleType = Role.RoleType.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Invalid role: " + roleName, null));
        }

        UserResponse user = userService.updateUserRole(id, roleType);
        return ResponseEntity.ok(new ApiResponse(true, "User role updated successfully", user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse(true, "User deleted successfully", null));
    }
}
