package com.madfood.backend.controller;

import com.madfood.backend.dto.DeliveryPartnerDto;
import com.madfood.backend.service.DeliveryPartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryPartnerController {

    @Autowired
    private DeliveryPartnerService deliveryPartnerService;

    @GetMapping("/{partnerId}/profile")
    public ResponseEntity<DeliveryPartnerDto> getProfile(@PathVariable Long partnerId) {
        return ResponseEntity.ok(deliveryPartnerService.getProfile(partnerId));
    }

    @PutMapping("/{partnerId}/profile")
    public ResponseEntity<DeliveryPartnerDto> updateProfile(@PathVariable Long partnerId, @RequestBody DeliveryPartnerDto dto) {
        return ResponseEntity.ok(deliveryPartnerService.updateProfile(partnerId, dto));
    }

    @PostMapping("/{partnerId}/apply")
    public ResponseEntity<DeliveryPartnerDto> submitApplication(@PathVariable Long partnerId, @RequestBody DeliveryPartnerDto dto) {
        return ResponseEntity.ok(deliveryPartnerService.submitApplication(partnerId, dto));
    }
}
