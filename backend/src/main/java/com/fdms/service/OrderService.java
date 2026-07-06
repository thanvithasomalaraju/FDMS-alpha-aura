package com.fdms.service;

import com.fdms.dto.CreateOrderRequest;
import com.fdms.dto.OrderDto;
import com.fdms.dto.OrderItemDto;
import com.fdms.entity.*;
import com.fdms.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    private static final BigDecimal TAX_RATE = new BigDecimal("0.05"); // 5% tax
    private static final BigDecimal DELIVERY_FEE = new BigDecimal("50.00");

    @Transactional
    public OrderDto createOrder(Long userId, CreateOrderRequest request) {
        log.info("Creating order for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found with ID: " + request.getRestaurantId()));

        // Calculate order totals
        BigDecimal subtotal = BigDecimal.ZERO;
        Set<OrderItem> orderItems = new HashSet<>();

        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getOrderItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new IllegalArgumentException("Menu item not found with ID: " + itemRequest.getMenuItemId()));

            if (!menuItem.getIsAvailable()) {
                throw new IllegalArgumentException("Menu item is not available: " + menuItem.getName());
            }

            BigDecimal itemSubtotal = menuItem.getPrice().multiply(new BigDecimal(itemRequest.getQuantity()));
            subtotal = subtotal.add(itemSubtotal);

            OrderItem orderItem = OrderItem.builder()
                    .menuItem(menuItem)
                    .quantity(itemRequest.getQuantity())
                    .price(menuItem.getPrice())
                    .subtotal(itemSubtotal)
                    .specialInstructions(itemRequest.getSpecialInstructions())
                    .build();
            orderItems.add(orderItem);
        }

        // Calculate tax and total
        BigDecimal taxAmount = subtotal.multiply(TAX_RATE);
        BigDecimal totalAmount = subtotal.add(taxAmount).add(DELIVERY_FEE);

        // Create order
        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .user(user)
                .restaurant(restaurant)
                .deliveryAddress(request.getDeliveryAddress())
                .deliveryCity(request.getDeliveryCity())
                .deliveryPostalCode(request.getDeliveryPostalCode())
                .totalAmount(totalAmount)
                .deliveryFee(DELIVERY_FEE)
                .taxAmount(taxAmount)
                .specialInstructions(request.getSpecialInstructions())
                .status(Order.OrderStatus.PENDING)
                .build();

        Order savedOrder = orderRepository.save(order);

        // Link order items to order and save
        for (OrderItem item : orderItems) {
            item.setOrder(savedOrder);
            orderItemRepository.save(item);
        }

        log.info("Order created successfully with order number: {}", savedOrder.getOrderNumber());
        return mapToOrderDto(savedOrder);
    }

    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));
        return mapToOrderDto(order);
    }

    public OrderDto getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with order number: " + orderNumber));
        return mapToOrderDto(order);
    }

    public List<OrderDto> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToOrderDto)
                .collect(Collectors.toList());
    }

    public List<OrderDto> getRestaurantOrders(Long restaurantId) {
        return orderRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId).stream()
                .map(this::mapToOrderDto)
                .collect(Collectors.toList());
    }

    public List<OrderDto> getDeliveryPartnerOrders(Long deliveryPartnerId) {
        return orderRepository.findByDeliveryPartnerId(deliveryPartnerId).stream()
                .map(this::mapToOrderDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDto updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        log.info("Order status updated to {} for order ID: {}", status, orderId);
        return mapToOrderDto(updatedOrder);
    }

    @Transactional
    public OrderDto assignDeliveryPartner(Long orderId, Long deliveryPartnerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        User deliveryPartner = userRepository.findById(deliveryPartnerId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery partner not found with ID: " + deliveryPartnerId));

        order.setDeliveryPartner(deliveryPartner);
        Order updatedOrder = orderRepository.save(order);
        log.info("Delivery partner assigned to order ID: {}", orderId);
        return mapToOrderDto(updatedOrder);
    }

    @Transactional
    public OrderDto cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        if (order.getStatus() == Order.OrderStatus.OUT_FOR_DELIVERY || order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("Cannot cancel order in current status: " + order.getStatus());
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        Order cancelledOrder = orderRepository.save(order);
        log.info("Order cancelled: {}", orderId);
        return mapToOrderDto(cancelledOrder);
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }

    private OrderDto mapToOrderDto(Order order) {
        Set<OrderItemDto> itemDtos = order.getOrderItems().stream()
                .map(item -> OrderItemDto.builder()
                        .id(item.getId())
                        .menuItemId(item.getMenuItem().getId())
                        .menuItemName(item.getMenuItem().getName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .subtotal(item.getSubtotal())
                        .specialInstructions(item.getSpecialInstructions())
                        .build())
                .collect(Collectors.toSet());

        return OrderDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUser().getId())
                .restaurantId(order.getRestaurant().getId())
                .deliveryPartnerId(order.getDeliveryPartner() != null ? order.getDeliveryPartner().getId() : null)
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryCity(order.getDeliveryCity())
                .deliveryPostalCode(order.getDeliveryPostalCode())
                .totalAmount(order.getTotalAmount())
                .deliveryFee(order.getDeliveryFee())
                .taxAmount(order.getTaxAmount())
                .status(order.getStatus())
                .specialInstructions(order.getSpecialInstructions())
                .orderItems(itemDtos)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
