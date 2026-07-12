package com.madfood.backend.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDto {
    private Long id;
    private Long customerId;
    private List<CartItemDto> items;
    private Double total;
}
