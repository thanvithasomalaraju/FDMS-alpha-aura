package com.fdms.service;

import com.fdms.dto.RestaurantDto;
import com.fdms.entity.Restaurant;
import com.fdms.entity.User;
import com.fdms.repository.RestaurantRepository;
import com.fdms.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    @Transactional
    public RestaurantDto createRestaurant(RestaurantDto restaurantDto, Long ownerId) {
        log.info("Creating restaurant for owner ID: {}", ownerId);

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + ownerId));

        if (restaurantRepository.findByOwnerId(ownerId).isPresent()) {
            throw new IllegalArgumentException("Owner already has a restaurant");
        }

        Restaurant restaurant = Restaurant.builder()
                .name(restaurantDto.getName())
                .description(restaurantDto.getDescription())
                .address(restaurantDto.getAddress())
                .city(restaurantDto.getCity())
                .postalCode(restaurantDto.getPostalCode())
                .phoneNumber(restaurantDto.getPhoneNumber())
                .email(restaurantDto.getEmail())
                .latitude(restaurantDto.getLatitude())
                .longitude(restaurantDto.getLongitude())
                .cuisineType(restaurantDto.getCuisineType())
                .owner(owner)
                .isActive(true)
                .build();

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        log.info("Restaurant created successfully with ID: {}", savedRestaurant.getId());
        return mapToRestaurantDto(savedRestaurant);
    }

    public RestaurantDto getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found with ID: " + id));
        return mapToRestaurantDto(restaurant);
    }

    public List<RestaurantDto> getAllRestaurants() {
        return restaurantRepository.findByIsActiveTrue().stream()
                .map(this::mapToRestaurantDto)
                .collect(Collectors.toList());
    }

    public List<RestaurantDto> getRestaurantsByCity(String city) {
        return restaurantRepository.findByCity(city).stream()
                .map(this::mapToRestaurantDto)
                .collect(Collectors.toList());
    }

    public List<RestaurantDto> searchRestaurants(String query) {
        return restaurantRepository.findByNameContainingIgnoreCase(query).stream()
                .map(this::mapToRestaurantDto)
                .collect(Collectors.toList());
    }

    public List<RestaurantDto> getRestaurantsByCuisine(String cuisineType) {
        return restaurantRepository.findByCuisineTypeContainingIgnoreCase(cuisineType).stream()
                .map(this::mapToRestaurantDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RestaurantDto updateRestaurant(Long id, RestaurantDto restaurantDto) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found with ID: " + id));

        if (restaurantDto.getName() != null) restaurant.setName(restaurantDto.getName());
        if (restaurantDto.getDescription() != null) restaurant.setDescription(restaurantDto.getDescription());
        if (restaurantDto.getAddress() != null) restaurant.setAddress(restaurantDto.getAddress());
        if (restaurantDto.getCity() != null) restaurant.setCity(restaurantDto.getCity());
        if (restaurantDto.getPostalCode() != null) restaurant.setPostalCode(restaurantDto.getPostalCode());
        if (restaurantDto.getPhoneNumber() != null) restaurant.setPhoneNumber(restaurantDto.getPhoneNumber());
        if (restaurantDto.getCuisineType() != null) restaurant.setCuisineType(restaurantDto.getCuisineType());
        if (restaurantDto.getLatitude() != null) restaurant.setLatitude(restaurantDto.getLatitude());
        if (restaurantDto.getLongitude() != null) restaurant.setLongitude(restaurantDto.getLongitude());
        if (restaurantDto.getRating() != null) restaurant.setRating(restaurantDto.getRating());
        if (restaurantDto.getIsActive() != null) restaurant.setIsActive(restaurantDto.getIsActive());

        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        log.info("Restaurant updated successfully: {}", id);
        return mapToRestaurantDto(updatedRestaurant);
    }

    public RestaurantDto getRestaurantByOwnerId(Long ownerId) {
        Restaurant restaurant = restaurantRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("No restaurant found for owner ID: " + ownerId));
        return mapToRestaurantDto(restaurant);
    }

    private RestaurantDto mapToRestaurantDto(Restaurant restaurant) {
        return RestaurantDto.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .description(restaurant.getDescription())
                .address(restaurant.getAddress())
                .city(restaurant.getCity())
                .postalCode(restaurant.getPostalCode())
                .phoneNumber(restaurant.getPhoneNumber())
                .email(restaurant.getEmail())
                .latitude(restaurant.getLatitude())
                .longitude(restaurant.getLongitude())
                .cuisineType(restaurant.getCuisineType())
                .rating(restaurant.getRating())
                .isActive(restaurant.getIsActive())
                .ownerId(restaurant.getOwner().getId())
                .createdAt(restaurant.getCreatedAt())
                .updatedAt(restaurant.getUpdatedAt())
                .build();
    }
}
