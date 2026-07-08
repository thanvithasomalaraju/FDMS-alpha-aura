package com.madfood.service;

import com.madfood.entity.Restaurant;
import com.madfood.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    public Restaurant createRestaurant(Restaurant restaurant) {
        restaurant.setIsApproved(false);
        return restaurantRepository.save(restaurant);
    }

    public Optional<Restaurant> getRestaurantById(Long id) {
        return restaurantRepository.findById(id);
    }

    public Optional<Restaurant> getRestaurantByUserId(Long userId) {
        return restaurantRepository.findByUserId(userId);
    }

    public List<Restaurant> getAllApprovedRestaurants() {
        return restaurantRepository.findByIsApprovedTrue();
    }

    public List<Restaurant> getAllOpenRestaurants() {
        return restaurantRepository.findByIsOpenTrue();
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public Restaurant updateRestaurant(Long id, Restaurant restaurantDetails) {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(() -> new RuntimeException("Restaurant not found"));
        if (restaurantDetails.getRestaurantName() != null) restaurant.setRestaurantName(restaurantDetails.getRestaurantName());
        if (restaurantDetails.getDescription() != null) restaurant.setDescription(restaurantDetails.getDescription());
        if (restaurantDetails.getAddress() != null) restaurant.setAddress(restaurantDetails.getAddress());
        if (restaurantDetails.getIsOpen() != null) restaurant.setIsOpen(restaurantDetails.getIsOpen());
        return restaurantRepository.save(restaurant);
    }

    public Restaurant approveRestaurant(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(() -> new RuntimeException("Restaurant not found"));
        restaurant.setIsApproved(true);
        return restaurantRepository.save(restaurant);
    }

    public void deleteRestaurant(Long id) {
        restaurantRepository.deleteById(id);
    }
}
