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
import java.io.IOException;
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

        // Basic file checks. If the frontend has uploaded files to S3 and provided keys, we skip file validations for those fields.
        MultipartFile[] files = new MultipartFile[]{request.getPhoto(), request.getLicense(), request.getRc(), request.getAadhar()};
        String[] names = new String[]{"photo", "license", "rc", "aadhar"};
        String[] keys = new String[]{request.getPhotoKey(), request.getLicenseKey(), request.getRcKey(), request.getAadharKey()};
        for (int i = 0; i < files.length; i++) {
            MultipartFile f = files[i];
            String providedKey = keys[i];
            if ((f == null || f.isEmpty()) && (providedKey == null || providedKey.isBlank())) {
                // no file provided at all — that's acceptable (fields are optional)
                continue;
            }
            if (providedKey != null && !providedKey.isBlank()) {
                // presigned-uploaded file key provided by frontend — skip size/content-type checks
                continue;
            }
            // At this point we have a MultipartFile present; validate size and content type
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
            // For each field: prefer provided key (S3 object key). If not provided but MultipartFile present, store locally via FileStorageService.
            if (request.getPhotoKey() != null && !request.getPhotoKey().isBlank()) {
                app.setPhotoPath(request.getPhotoKey());
            } else if (request.getPhoto() != null && !request.getPhoto().isEmpty()) {
                String stored = fileStorageService.store(request.getPhoto());
                app.setPhotoPath(stored);
            }

            if (request.getLicenseKey() != null && !request.getLicenseKey().isBlank()) {
                app.setLicensePath(request.getLicenseKey());
            } else if (request.getLicense() != null && !request.getLicense().isEmpty()) {
                String stored = fileStorageService.store(request.getLicense());
                app.setLicensePath(stored);
            }

            if (request.getRcKey() != null && !request.getRcKey().isBlank()) {
                app.setRcPath(request.getRcKey());
            } else if (request.getRc() != null && !request.getRc().isEmpty()) {
                String stored = fileStorageService.store(request.getRc());
                app.setRcPath(stored);
            }

            if (request.getAadharKey() != null && !request.getAadharKey().isBlank()) {
                app.setAadharPath(request.getAadharKey());
            } else if (request.getAadhar() != null && !request.getAadhar().isEmpty()) {
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
        } catch (IOException ex) {
            logger.error("Failed to store file", ex);
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Failed to store uploaded file"));
        } catch (Exception ex) {
            logger.error("Failed to save delivery application", ex);
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Failed to process application"));
        }
    }
}
