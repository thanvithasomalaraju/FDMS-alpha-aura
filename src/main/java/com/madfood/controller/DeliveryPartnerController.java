package com.madfood.controller;

import com.madfood.entity.DeliveryPartner;
import com.madfood.repository.DeliveryPartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/delivery-partners")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8000"})
public class DeliveryPartnerController {

    @Autowired
    private DeliveryPartnerRepository deliveryPartnerRepository;

    @GetMapping
    public ResponseEntity<List<DeliveryPartner>> getAllDeliveryPartners() {
        return ResponseEntity.ok(deliveryPartnerRepository.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<DeliveryPartner>> getActiveDeliveryPartners() {
        return ResponseEntity.ok(deliveryPartnerRepository.findByIsActiveTrue());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryPartner> getDeliveryPartner(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryPartnerRepository.findById(id).orElse(null));
    }

    @PostMapping
    public ResponseEntity<DeliveryPartner> createDeliveryPartner(@RequestBody DeliveryPartner deliveryPartner) {
        return ResponseEntity.ok(deliveryPartnerRepository.save(deliveryPartner));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryPartner> updateDeliveryPartner(@PathVariable Long id, @RequestBody DeliveryPartner deliveryPartner) {
        deliveryPartner.setId(id);
        return ResponseEntity.ok(deliveryPartnerRepository.save(deliveryPartner));
    }
}
