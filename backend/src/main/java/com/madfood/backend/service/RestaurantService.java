package com.madfood.backend.service;

import com.madfood.backend.dto.*;
import java.util.List;
import java.util.Map;

public interface RestaurantService {
    RestaurantDto getProfile(Long restaurantId);
    RestaurantDto updateProfile(Long restaurantId, RestaurantDto profileDto);
    RestaurantDto submitApplication(Long restaurantId, RestaurantDto appDto);
    
    // Categories CRUD
    FoodCategoryDto addCategory(Long restaurantId, FoodCategoryDto categoryDto);
    FoodCategoryDto updateCategory(Long restaurantId, Long categoryId, FoodCategoryDto categoryDto);
    void deleteCategory(Long restaurantId, Long categoryId);
    List<FoodCategoryDto> getCategories(Long restaurantId);
    
    // Menu Items CRUD
    FoodItemDto addFoodItem(Long restaurantId, FoodItemDto itemDto);
    FoodItemDto updateFoodItem(Long restaurantId, Long foodItemId, FoodItemDto itemDto);
    void deleteFoodItem(Long restaurantId, Long foodItemId);
    List<FoodItemDto> getFoodItems(Long restaurantId);
    
    // Orders
    List<OrderDto> getOrders(Long restaurantId);
    OrderDto updateOrderStatus(Long restaurantId, Long orderId, String status);
    
    // Dashboard Stats
    Map<String, Object> getDashboardStats(Long restaurantId);
}
