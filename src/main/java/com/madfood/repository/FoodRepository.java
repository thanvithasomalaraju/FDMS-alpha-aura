package com.madfood.repository;

import com.madfood.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    List<Food> findByRestaurantId(Long restaurantId);
    List<Food> findByRestaurantIdAndIsAvailableTrue(Long restaurantId);
    List<Food> findByCategory(String category);
    List<Food> findByDietType(String dietType);
}
