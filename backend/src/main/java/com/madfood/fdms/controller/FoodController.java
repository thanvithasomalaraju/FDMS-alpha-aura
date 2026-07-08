package com.madfood.fdms.controller;

import com.madfood.fdms.model.Food;
import com.madfood.fdms.model.Restaurant;
import com.madfood.fdms.repository.FoodRepository;
import com.madfood.fdms.repository.RestaurantRepository;
import com.madfood.fdms.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/restaurants/{rid}/foods")
public class FoodController {
    @Autowired private RestaurantRepository restaurantRepo;
    @Autowired private FoodRepository foodRepo;
    @Autowired private FileStorageService storage;

    @PostMapping
    public ResponseEntity<?> addFood(
      @PathVariable Long rid,
      @RequestParam String name,
      @RequestParam(required=false) String description,
      @RequestParam BigDecimal price,
      @RequestParam(required=false) Boolean isVeg,
      @RequestParam(required=false) Boolean available,
      @RequestParam(required=false) MultipartFile image
    ) {
        Restaurant rest = restaurantRepo.findById(rid).orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
        Food f = new Food();
        f.setRestaurant(rest);
        f.setName(name);
        f.setDescription(description);
        f.setPrice(price);
        f.setVeg(Boolean.TRUE.equals(isVeg));
        f.setAvailable(Boolean.TRUE.equals(available));
        if (image != null && !image.isEmpty()) f.setImagePath(storage.store(image, "foods"));
        foodRepo.save(f);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true, "data", f));
    }

    @GetMapping
    public ResponseEntity<?> list(@PathVariable Long rid) {
        return ResponseEntity.ok(Map.of("success", true, "data", Map.of("items", foodRepo.findByRestaurantId(rid))));
    }
}
