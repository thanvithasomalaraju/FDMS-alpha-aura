package com.madfood.backend.dto;

import lombok.*;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantDto {
    private String id;
    private Long numericId;
    private Map<String, String> name;
    private String nameString;
    private String ownerName;
    private String email;
    private String phone;
    private String address;
    private String status;
    private String rejectionReason;
    
    // For customer portal compatibility:
    private Double rate;
    private String ratingsCount;
    private String timing;
    private String chef;
    private String openingTime;
    private String closingTime;
    private String img;

    // Document URLs for Admin/Restaurant portals:
    private String logoUrl;
    private String docGst;
    private String docFssai;
    private String docPan;
    private String docMenu;
}
