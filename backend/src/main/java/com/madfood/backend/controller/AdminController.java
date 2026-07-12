package com.madfood.backend.controller;

import com.madfood.backend.dto.*;
import com.madfood.backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/restaurants/pending")
    public ResponseEntity<List<RestaurantDto>> getPendingRestaurants() {
        return ResponseEntity.ok(adminService.getPendingRestaurants());
    }

    @GetMapping("/partners/pending")
    public ResponseEntity<List<DeliveryPartnerDto>> getPendingDeliveryPartners() {
        return ResponseEntity.ok(adminService.getPendingDeliveryPartners());
    }

    @PostMapping("/restaurants/{restaurantId}/approve")
    public ResponseEntity<RestaurantDto> approveRestaurant(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(adminService.approveRestaurant(restaurantId));
    }

    @PostMapping("/restaurants/{restaurantId}/reject")
    public ResponseEntity<RestaurantDto> rejectRestaurant(@PathVariable Long restaurantId, @RequestParam String reason) {
        return ResponseEntity.ok(adminService.rejectRestaurant(restaurantId, reason));
    }

    @PostMapping("/partners/{partnerId}/approve")
    public ResponseEntity<DeliveryPartnerDto> approveDeliveryPartner(@PathVariable Long partnerId) {
        return ResponseEntity.ok(adminService.approveDeliveryPartner(partnerId));
    }

    @PostMapping("/partners/{partnerId}/reject")
    public ResponseEntity<DeliveryPartnerDto> rejectDeliveryPartner(@PathVariable Long partnerId, @RequestParam String reason) {
        return ResponseEntity.ok(adminService.rejectDeliveryPartner(partnerId, reason));
    }

    @GetMapping("/restaurants")
    public ResponseEntity<List<RestaurantDto>> getAllRestaurants() {
        return ResponseEntity.ok(adminService.getAllRestaurants());
    }

    @GetMapping("/partners")
    public ResponseEntity<List<DeliveryPartnerDto>> getAllDeliveryPartners() {
        return ResponseEntity.ok(adminService.getAllDeliveryPartners());
    }

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerProfileDto>> getAllCustomers() {
        return ResponseEntity.ok(adminService.getAllCustomers());
    }

    @GetMapping("/foods")
    public ResponseEntity<List<FoodItemDto>> getAllFoods() {
        return ResponseEntity.ok(adminService.getAllFoods());
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        return ResponseEntity.ok(adminService.getAllOrders());
    }

    @GetMapping("/payments")
    public ResponseEntity<List<PaymentDto>> getAllPayments() {
        return ResponseEntity.ok(adminService.getAllPayments());
    }

    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewDto>> getAllReviews() {
        return ResponseEntity.ok(adminService.getAllReviews());
    }

    @GetMapping("/logs")
    public ResponseEntity<List<Map<String, String>>> getSystemLogs() {
        return ResponseEntity.ok(adminService.getSystemLogs());
    }
}
