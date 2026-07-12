package com.madfood.backend.service.impl;

import com.madfood.backend.dto.DeliveryPartnerDto;
import com.madfood.backend.entity.DeliveryPartner;
import com.madfood.backend.exception.ResourceNotFoundException;
import com.madfood.backend.mapper.DeliveryPartnerMapper;
import com.madfood.backend.repository.DeliveryPartnerRepository;
import com.madfood.backend.service.DeliveryPartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeliveryPartnerServiceImpl implements DeliveryPartnerService {

    @Autowired
    private DeliveryPartnerRepository repository;

    @Override
    public DeliveryPartnerDto getProfile(Long partnerId) {
        DeliveryPartner partner = repository.findById(partnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery partner not found"));
        return DeliveryPartnerMapper.toDto(partner);
    }

    @Override
    @Transactional
    public DeliveryPartnerDto updateProfile(Long partnerId, DeliveryPartnerDto profileDto) {
        DeliveryPartner partner = repository.findById(partnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery partner not found"));
        partner.setFullName(profileDto.getFullName());
        partner.setPhone(profileDto.getPhone());
        repository.save(partner);
        return DeliveryPartnerMapper.toDto(partner);
    }

    @Override
    @Transactional
    public DeliveryPartnerDto submitApplication(Long partnerId, DeliveryPartnerDto appDto) {
        DeliveryPartner partner = repository.findById(partnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery partner not found"));

        partner.setFullName(appDto.getFullName());
        partner.setPhone(appDto.getPhone());
        partner.setPhotoUrl(appDto.getPhotoUrl());
        partner.setLicenseUrl(appDto.getLicenseUrl());
        partner.setRcUrl(appDto.getRcUrl());
        partner.setAadharUrl(appDto.getAadharUrl());
        partner.setStatus("PENDING"); // Reset status to PENDING upon submission

        repository.save(partner);
        return DeliveryPartnerMapper.toDto(partner);
    }
}
