package com.madfood.backend.controller;

import com.madfood.backend.dto.*;
import com.madfood.backend.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurant")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping("/{restaurantId}/profile")
    public ResponseEntity<RestaurantDto> getProfile(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(restaurantService.getProfile(restaurantId));
    }

    @PutMapping("/{restaurantId}/profile")
    public ResponseEntity<RestaurantDto> updateProfile(@PathVariable Long restaurantId, @RequestBody RestaurantDto dto) {
        return ResponseEntity.ok(restaurantService.updateProfile(restaurantId, dto));
    }

    @PostMapping("/{restaurantId}/apply")
    public ResponseEntity<RestaurantDto> submitApplication(@PathVariable Long restaurantId, @RequestBody RestaurantDto dto) {
        return ResponseEntity.ok(restaurantService.submitApplication(restaurantId, dto));
    }

    // Categories
    @PostMapping("/{restaurantId}/categories")
    public ResponseEntity<FoodCategoryDto> addCategory(@PathVariable Long restaurantId, @RequestBody FoodCategoryDto dto) {
        return ResponseEntity.ok(restaurantService.addCategory(restaurantId, dto));
    }

    @PutMapping("/{restaurantId}/categories/{categoryId}")
    public ResponseEntity<FoodCategoryDto> updateCategory(@PathVariable Long restaurantId, @PathVariable Long categoryId, @RequestBody FoodCategoryDto dto) {
        return ResponseEntity.ok(restaurantService.updateCategory(restaurantId, categoryId, dto));
    }

    @DeleteMapping("/{restaurantId}/categories/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long restaurantId, @PathVariable Long categoryId) {
        restaurantService.deleteCategory(restaurantId, categoryId);
        return ResponseEntity.ok(Map.of("message", "Category deleted successfully"));
    }

    @GetMapping("/{restaurantId}/categories")
    public ResponseEntity<List<FoodCategoryDto>> getCategories(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(restaurantService.getCategories(restaurantId));
    }

    // Menu Items
    @PostMapping("/{restaurantId}/food-items")
    public ResponseEntity<FoodItemDto> addFoodItem(@PathVariable Long restaurantId, @RequestBody FoodItemDto dto) {
        return ResponseEntity.ok(restaurantService.addFoodItem(restaurantId, dto));
    }

    @PutMapping("/{restaurantId}/food-items/{foodItemId}")
    public ResponseEntity<FoodItemDto> updateFoodItem(@PathVariable Long restaurantId, @PathVariable Long foodItemId, @RequestBody FoodItemDto dto) {
        return ResponseEntity.ok(restaurantService.updateFoodItem(restaurantId, foodItemId, dto));
    }

    @DeleteMapping("/{restaurantId}/food-items/{foodItemId}")
    public ResponseEntity<?> deleteFoodItem(@PathVariable Long restaurantId, @PathVariable Long foodItemId) {
        restaurantService.deleteFoodItem(restaurantId, foodItemId);
        return ResponseEntity.ok(Map.of("message", "Food item deleted successfully"));
    }

    @GetMapping("/{restaurantId}/food-items")
    public ResponseEntity<List<FoodItemDto>> getFoodItems(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(restaurantService.getFoodItems(restaurantId));
    }

    // Orders
    @GetMapping("/{restaurantId}/orders")
    public ResponseEntity<List<OrderDto>> getOrders(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(restaurantService.getOrders(restaurantId));
    }

    @PutMapping("/{restaurantId}/orders/{orderId}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(@PathVariable Long restaurantId, @PathVariable Long orderId, @RequestParam String status) {
        return ResponseEntity.ok(restaurantService.updateOrderStatus(restaurantId, orderId, status));
    }

    // Dashboard Stats
    @GetMapping("/{restaurantId}/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(restaurantService.getDashboardStats(restaurantId));
    }
}
