package com.madfood.backend.service;

import com.madfood.backend.dto.*;
import java.util.List;

public interface CustomerService {
    CustomerProfileDto getProfile(Long customerId);
    CustomerProfileDto updateProfile(Long customerId, CustomerProfileDto profileDto);
    AddressDto addAddress(Long customerId, AddressDto addressDto);
    void deleteAddress(Long customerId, Long addressId);
    
    List<RestaurantDto> getApprovedRestaurants();
    List<FoodItemDto> getMenuItems(Long restaurantId);
    List<FoodItemDto> searchFood(String query);
    
    CartDto getCart(Long customerId);
    CartDto addToCart(Long customerId, Long foodItemId, Integer quantity);
    CartDto updateCartQuantity(Long customerId, Long foodItemId, Integer quantity);
    CartDto removeFromCart(Long customerId, Long foodItemId);
    
    OrderDto placeOrder(Long customerId, String paymentMethod, Long addressId);
    List<OrderDto> getOrderHistory(Long customerId);
    OrderDto getOrderDetails(Long orderId);
    
    ReviewDto addReview(Long customerId, Long restaurantId, ReviewDto reviewDto);
    List<ReviewDto> getReviews(Long restaurantId);
    
    List<NotificationDto> getNotifications(Long userId);
}
