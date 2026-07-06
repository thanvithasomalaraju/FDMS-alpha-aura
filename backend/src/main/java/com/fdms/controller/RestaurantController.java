package com.fdms.controller;

import com.fdms.dto.RestaurantDto;
import com.fdms.service.RestaurantService;
import com.fdms.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestaurantController {
    private final RestaurantService restaurantService;

    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<RestaurantDto> createRestaurant(@Valid @RequestBody RestaurantDto restaurantDto) {
        log.info("Creating restaurant: {}", restaurantDto.getName());
        Long userId = SecurityUtils.getCurrentUserId();
        RestaurantDto createdRestaurant = restaurantService.createRestaurant(restaurantDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRestaurant);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDto> getRestaurantById(@PathVariable Long id) {
        log.info("Getting restaurant by ID: {}", id);
        RestaurantDto restaurant = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(restaurant);
    }

    @GetMapping
    public ResponseEntity<List<RestaurantDto>> getAllRestaurants() {
        log.info("Getting all active restaurants");
        List<RestaurantDto> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<RestaurantDto>> getRestaurantsByCity(@PathVariable String city) {
        log.info("Getting restaurants by city: {}", city);
        List<RestaurantDto> restaurants = restaurantService.getRestaurantsByCity(city);
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/search")
    public ResponseEntity<List<RestaurantDto>> searchRestaurants(@RequestParam String query) {
        log.info("Searching restaurants with query: {}", query);
        List<RestaurantDto> restaurants = restaurantService.searchRestaurants(query);
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/cuisine/{cuisineType}")
    public ResponseEntity<List<RestaurantDto>> getRestaurantsByCuisine(@PathVariable String cuisineType) {
        log.info("Getting restaurants by cuisine: {}", cuisineType);
        List<RestaurantDto> restaurants = restaurantService.getRestaurantsByCuisine(cuisineType);
        return ResponseEntity.ok(restaurants);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<RestaurantDto> updateRestaurant(@PathVariable Long id, @Valid @RequestBody RestaurantDto restaurantDto) {
        log.info("Updating restaurant with ID: {}", id);
        RestaurantDto updatedRestaurant = restaurantService.updateRestaurant(id, restaurantDto);
        return ResponseEntity.ok(updatedRestaurant);
    }

    @GetMapping("/owner/my-restaurant")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<RestaurantDto> getMyRestaurant() {
        log.info("Getting my restaurant");
        Long userId = SecurityUtils.getCurrentUserId();
        RestaurantDto restaurant = restaurantService.getRestaurantByOwnerId(userId);
        return ResponseEntity.ok(restaurant);
    }
}
