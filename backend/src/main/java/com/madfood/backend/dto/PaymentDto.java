package com.madfood.backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {
    private String txn;
    private String order;
    private Long orderId;
    private String method;
    private Double amount;
    private String status;
}
