package com.madfood.backend.dto;

import lombok.*;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodItemDto {
    private String id; // "item_1" or string representation of numeric ID
    private Long numericId;
    private Map<String, String> name;
    private String nameString;
    private Map<String, String> desc;
    private String descriptionString;
    private Double price;
    private String category;
    private String diet;
    private Double rate;
    private String img;
    private String imageUrl;
    private String restaurant;
    private String special;
    private String chef;
    private Long categoryId;
    private Long restaurantId;
    private Boolean isAvailable;
}
