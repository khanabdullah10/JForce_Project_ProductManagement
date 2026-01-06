package com.productmanagement.service;

import com.productmanagement.dto.RegisterRequest;
import com.productmanagement.dto.UserResponse;
import com.productmanagement.entity.Cart;
import com.productmanagement.entity.Role;
import com.productmanagement.entity.User;
import com.productmanagement.exception.DuplicateResourceException;
import com.productmanagement.exception.ResourceNotFoundException;
import com.productmanagement.repository.CartRepository;
import com.productmanagement.repository.RoleRepository;
import com.productmanagement.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, 
                       CartRepository cartRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(role -> "ROLE_" + role.getName().name())
                        .collect(Collectors.toList())
                        .toArray(new String[0]))
                .build();
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEnabled(true);

        Role userRole = roleRepository.findByName(Role.RoleType.USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role USER not found"));
        user.setRoles(new HashSet<>(Set.of(userRole)));

        user = userRepository.save(user);

        // Create cart for new user
        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);

        return convertToResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToResponse(user);
    }

    @Transactional
    public UserResponse updateUserRole(Long userId, Role.RoleType roleType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleType));

        user.getRoles().clear();
        user.getRoles().add(role);
        user = userRepository.save(user);

        return convertToResponse(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    public UserResponse getUserResponseByUsername(String username) {
        User user = getUserByUsername(username);
        return convertToResponse(user);
    }

    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEnabled(user.isEnabled());
        response.setRoles(user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet()));
        return response;
    }
}
