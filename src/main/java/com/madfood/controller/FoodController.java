package com.madfood.controller;

import com.madfood.entity.Food;
import com.madfood.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/foods")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8000"})
public class FoodController {

    @Autowired
    private FoodRepository foodRepository;

    @GetMapping
    public ResponseEntity<List<Food>> getAllFoods() {
        return ResponseEntity.ok(foodRepository.findAll());
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<Food>> getFoodsByRestaurant(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(foodRepository.findByRestaurantId(restaurantId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Food> getFood(@PathVariable Long id) {
        return ResponseEntity.ok(foodRepository.findById(id).orElse(null));
    }

    @PostMapping
    public ResponseEntity<Food> createFood(@RequestBody Food food) {
        return ResponseEntity.ok(foodRepository.save(food));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Food> updateFood(@PathVariable Long id, @RequestBody Food food) {
        food.setId(id);
        return ResponseEntity.ok(foodRepository.save(food));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFood(@PathVariable Long id) {
        foodRepository.deleteById(id);
        return ResponseEntity.ok("Food deleted successfully");
    }
}
