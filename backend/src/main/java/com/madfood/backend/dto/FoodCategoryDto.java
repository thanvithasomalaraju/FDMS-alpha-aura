package com.madfood.backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodCategoryDto {
    private Long id;
    private String name;
    private Long restaurantId;
    private Integer itemsCount;
    private String status;
}
