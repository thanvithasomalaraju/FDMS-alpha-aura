package com.fdms.service;

import com.fdms.dto.MenuItemDto;
import com.fdms.entity.MenuItem;
import com.fdms.entity.Restaurant;
import com.fdms.repository.MenuItemRepository;
import com.fdms.repository.RestaurantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuService {
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public MenuItemDto createMenuItem(MenuItemDto menuItemDto, Long restaurantId) {
        log.info("Creating menu item for restaurant ID: {}", restaurantId);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found with ID: " + restaurantId));

        MenuItem menuItem = MenuItem.builder()
                .name(menuItemDto.getName())
                .description(menuItemDto.getDescription())
                .price(menuItemDto.getPrice())
                .category(menuItemDto.getCategory())
                .imageUrl(menuItemDto.getImageUrl())
                .isVegetarian(menuItemDto.getIsVegetarian())
                .isAvailable(true)
                .preparationTime(menuItemDto.getPreparationTime())
                .restaurant(restaurant)
                .build();

        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        log.info("Menu item created successfully with ID: {}", savedMenuItem.getId());
        return mapToMenuItemDto(savedMenuItem);
    }

    public MenuItemDto getMenuItemById(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found with ID: " + id));
        return mapToMenuItemDto(menuItem);
    }

    public List<MenuItemDto> getMenuByRestaurantId(Long restaurantId) {
        return menuItemRepository.findByRestaurantIdAndIsAvailableTrue(restaurantId).stream()
                .map(this::mapToMenuItemDto)
                .collect(Collectors.toList());
    }

    public List<MenuItemDto> getAllMenuItemsByRestaurantId(Long restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId).stream()
                .map(this::mapToMenuItemDto)
                .collect(Collectors.toList());
    }

    public List<MenuItemDto> searchMenuItems(Long restaurantId, String query) {
        return menuItemRepository.findByRestaurantIdAndNameContainingIgnoreCase(restaurantId, query).stream()
                .map(this::mapToMenuItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public MenuItemDto updateMenuItem(Long id, MenuItemDto menuItemDto) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found with ID: " + id));

        if (menuItemDto.getName() != null) menuItem.setName(menuItemDto.getName());
        if (menuItemDto.getDescription() != null) menuItem.setDescription(menuItemDto.getDescription());
        if (menuItemDto.getPrice() != null) menuItem.setPrice(menuItemDto.getPrice());
        if (menuItemDto.getCategory() != null) menuItem.setCategory(menuItemDto.getCategory());
        if (menuItemDto.getImageUrl() != null) menuItem.setImageUrl(menuItemDto.getImageUrl());
        if (menuItemDto.getIsVegetarian() != null) menuItem.setIsVegetarian(menuItemDto.getIsVegetarian());
        if (menuItemDto.getIsAvailable() != null) menuItem.setIsAvailable(menuItemDto.getIsAvailable());
        if (menuItemDto.getPreparationTime() != null) menuItem.setPreparationTime(menuItemDto.getPreparationTime());

        MenuItem updatedMenuItem = menuItemRepository.save(menuItem);
        log.info("Menu item updated successfully: {}", id);
        return mapToMenuItemDto(updatedMenuItem);
    }

    @Transactional
    public void deleteMenuItem(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found with ID: " + id));
        menuItemRepository.delete(menuItem);
        log.info("Menu item deleted successfully: {}", id);
    }

    private MenuItemDto mapToMenuItemDto(MenuItem menuItem) {
        return MenuItemDto.builder()
                .id(menuItem.getId())
                .name(menuItem.getName())
                .description(menuItem.getDescription())
                .price(menuItem.getPrice())
                .category(menuItem.getCategory())
                .imageUrl(menuItem.getImageUrl())
                .isVegetarian(menuItem.getIsVegetarian())
                .isAvailable(menuItem.getIsAvailable())
                .preparationTime(menuItem.getPreparationTime())
                .restaurantId(menuItem.getRestaurant().getId())
                .createdAt(menuItem.getCreatedAt())
                .updatedAt(menuItem.getUpdatedAt())
                .build();
    }
}
