package com.madfood.backend.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private String id;
    private Long numericId;
    private String customerName;
    private Long customerId;
    private String restaurantName;
    private Long restaurantId;
    private Double amount;
    private Double totalAmount;
    private String status;
    private String payment;
    private String paymentMethod;
    private String deliveryAddress;
    private String date;
    private List<OrderItemDto> items;
    private String itemsSummary;
}
