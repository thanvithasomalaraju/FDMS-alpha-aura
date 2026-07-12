package com.madfood.backend.service;

import com.madfood.backend.dto.*;
import java.util.List;
import java.util.Map;

public interface AdminService {
    Map<String, Object> getDashboardStats();
    
    // Approvals
    List<RestaurantDto> getPendingRestaurants();
    List<DeliveryPartnerDto> getPendingDeliveryPartners();
    
    RestaurantDto approveRestaurant(Long restaurantId);
    RestaurantDto rejectRestaurant(Long restaurantId, String reason);
    
    DeliveryPartnerDto approveDeliveryPartner(Long partnerId);
    DeliveryPartnerDto rejectDeliveryPartner(Long partnerId, String reason);
    
    // Management lists
    List<RestaurantDto> getAllRestaurants();
    List<DeliveryPartnerDto> getAllDeliveryPartners();
    List<CustomerProfileDto> getAllCustomers();
    List<FoodItemDto> getAllFoods();
    List<OrderDto> getAllOrders();
    List<PaymentDto> getAllPayments();
    List<ReviewDto> getAllReviews();
    
    // Logging / CMS Actions
    List<Map<String, String>> getSystemLogs();
    void addSystemLog(String action);
}
