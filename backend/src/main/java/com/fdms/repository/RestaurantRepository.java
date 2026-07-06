package com.fdms.repository;

import com.fdms.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findByEmail(String email);
    List<Restaurant> findByCity(String city);
    List<Restaurant> findByIsActiveTrue();
    List<Restaurant> findByCuisineTypeContainingIgnoreCase(String cuisineType);
    List<Restaurant> findByNameContainingIgnoreCase(String name);
    Optional<Restaurant> findByOwnerId(Long ownerId);
}
