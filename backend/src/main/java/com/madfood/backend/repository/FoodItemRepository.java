package com.madfood.backend.repository;

import com.madfood.backend.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    List<FoodItem> findByRestaurantId(Long restaurantId);
    List<FoodItem> findByRestaurantIdAndIsAvailable(Long restaurantId, Boolean isAvailable);
    List<FoodItem> findByCategoryId(Long categoryId);
    List<FoodItem> findByNameContainingIgnoreCase(String name);
}
