package com.fdms.dto;

import com.fdms.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private Long id;
    private String orderNumber;
    private Long userId;
    private Long restaurantId;
    private Long deliveryPartnerId;
    private String deliveryAddress;
    private String deliveryCity;
    private String deliveryPostalCode;
    private BigDecimal totalAmount;
    private BigDecimal deliveryFee;
    private BigDecimal taxAmount;
    private Order.OrderStatus status;
    private String specialInstructions;
    private Set<OrderItemDto> orderItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
