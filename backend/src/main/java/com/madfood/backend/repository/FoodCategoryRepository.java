package com.madfood.backend.repository;

import com.madfood.backend.entity.FoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FoodCategoryRepository extends JpaRepository<FoodCategory, Long> {
    List<FoodCategory> findByRestaurantId(Long restaurantId);
    List<FoodCategory> findByRestaurantIdAndStatus(Long restaurantId, String status);
}
