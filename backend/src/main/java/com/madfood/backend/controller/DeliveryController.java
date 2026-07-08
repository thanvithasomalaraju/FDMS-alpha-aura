package com.madfood.backend.controller;

import com.madfood.backend.dto.DeliveryApplicationRequest;
import com.madfood.backend.model.DeliveryApplication;
import com.madfood.backend.repository.DeliveryApplicationRepository;
import com.madfood.backend.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery/partners")
@Validated
public class DeliveryController {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryController.class);

    private final FileStorageService fileStorageService;
    private final DeliveryApplicationRepository applicationRepository;

    public DeliveryController(FileStorageService fileStorageService, DeliveryApplicationRepository applicationRepository) {
        this.fileStorageService = fileStorageService;
        this.applicationRepository = applicationRepository;
    }

    @PostMapping("/apply")
    public ResponseEntity<?> apply(@Valid @ModelAttribute DeliveryApplicationRequest request) {
        logger.info("New delivery application from {}", request.getPhone());

        // Basic file checks
        MultipartFile[] files = new MultipartFile[]{request.getPhoto(), request.getLicense(), request.getRc(), request.getAadhar()};
        String[] names = new String[]{"photo", "license", "rc", "aadhar"};
        for (int i = 0; i < files.length; i++) {
            MultipartFile f = files[i];
            if (f != null && !f.isEmpty()) {
                if (f.getSize() > 5 * 1024 * 1024) {
                    logger.warn("File {} too large ({} bytes)", names[i], f.getSize());
                    return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Each file must be <= 5MB"));
                }
                String ct = f.getContentType();
                if (ct != null && !ct.startsWith("image/") && !ct.equals("application/pdf")) {
                    logger.warn("File {} has invalid content type: {}", names[i], ct);
                    return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Files must be images or PDFs"));
                }
            }
        }

        // Save files and persist entity
        DeliveryApplication app = new DeliveryApplication();
        app.setFullName(request.getFullName());
        app.setPhone(request.getPhone());
        app.setSource(request.getSource());

        try {
            if (request.getPhoto() != null && !request.getPhoto().isEmpty()) {
                String stored = fileStorageService.store(request.getPhoto());
                app.setPhotoPath(stored);
            }
            if (request.getLicense() != null && !request.getLicense().isEmpty()) {
                String stored = fileStorageService.store(request.getLicense());
                app.setLicensePath(stored);
            }
            if (request.getRc() != null && !request.getRc().isEmpty()) {
                String stored = fileStorageService.store(request.getRc());
                app.setRcPath(stored);
            }
            if (request.getAadhar() != null && !request.getAadhar().isEmpty()) {
                String stored = fileStorageService.store(request.getAadhar());
                app.setAadharPath(stored);
            }

            DeliveryApplication saved = applicationRepository.save(app);
            logger.info("Saved delivery application id={}", saved.getId());
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "Application received");
            resp.put("id", saved.getId());
            return ResponseEntity.ok(resp);
        } catch (Exception ex) {
            logger.error("Failed to save delivery application", ex);
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Failed to process application"));
        }
    }
}
