package com.madfood.backend.mapper;

import com.madfood.backend.dto.CartDto;
import com.madfood.backend.dto.CartItemDto;
import com.madfood.backend.entity.Cart;
import java.util.stream.Collectors;

public class CartMapper {
    public static CartDto toDto(Cart cart) {
        if (cart == null) return null;
        
        var items = cart.getCartItems().stream()
                .map(item -> CartItemDto.builder()
                        .id(item.getId())
                        .foodItem(FoodMapper.toItemDto(item.getFoodItem()))
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());
                
        double total = items.stream()
                .mapToDouble(item -> item.getFoodItem().getPrice() * item.getQuantity())
                .sum();

        return CartDto.builder()
                .id(cart.getId())
                .customerId(cart.getCustomer().getId())
                .items(items)
                .total(total)
                .build();
    }
}
