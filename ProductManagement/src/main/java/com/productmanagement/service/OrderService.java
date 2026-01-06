package com.productmanagement.service;

import com.productmanagement.dto.CartItemResponse;
import com.productmanagement.dto.CartResponse;
import com.productmanagement.dto.OrderItemResponse;
import com.productmanagement.dto.OrderResponse;
import com.productmanagement.entity.*;
import com.productmanagement.exception.InsufficientInventoryException;
import com.productmanagement.exception.InvalidOperationException;
import com.productmanagement.exception.ResourceNotFoundException;
import com.productmanagement.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                        UserRepository userRepository, AddressRepository addressRepository,
                        ProductRepository productRepository, CartService cartService,
                        ProductService productService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
        this.productService = productService;
    }

    @Transactional
    public OrderResponse placeOrder(Long userId, Long addressId) {
        // Validate cart before processing
        cartService.validateCart(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        if (!address.getUser().getId().equals(userId)) {
            throw new InvalidOperationException("Address does not belong to user");
        }

        // Get cart items
        CartResponse cartResponse = cartService.getCart(userId);
        if (cartResponse.getItems().isEmpty()) {
            throw new InvalidOperationException("Cart is empty");
        }

        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setStatus(Order.OrderStatus.CONFIRMED);
        order.setTotalAmount(cartResponse.getTotalAmount());

        order = orderRepository.save(order);

        // Create order items and reduce inventory
        for (CartItemResponse cartItem : cartResponse.getItems()) {
            // Double-check inventory before reducing
            Integer availableQuantity = productService.getProductInventory(cartItem.getProductId());
            if (availableQuantity < cartItem.getQuantity()) {
                throw new InsufficientInventoryException(
                        "Insufficient inventory for product ID " + cartItem.getProductId() +
                                ". Available: " + availableQuantity + ", Required: " + cartItem.getQuantity()
                );
            }

            // Reduce inventory
            productService.reduceInventory(cartItem.getProductId(), cartItem.getQuantity());

            // Fetch product for order item
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + cartItem.getProductId()));

            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItemRepository.save(orderItem);
        }

        // Clear cart after successful order
        cartService.clearCart(userId);

        return convertToResponse(order);
    }

    public List<OrderResponse> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Order not found with id: " + orderId);
        }

        return convertToResponse(order);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUser().getId());
        response.setUsername(order.getUser().getUsername());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setOrderDate(order.getOrderDate());

        // Convert address
        com.productmanagement.dto.AddressResponse addressResponse = new com.productmanagement.dto.AddressResponse();
        addressResponse.setId(order.getAddress().getId());
        addressResponse.setStreet(order.getAddress().getStreet());
        addressResponse.setCity(order.getAddress().getCity());
        addressResponse.setState(order.getAddress().getState());
        addressResponse.setZipCode(order.getAddress().getZipCode());
        addressResponse.setCountry(order.getAddress().getCountry());
        response.setAddress(addressResponse);

        // Convert order items
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
        List<OrderItemResponse> itemResponses = orderItems.stream()
                .map(item -> {
                    OrderItemResponse itemResponse = new OrderItemResponse();
                    itemResponse.setId(item.getId());
                    itemResponse.setProductId(item.getProduct().getId());
                    itemResponse.setProductName(item.getProduct().getName());
                    itemResponse.setQuantity(item.getQuantity());
                    itemResponse.setPrice(item.getPrice());
                    itemResponse.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    return itemResponse;
                })
                .collect(Collectors.toList());
        response.setItems(itemResponses);

        return response;
    }
}

