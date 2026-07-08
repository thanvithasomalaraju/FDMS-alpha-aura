package com.madfood.backend.controller;

import com.madfood.backend.model.DeliveryApplication;
import com.madfood.backend.repository.DeliveryApplicationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/delivery/partners")
public class DeliveryAdminController {

    private final DeliveryApplicationRepository repository;

    public DeliveryAdminController(DeliveryApplicationRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/applications")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listApplications() {
        List<DeliveryApplication> apps = repository.findAll();
        List<Object> summary = apps.stream().map(a -> {
            return new Object() {
                public final Long id = a.getId();
                public final String fullName = a.getFullName();
                public final String phone = a.getPhone();
                public final String source = a.getSource();
                public final java.time.Instant createdAt = a.getCreatedAt();
            };
        }).collect(Collectors.toList());
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/applications/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getApplication(@PathVariable Long id) {
        Optional<DeliveryApplication> o = repository.findById(id);
        if (o.isEmpty()) return ResponseEntity.notFound().build();
        DeliveryApplication a = o.get();
        return ResponseEntity.ok(a);
    }
}
