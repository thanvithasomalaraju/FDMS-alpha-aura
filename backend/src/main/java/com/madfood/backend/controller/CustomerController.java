package com.madfood.backend.controller;

import com.madfood.backend.dto.*;
import com.madfood.backend.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/{customerId}/profile")
    public ResponseEntity<CustomerProfileDto> getProfile(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.getProfile(customerId));
    }

    @PutMapping("/{customerId}/profile")
    public ResponseEntity<CustomerProfileDto> updateProfile(@PathVariable Long customerId, @RequestBody CustomerProfileDto dto) {
        return ResponseEntity.ok(customerService.updateProfile(customerId, dto));
    }

    @PostMapping("/{customerId}/addresses")
    public ResponseEntity<AddressDto> addAddress(@PathVariable Long customerId, @RequestBody AddressDto dto) {
        return ResponseEntity.ok(customerService.addAddress(customerId, dto));
    }

    @DeleteMapping("/{customerId}/addresses/{addressId}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long customerId, @PathVariable Long addressId) {
        customerService.deleteAddress(customerId, addressId);
        return ResponseEntity.ok(Map.of("message", "Address deleted successfully"));
    }

    @GetMapping("/restaurants")
    public ResponseEntity<List<RestaurantDto>> getApprovedRestaurants() {
        return ResponseEntity.ok(customerService.getApprovedRestaurants());
    }

    @GetMapping("/restaurants/{restaurantId}/menu")
    public ResponseEntity<List<FoodItemDto>> getMenuItems(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(customerService.getMenuItems(restaurantId));
    }

    @GetMapping("/menu/search")
    public ResponseEntity<List<FoodItemDto>> searchFood(@RequestParam String query) {
        return ResponseEntity.ok(customerService.searchFood(query));
    }

    @GetMapping("/{customerId}/cart")
    public ResponseEntity<CartDto> getCart(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.getCart(customerId));
    }

    @PostMapping("/{customerId}/cart")
    public ResponseEntity<CartDto> addToCart(@PathVariable Long customerId, @RequestParam Long foodItemId, @RequestParam Integer quantity) {
        return ResponseEntity.ok(customerService.addToCart(customerId, foodItemId, quantity));
    }

    @PutMapping("/{customerId}/cart")
    public ResponseEntity<CartDto> updateCartQuantity(@PathVariable Long customerId, @RequestParam Long foodItemId, @RequestParam Integer quantity) {
        return ResponseEntity.ok(customerService.updateCartQuantity(customerId, foodItemId, quantity));
    }

    @DeleteMapping("/{customerId}/cart")
    public ResponseEntity<CartDto> removeFromCart(@PathVariable Long customerId, @RequestParam Long foodItemId) {
        return ResponseEntity.ok(customerService.removeFromCart(customerId, foodItemId));
    }

    @PostMapping("/{customerId}/orders")
    public ResponseEntity<OrderDto> placeOrder(@PathVariable Long customerId, @RequestParam String paymentMethod, @RequestParam Long addressId) {
        return ResponseEntity.ok(customerService.placeOrder(customerId, paymentMethod, addressId));
    }

    @GetMapping("/{customerId}/orders")
    public ResponseEntity<List<OrderDto>> getOrderHistory(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.getOrderHistory(customerId));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDto> getOrderDetails(@PathVariable Long orderId) {
        return ResponseEntity.ok(customerService.getOrderDetails(orderId));
    }

    @PostMapping("/{customerId}/restaurants/{restaurantId}/reviews")
    public ResponseEntity<ReviewDto> addReview(@PathVariable Long customerId, @PathVariable Long restaurantId, @RequestBody ReviewDto dto) {
        return ResponseEntity.ok(customerService.addReview(customerId, restaurantId, dto));
    }

    @GetMapping("/restaurants/{restaurantId}/reviews")
    public ResponseEntity<List<ReviewDto>> getReviews(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(customerService.getReviews(restaurantId));
    }

    @GetMapping("/notifications/{userId}")
    public ResponseEntity<List<NotificationDto>> getNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(customerService.getNotifications(userId));
    }
}
