package com.productmanagement.service;

import com.productmanagement.dto.CartItemRequest;
import com.productmanagement.dto.CartItemResponse;
import com.productmanagement.dto.CartResponse;
import com.productmanagement.entity.Cart;
import com.productmanagement.entity.CartItem;
import com.productmanagement.entity.Product;
import com.productmanagement.exception.InsufficientInventoryException;
import com.productmanagement.exception.InvalidOperationException;
import com.productmanagement.exception.ResourceNotFoundException;
import com.productmanagement.entity.User;
import com.productmanagement.repository.CartItemRepository;
import com.productmanagement.repository.CartRepository;
import com.productmanagement.repository.ProductRepository;
import com.productmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
                       ProductRepository productRepository, UserRepository userRepository,
                       ProductService productService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.productService = productService;
    }

    @Transactional
    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    @Transactional
    public CartResponse addItemToCart(Long userId, CartItemRequest request) {
        Cart cart = getOrCreateCart(userId);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        if (!product.isEnabled()) {
            throw new InvalidOperationException("Product is disabled");
        }

        // Check inventory
        Integer availableQuantity = productService.getProductInventory(request.getProductId());
        if (availableQuantity < request.getQuantity()) {
            throw new InsufficientInventoryException(
                    "Insufficient inventory. Available: " + availableQuantity + ", Requested: " + request.getQuantity()
            );
        }

        // Check if item already exists in cart
        CartItem existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId())
                .orElse(null);

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (availableQuantity < newQuantity) {
                throw new InsufficientInventoryException(
                        "Insufficient inventory. Available: " + availableQuantity + ", Requested: " + newQuantity
                );
            }
            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItemRepository.save(cartItem);
        }

        return getCart(userId);
    }

    @Transactional
    public CartResponse updateCartItem(Long userId, Long cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new InvalidOperationException("Cart item does not belong to user");
        }

        if (quantity <= 0) {
            throw new InvalidOperationException("Quantity must be greater than 0");
        }

        // Check inventory
        Integer availableQuantity = productService.getProductInventory(cartItem.getProduct().getId());
        if (availableQuantity < quantity) {
            throw new InsufficientInventoryException(
                    "Insufficient inventory. Available: " + availableQuantity + ", Requested: " + quantity
            );
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        return getCart(userId);
    }

    @Transactional
    public CartResponse removeCartItem(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new InvalidOperationException("Cart item does not belong to user");
        }

        cartItemRepository.delete(cartItem);
        return getCart(userId);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteByCartId(cart.getId());
    }

    public CartResponse getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

        List<CartItemResponse> items = cartItems.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        BigDecimal totalAmount = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());
        response.setItems(items);
        response.setTotalAmount(totalAmount);

        return response;
    }

    @Transactional
    public void validateCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        if (cart.getId() == null) {
            throw new InvalidOperationException("Cart is empty");
        }
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

        if (cartItems.isEmpty()) {
            throw new InvalidOperationException("Cart is empty");
        }

        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            if (!product.isEnabled()) {
                throw new InvalidOperationException("Product '" + product.getName() + "' is disabled");
            }

            Integer availableQuantity = productService.getProductInventory(product.getId());
            if (availableQuantity < item.getQuantity()) {
                throw new InsufficientInventoryException(
                        "Insufficient inventory for product '" + product.getName() +
                                "'. Available: " + availableQuantity + ", Required: " + item.getQuantity()
                );
            }
        }
    }

    private CartItemResponse convertToResponse(CartItem cartItem) {
        CartItemResponse response = new CartItemResponse();
        response.setId(cartItem.getId());
        response.setProductId(cartItem.getProduct().getId());
        response.setProductName(cartItem.getProduct().getName());
        response.setPrice(cartItem.getProduct().getPrice());
        response.setQuantity(cartItem.getQuantity());
        response.setSubtotal(cartItem.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        return response;
    }
}

