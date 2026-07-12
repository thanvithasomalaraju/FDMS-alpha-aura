package com.madfood.backend.mapper;

import com.madfood.backend.dto.DeliveryPartnerDto;
import com.madfood.backend.entity.DeliveryPartner;

public class DeliveryPartnerMapper {
    public static DeliveryPartnerDto toDto(DeliveryPartner partner) {
        if (partner == null) return null;
        return DeliveryPartnerDto.builder()
                .id("D" + (partner.getId() + 300))
                .numericId(partner.getId())
                .fullName(partner.getFullName())
                .phone(partner.getPhone())
                .email(partner.getEmail())
                .photoUrl(partner.getPhotoUrl())
                .licenseUrl(partner.getLicenseUrl())
                .rcUrl(partner.getRcUrl())
                .aadharUrl(partner.getAadharUrl())
                .status(partner.getStatus())
                .rejectionReason(partner.getRejectionReason())
                .build();
    }
}
