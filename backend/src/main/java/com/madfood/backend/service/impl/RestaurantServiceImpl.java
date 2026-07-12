package com.madfood.backend.service.impl;

import com.madfood.backend.dto.*;
import com.madfood.backend.entity.*;
import com.madfood.backend.exception.BadRequestException;
import com.madfood.backend.exception.ResourceNotFoundException;
import com.madfood.backend.mapper.FoodMapper;
import com.madfood.backend.mapper.OrderMapper;
import com.madfood.backend.mapper.RestaurantMapper;
import com.madfood.backend.repository.*;
import com.madfood.backend.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private FoodCategoryRepository categoryRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public RestaurantDto getProfile(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        return RestaurantMapper.toDto(restaurant);
    }

    @Override
    @Transactional
    public RestaurantDto updateProfile(Long restaurantId, RestaurantDto profileDto) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        
        restaurant.setName(profileDto.getNameString());
        restaurant.setPhone(profileDto.getPhone());
        restaurant.setAddress(profileDto.getAddress());
        if (profileDto.getDocFssai() != null) {
            restaurant.setDocFssai(profileDto.getDocFssai());
        }
        if (profileDto.getDocMenu() != null) {
            restaurant.setDocMenu(profileDto.getDocMenu());
        }
        if (profileDto.getOwnerName() != null) {
            restaurant.setOwnerName(profileDto.getOwnerName());
        }
        
        restaurantRepository.save(restaurant);
        return RestaurantMapper.toDto(restaurant);
    }

    @Override
    @Transactional
    public RestaurantDto submitApplication(Long restaurantId, RestaurantDto appDto) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        restaurant.setOwnerName(appDto.getOwnerName());
        restaurant.setAddress(appDto.getAddress());
        restaurant.setPhone(appDto.getPhone());
        restaurant.setLogoUrl(appDto.getLogoUrl());
        restaurant.setDocGst(appDto.getDocGst());
        restaurant.setDocFssai(appDto.getDocFssai());
        restaurant.setDocPan(appDto.getDocPan());
        restaurant.setDocMenu(appDto.getDocMenu());
        restaurant.setStatus("PENDING"); // Reset status to PENDING upon submission

        restaurantRepository.save(restaurant);
        return RestaurantMapper.toDto(restaurant);
    }

    @Override
    @Transactional
    public FoodCategoryDto addCategory(Long restaurantId, FoodCategoryDto categoryDto) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        FoodCategory category = FoodCategory.builder()
                .name(categoryDto.getName())
                .restaurant(restaurant)
                .status(categoryDto.getStatus() != null ? categoryDto.getStatus() : "active")
                .build();
        categoryRepository.save(category);
        return FoodMapper.toCategoryDto(category, 0);
    }

    @Override
    @Transactional
    public FoodCategoryDto updateCategory(Long restaurantId, Long categoryId, FoodCategoryDto categoryDto) {
        FoodCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!category.getRestaurant().getId().equals(restaurantId)) {
            throw new BadRequestException("Category does not belong to this restaurant!");
        }

        category.setName(categoryDto.getName());
        if (categoryDto.getStatus() != null) {
            category.setStatus(categoryDto.getStatus());
        }
        categoryRepository.save(category);

        int itemsCount = foodItemRepository.findByCategoryId(categoryId).size();
        return FoodMapper.toCategoryDto(category, itemsCount);
    }

    @Override
    @Transactional
    public void deleteCategory(Long restaurantId, Long categoryId) {
        FoodCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!category.getRestaurant().getId().equals(restaurantId)) {
            throw new BadRequestException("Category does not belong to this restaurant!");
        }

        // Delete associated food items first
        List<FoodItem> items = foodItemRepository.findByCategoryId(categoryId);
        foodItemRepository.deleteAll(items);

        categoryRepository.delete(category);
    }

    @Override
    public List<FoodCategoryDto> getCategories(Long restaurantId) {
        return categoryRepository.findByRestaurantId(restaurantId).stream()
                .map(cat -> {
                    int count = foodItemRepository.findByCategoryId(cat.getId()).size();
                    return FoodMapper.toCategoryDto(cat, count);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FoodItemDto addFoodItem(Long restaurantId, FoodItemDto itemDto) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        // Resolve or create category by name
        String categoryName = itemDto.getCategory();
        FoodCategory category = categoryRepository.findByRestaurantId(restaurantId).stream()
                .filter(c -> c.getName().equalsIgnoreCase(categoryName))
                .findFirst()
                .orElseGet(() -> categoryRepository.save(
                        FoodCategory.builder()
                                .name(categoryName)
                                .restaurant(restaurant)
                                .status("active")
                                .build()
                ));

        FoodItem item = FoodItem.builder()
                .name(itemDto.getNameString() != null ? itemDto.getNameString() : itemDto.getName().get("en"))
                .description(itemDto.getDescriptionString() != null ? itemDto.getDescriptionString() : itemDto.getDesc().get("en"))
                .price(itemDto.getPrice())
                .category(category)
                .restaurant(restaurant)
                .imageUrl(itemDto.getImageUrl())
                .isVeg(itemDto.getDiet() != null ? itemDto.getDiet().equalsIgnoreCase("veg") : true)
                .isAvailable(itemDto.getIsAvailable() != null ? itemDto.getIsAvailable() : true)
                .build();

        foodItemRepository.save(item);
        return FoodMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public FoodItemDto updateFoodItem(Long restaurantId, Long foodItemId, FoodItemDto itemDto) {
        FoodItem item = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found"));

        if (!item.getRestaurant().getId().equals(restaurantId)) {
            throw new BadRequestException("Food item does not belong to this restaurant!");
        }

        // Update basic attributes
        item.setName(itemDto.getNameString() != null ? itemDto.getNameString() : itemDto.getName().get("en"));
        item.setDescription(itemDto.getDescriptionString() != null ? itemDto.getDescriptionString() : itemDto.getDesc().get("en"));
        item.setPrice(itemDto.getPrice());
        item.setIsVeg(itemDto.getDiet() != null ? itemDto.getDiet().equalsIgnoreCase("veg") : item.getIsVeg());
        if (itemDto.getIsAvailable() != null) {
            item.setIsAvailable(itemDto.getIsAvailable());
        }
        if (itemDto.getImageUrl() != null) {
            item.setImageUrl(itemDto.getImageUrl());
        }

        // Category updates
        if (itemDto.getCategory() != null && !item.getCategory().getName().equalsIgnoreCase(itemDto.getCategory())) {
            String newCatName = itemDto.getCategory();
            FoodCategory category = categoryRepository.findByRestaurantId(restaurantId).stream()
                    .filter(c -> c.getName().equalsIgnoreCase(newCatName))
                    .findFirst()
                    .orElseGet(() -> categoryRepository.save(
                            FoodCategory.builder()
                                    .name(newCatName)
                                    .restaurant(item.getRestaurant())
                                    .status("active")
                                    .build()
                    ));
            item.setCategory(category);
        }

        foodItemRepository.save(item);
        return FoodMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public void deleteFoodItem(Long restaurantId, Long foodItemId) {
        FoodItem item = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found"));

        if (!item.getRestaurant().getId().equals(restaurantId)) {
            throw new BadRequestException("Food item does not belong to this restaurant!");
        }

        foodItemRepository.delete(item);
    }

    @Override
    public List<FoodItemDto> getFoodItems(Long restaurantId) {
        return foodItemRepository.findByRestaurantId(restaurantId).stream()
                .map(FoodMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDto> getOrders(Long restaurantId) {
        return orderRepository.findByRestaurantId(restaurantId).stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDto updateOrderStatus(Long restaurantId, Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getRestaurant().getId().equals(restaurantId)) {
            throw new BadRequestException("Order does not belong to this restaurant!");
        }

        order.setStatus(status.toLowerCase());
        
        // Update payment status as paid if order is completed
        if (status.equalsIgnoreCase("completed")) {
            order.setPaymentStatus("Paid");
            paymentRepository.findByOrderId(orderId).ifPresent(p -> {
                p.setPaymentStatus("Success");
                paymentRepository.save(p);
            });
        }
        
        orderRepository.save(order);

        // Notify customer
        notificationRepository.save(Notification.builder()
                .user(order.getCustomer().getUser())
                .title("Order Status Updated")
                .message("Your order from " + order.getRestaurant().getName() + " is now " + status.toUpperCase())
                .build());

        return OrderMapper.toDto(order);
    }

    @Override
    public Map<String, Object> getDashboardStats(Long restaurantId) {
        List<Order> orders = orderRepository.findByRestaurantId(restaurantId);
        List<Review> reviews = reviewRepository.findByRestaurantId(restaurantId);

        double totalEarnings = orders.stream()
                .filter(o -> o.getStatus().equalsIgnoreCase("completed"))
                .mapToDouble(Order::getTotalAmount)
                .sum();

        long activeOrders = orders.stream()
                .filter(o -> List.of("new", "preparing", "ready", "delivery").contains(o.getStatus().toLowerCase()))
                .count();

        double avgRating = reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(4.0);

        Map<String, Object> stats = new HashMap<>();
        stats.put("earnings", totalEarnings);
        stats.put("totalOrders", orders.size());
        stats.put("activeOrders", activeOrders);
        stats.put("rating", avgRating);
        stats.put("menuCount", foodItemRepository.findByRestaurantId(restaurantId).size());

        return stats;
    }
}
