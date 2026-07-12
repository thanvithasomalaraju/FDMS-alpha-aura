package com.madfood.backend.service;

import com.madfood.backend.dto.DeliveryPartnerDto;

public interface DeliveryPartnerService {
    DeliveryPartnerDto getProfile(Long partnerId);
    DeliveryPartnerDto updateProfile(Long partnerId, DeliveryPartnerDto profileDto);
    DeliveryPartnerDto submitApplication(Long partnerId, DeliveryPartnerDto appDto);
}
