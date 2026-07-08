package com.madfood.backend.controller;

import com.madfood.backend.model.DeliveryApplication;
import com.madfood.backend.repository.DeliveryApplicationRepository;
import com.madfood.backend.service.FileStorageService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery/partners")
public class DeliveryController {

    private final FileStorageService fileStorageService;
    private final DeliveryApplicationRepository repository;

    public DeliveryController(FileStorageService fileStorageService, DeliveryApplicationRepository repository) {
        this.fileStorageService = fileStorageService;
        this.repository = repository;
    }

    @PostMapping(path = "/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> apply(
            @RequestParam("fullName") @NotBlank String fullName,
            @RequestParam("phone") @NotBlank String phone,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @RequestParam(value = "license", required = false) MultipartFile license,
            @RequestParam(value = "rc", required = false) MultipartFile rc,
            @RequestParam(value = "aadhar", required = false) MultipartFile aadhar
    ) {
        try {
            String photoPath = fileStorageService.storeFile(photo);
            String licensePath = fileStorageService.storeFile(license);
            String rcPath = fileStorageService.storeFile(rc);
            String aadharPath = fileStorageService.storeFile(aadhar);

            DeliveryApplication app = new DeliveryApplication();
            app.setFullName(fullName);
            app.setPhone(phone);
            app.setSource(source);
            app.setPhotoPath(photoPath);
            app.setLicensePath(licensePath);
            app.setRcPath(rcPath);
            app.setAadharPath(aadharPath);

            DeliveryApplication saved = repository.save(app);

            Map<String, Object> body = new HashMap<>();
            body.put("success", true);
            body.put("message", "Application received");
            body.put("id", saved.getId());

            return ResponseEntity.ok(body);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", "Failed to store files"));
        }
    }

    // Serve files (unsafe for production - no auth)
    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<byte[]> getFile(@PathVariable String filename) throws IOException {
        Path uploads = Paths.get("uploads").toAbsolutePath().normalize();
        Path file = uploads.resolve(filename);
        if (!Files.exists(file)) return ResponseEntity.notFound().build();
        byte[] data = Files.readAllBytes(file);
        String contentType = Files.probeContentType(file);
        if (contentType == null) contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
