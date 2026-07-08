package com.madfood.fdms.controller;

import com.madfood.fdms.model.DeliveryApplication;
import com.madfood.fdms.repository.DeliveryApplicationRepository;
import com.madfood.fdms.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {
    @Autowired private DeliveryApplicationRepository repo;
    @Autowired private FileStorageService storage;

    @PostMapping("/applications")
    public ResponseEntity<?> apply(
        @RequestParam String fullName,
        @RequestParam String phone,
        @RequestParam(required = false) String source,
        @RequestParam(required = false) MultipartFile photo,
        @RequestParam(required = false) MultipartFile license,
        @RequestParam(required = false) MultipartFile rc,
        @RequestParam(required = false) MultipartFile aadhar
    ) {
        DeliveryApplication app = new DeliveryApplication();
        app.setFullName(fullName);
        app.setPhone(phone);
        app.setSource(source);
        if (photo != null && !photo.isEmpty()) app.setPhotoPath(storage.store(photo, "delivery"));
        if (license != null && !license.isEmpty()) app.setLicensePath(storage.store(license, "delivery"));
        if (rc != null && !rc.isEmpty()) app.setRcPath(storage.store(rc, "delivery"));
        if (aadhar != null && !aadhar.isEmpty()) app.setAadharPath(storage.store(aadhar, "delivery"));
        repo.save(app);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true, "data", Map.of("applicationId", app.getId(), "status", app.getStatus()), "message", "Application received"));
    }
}
