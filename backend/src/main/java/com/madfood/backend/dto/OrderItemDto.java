package com.madfood.backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
    private Long id;
    private Long foodItemId;
    private String foodName;
    private Integer quantity;
    private Double price;
}
