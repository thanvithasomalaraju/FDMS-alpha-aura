package com.madfood.backend.mapper;

import com.madfood.backend.dto.OrderDto;
import com.madfood.backend.dto.OrderItemDto;
import com.madfood.backend.entity.Order;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class OrderMapper {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static OrderDto toDto(Order order) {
        if (order == null) return null;

        var items = order.getOrderItems().stream()
                .map(item -> OrderItemDto.builder()
                        .id(item.getId())
                        .foodItemId(item.getFoodItem().getId())
                        .foodName(item.getFoodItem().getName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .collect(Collectors.toList());

        String itemsSummary = items.stream()
                .map(item -> item.getQuantity() + "x " + item.getFoodName())
                .collect(Collectors.joining(", "));

        return OrderDto.builder()
                .id("ORD-" + (order.getId() + 48200))
                .numericId(order.getId())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getName())
                .restaurantId(order.getRestaurant().getId())
                .restaurantName(order.getRestaurant().getName())
                .amount(order.getTotalAmount())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .payment(order.getPaymentStatus())
                .paymentMethod(order.getPaymentMethod())
                .deliveryAddress(order.getDeliveryAddress())
                .date(order.getOrderDate() != null ? order.getOrderDate().format(formatter) : "")
                .items(items)
                .itemsSummary(itemsSummary)
                .build();
    }
}
