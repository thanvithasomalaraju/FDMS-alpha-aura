package com.fdms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantDto {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String city;
    private String postalCode;
    private String phoneNumber;
    private String email;
    private Double latitude;
    private Double longitude;
    private String cuisineType;
    private Double rating;
    private Boolean isActive;
    private Long ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
