package com.madfood.service;

import com.madfood.entity.Food;
import com.madfood.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FoodService {

    @Autowired
    private FoodRepository foodRepository;

    public Food createFood(Food food) {
        return foodRepository.save(food);
    }

    public Optional<Food> getFoodById(Long id) {
        return foodRepository.findById(id);
    }

    public List<Food> getFoodsByRestaurant(Long restaurantId) {
        return foodRepository.findByRestaurantId(restaurantId);
    }

    public List<Food> getAvailableFoodsByRestaurant(Long restaurantId) {
        return foodRepository.findByRestaurantIdAndIsAvailableTrue(restaurantId);
    }

    public List<Food> getFoodsByCategory(String category) {
        return foodRepository.findByCategory(category);
    }

    public List<Food> getFoodsByDietType(String dietType) {
        return foodRepository.findByDietType(dietType);
    }

    public List<Food> getAllFoods() {
        return foodRepository.findAll();
    }

    public Food updateFood(Long id, Food foodDetails) {
        Food food = foodRepository.findById(id).orElseThrow(() -> new RuntimeException("Food not found"));
        if (foodDetails.getFoodName() != null) food.setFoodName(foodDetails.getFoodName());
        if (foodDetails.getDescription() != null) food.setDescription(foodDetails.getDescription());
        if (foodDetails.getPrice() != null) food.setPrice(foodDetails.getPrice());
        if (foodDetails.getCategory() != null) food.setCategory(foodDetails.getCategory());
        if (foodDetails.getIsAvailable() != null) food.setIsAvailable(foodDetails.getIsAvailable());
        return foodRepository.save(food);
    }

    public void deleteFood(Long id) {
        foodRepository.deleteById(id);
    }
}
