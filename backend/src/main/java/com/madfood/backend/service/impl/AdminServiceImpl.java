package com.madfood.backend.service.impl;

import com.madfood.backend.dto.*;
import com.madfood.backend.entity.*;
import com.madfood.backend.exception.ResourceNotFoundException;
import com.madfood.backend.mapper.*;
import com.madfood.backend.repository.*;
import com.madfood.backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private DeliveryPartnerRepository deliveryPartnerRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private FoodCategoryRepository categoryRepository;

    private final List<Map<String, String>> systemLogs = new ArrayList<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm a");

    public AdminServiceImpl() {
        // Seed initial log messages
        addLog("Admin", "System initialized and ready");
    }

    private void addLog(String user, String action) {
        Map<String, String> log = new HashMap<>();
        log.put("time", LocalDateTime.now().format(formatter));
        log.put("user", user);
        log.put("action", action);
        systemLogs.add(0, log);
        if (systemLogs.size() > 50) {
            systemLogs.remove(systemLogs.size() - 1);
        }
    }

    @Override
    public Map<String, Object> getDashboardStats() {
        List<Order> orders = orderRepository.findAll();
        List<Restaurant> restaurants = restaurantRepository.findAll();
        List<DeliveryPartner> partners = deliveryPartnerRepository.findAll();
        List<Customer> customers = customerRepository.findAll();

        double totalEarnings = orders.stream()
                .filter(o -> o.getStatus().equalsIgnoreCase("completed"))
                .mapToDouble(Order::getTotalAmount)
                .sum();

        long approvedRestaurants = restaurants.stream()
                .filter(r -> r.getStatus().equalsIgnoreCase("APPROVED"))
                .count();

        long activePartners = partners.stream()
                .filter(p -> p.getStatus().equalsIgnoreCase("APPROVED"))
                .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("earnings", totalEarnings);
        stats.put("restaurantsCount", approvedRestaurants);
        stats.put("deliveryPartnersCount", activePartners);
        stats.put("ordersCount", orders.size());
        stats.put("customersCount", customers.size());
        
        stats.put("recentOrders", orders.stream()
                .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
                .limit(5)
                .map(OrderMapper::toDto)
                .collect(Collectors.toList()));

        stats.put("recentReviews", reviewRepository.findAll().stream()
                .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
                .limit(5)
                .map(ReviewMapper::toDto)
                .collect(Collectors.toList()));

        stats.put("logs", systemLogs.stream().limit(10).collect(Collectors.toList()));

        return stats;
    }

    @Override
    public List<RestaurantDto> getPendingRestaurants() {
        return restaurantRepository.findByStatus("PENDING").stream()
                .map(RestaurantMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeliveryPartnerDto> getPendingDeliveryPartners() {
        return deliveryPartnerRepository.findByStatus("PENDING").stream()
                .map(DeliveryPartnerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RestaurantDto approveRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        restaurant.setStatus("APPROVED");
        restaurant.setRejectionReason(null);
        restaurantRepository.save(restaurant);

        // Add default categories if approved restaurant has none
        if (categoryRepository.findByRestaurantId(restaurantId).isEmpty()) {
            categoryRepository.save(FoodCategory.builder().name("South Indian").restaurant(restaurant).status("active").build());
            categoryRepository.save(FoodCategory.builder().name("Pizza").restaurant(restaurant).status("active").build());
            categoryRepository.save(FoodCategory.builder().name("Biryani").restaurant(restaurant).status("active").build());
        }

        notificationRepository.save(Notification.builder()
                .user(restaurant.getUser())
                .title("Application Approved")
                .message("Congratulations! Your restaurant application for " + restaurant.getName() + " has been approved.")
                .build());

        addLog("Admin", "Approved restaurant application: " + restaurant.getName());
        return RestaurantMapper.toDto(restaurant);
    }

    @Override
    @Transactional
    public RestaurantDto rejectRestaurant(Long restaurantId, String reason) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        restaurant.setStatus("REJECTED");
        restaurant.setRejectionReason(reason);
        restaurantRepository.save(restaurant);

        notificationRepository.save(Notification.builder()
                .user(restaurant.getUser())
                .title("Application Rejected")
                .message("Your restaurant application was rejected. Reason: " + reason)
                .build());

        addLog("Admin", "Rejected restaurant application: " + restaurant.getName() + " (Reason: " + reason + ")");
        return RestaurantMapper.toDto(restaurant);
    }

    @Override
    @Transactional
    public DeliveryPartnerDto approveDeliveryPartner(Long partnerId) {
        DeliveryPartner partner = deliveryPartnerRepository.findById(partnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery partner not found"));
        partner.setStatus("APPROVED");
        partner.setRejectionReason(null);
        deliveryPartnerRepository.save(partner);

        notificationRepository.save(Notification.builder()
                .user(partner.getUser())
                .title("Application Approved")
                .message("Congratulations! Your delivery partner application has been approved.")
                .build());

        addLog("Admin", "Approved delivery partner application: " + partner.getFullName());
        return DeliveryPartnerMapper.toDto(partner);
    }

    @Override
    @Transactional
    public DeliveryPartnerDto rejectDeliveryPartner(Long partnerId, String reason) {
        DeliveryPartner partner = deliveryPartnerRepository.findById(partnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery partner not found"));
        partner.setStatus("REJECTED");
        partner.setRejectionReason(reason);
        deliveryPartnerRepository.save(partner);

        notificationRepository.save(Notification.builder()
                .user(partner.getUser())
                .title("Application Rejected")
                .message("Your delivery partner application was rejected. Reason: " + reason)
                .build());

        addLog("Admin", "Rejected delivery partner application: " + partner.getFullName() + " (Reason: " + reason + ")");
        return DeliveryPartnerMapper.toDto(partner);
    }

    @Override
    public List<RestaurantDto> getAllRestaurants() {
        return restaurantRepository.findAll().stream()
                .map(RestaurantMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeliveryPartnerDto> getAllDeliveryPartners() {
        return deliveryPartnerRepository.findAll().stream()
                .map(DeliveryPartnerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerProfileDto> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(CustomerMapper::toCustomerProfileDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FoodItemDto> getAllFoods() {
        return foodItemRepository.findAll().stream()
                .map(FoodMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDto> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(p -> PaymentDto.builder()
                        .txn(p.getTransactionId())
                        .order("ORD-" + (p.getOrder().getId() + 48200))
                        .orderId(p.getOrder().getId())
                        .method(p.getPaymentMethod())
                        .amount(p.getAmount())
                        .status(p.getPaymentStatus())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDto> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(ReviewMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, String>> getSystemLogs() {
        return systemLogs;
    }

    @Override
    public void addSystemLog(String action) {
        addLog("System", action);
    }
}
