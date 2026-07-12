package com.madfood.backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {
    private Long id;
    private String user;
    private String customerName;
    private Long customerId;
    private String restaurant;
    private Long restaurantId;
    private Integer rating;
    private String review;
    private String comment;
    private String status;
    private String date;
}
