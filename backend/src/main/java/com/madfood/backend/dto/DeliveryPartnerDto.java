package com.madfood.backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryPartnerDto {
    private String id;
    private Long numericId;
    private String fullName;
    private String phone;
    private String email;
    private String photoUrl;
    private String licenseUrl;
    private String rcUrl;
    private String aadharUrl;
    private String status;
    private String rejectionReason;
}
