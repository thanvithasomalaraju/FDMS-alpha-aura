package com.fdms.controller;

import com.fdms.dto.MenuItemDto;
import com.fdms.service.MenuService;
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
@RequestMapping("/api/menu")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MenuController {
    private final MenuService menuService;

    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<MenuItemDto> createMenuItem(
            @RequestParam Long restaurantId,
            @Valid @RequestBody MenuItemDto menuItemDto) {
        log.info("Creating menu item for restaurant ID: {}", restaurantId);
        MenuItemDto createdItem = menuService.createMenuItem(menuItemDto, restaurantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemDto> getMenuItemById(@PathVariable Long id) {
        log.info("Getting menu item by ID: {}", id);
        MenuItemDto menuItem = menuService.getMenuItemById(id);
        return ResponseEntity.ok(menuItem);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuItemDto>> getMenuByRestaurantId(@PathVariable Long restaurantId) {
        log.info("Getting menu for restaurant ID: {}", restaurantId);
        List<MenuItemDto> menu = menuService.getMenuByRestaurantId(restaurantId);
        return ResponseEntity.ok(menu);
    }

    @GetMapping("/restaurant/{restaurantId}/all")
    public ResponseEntity<List<MenuItemDto>> getAllMenuItemsByRestaurantId(@PathVariable Long restaurantId) {
        log.info("Getting all menu items for restaurant ID: {}", restaurantId);
        List<MenuItemDto> menu = menuService.getAllMenuItemsByRestaurantId(restaurantId);
        return ResponseEntity.ok(menu);
    }

    @GetMapping("/search")
    public ResponseEntity<List<MenuItemDto>> searchMenuItems(
            @RequestParam Long restaurantId,
            @RequestParam String query) {
        log.info("Searching menu items in restaurant {} with query: {}", restaurantId, query);
        List<MenuItemDto> items = menuService.searchMenuItems(restaurantId, query);
        return ResponseEntity.ok(items);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<MenuItemDto> updateMenuItem(
            @PathVariable Long id,
            @Valid @RequestBody MenuItemDto menuItemDto) {
        log.info("Updating menu item with ID: {}", id);
        MenuItemDto updatedItem = menuService.updateMenuItem(id, menuItemDto);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        log.info("Deleting menu item with ID: {}", id);
        menuService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}
