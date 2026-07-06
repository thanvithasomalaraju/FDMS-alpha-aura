package com.fdms.controller;

import com.fdms.dto.CreateOrderRequest;
import com.fdms.dto.OrderDto;
import com.fdms.entity.Order;
import com.fdms.service.OrderService;
import com.fdms.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("Creating order for restaurant ID: {}", request.getRestaurantId());
        Long userId = SecurityUtils.getCurrentUserId();
        OrderDto createdOrder = orderService.createOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        log.info("Getting order by ID: {}", id);
        OrderDto order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<OrderDto> getOrderByOrderNumber(@PathVariable String orderNumber) {
        log.info("Getting order by order number: {}", orderNumber);
        OrderDto order = orderService.getOrderByOrderNumber(orderNumber);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<OrderDto>> getUserOrders() {
        log.info("Getting current user's orders");
        Long userId = SecurityUtils.getCurrentUserId();
        List<OrderDto> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/restaurant/{restaurantId}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<List<OrderDto>> getRestaurantOrders(@PathVariable Long restaurantId) {
        log.info("Getting orders for restaurant ID: {}", restaurantId);
        List<OrderDto> orders = orderService.getRestaurantOrders(restaurantId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/delivery-partner/my-orders")
    @PreAuthorize("hasRole('DELIVERY_PARTNER')")
    public ResponseEntity<List<OrderDto>> getDeliveryPartnerOrders() {
        log.info("Getting current delivery partner's orders");
        Long deliveryPartnerId = SecurityUtils.getCurrentUserId();
        List<OrderDto> orders = orderService.getDeliveryPartnerOrders(deliveryPartnerId);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('DELIVERY_PARTNER')")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam Order.OrderStatus status) {
        log.info("Updating order status for ID: {} to {}", id, status);
        OrderDto updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @PutMapping("/{id}/assign-delivery")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDto> assignDeliveryPartner(
            @PathVariable Long id,
            @RequestParam Long deliveryPartnerId) {
        log.info("Assigning delivery partner {} to order {}", deliveryPartnerId, id);
        OrderDto updatedOrder = orderService.assignDeliveryPartner(id, deliveryPartnerId);
        return ResponseEntity.ok(updatedOrder);
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderDto> cancelOrder(@PathVariable Long id) {
        log.info("Cancelling order with ID: {}", id);
        OrderDto cancelledOrder = orderService.cancelOrder(id);
        return ResponseEntity.ok(cancelledOrder);
    }
}
