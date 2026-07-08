package com.madfood.backend.controller;

import com.madfood.backend.service.S3PresignService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/storage")
public class StorageController {

    private final S3PresignService presignService;

    public StorageController(S3PresignService presignService) {
        this.presignService = presignService;
    }

    // Request a presigned URL to upload a single file directly to S3
    @PostMapping("/presign")
    public ResponseEntity<?> presignUpload(@RequestBody Map<String, String> body) {
        String filename = body.get("filename");
        String contentType = body.getOrDefault("contentType", "application/octet-stream");
        int validMinutes = 15;
        if (body.containsKey("validMinutes")) {
            try { validMinutes = Integer.parseInt(body.get("validMinutes")); } catch (Exception ignored) {}
        }
        var r = presignService.presignUpload(filename, contentType, validMinutes);
        return ResponseEntity.ok(Map.of("success", true, "key", r.key(), "uploadUrl", r.url(), "expiresMinutes", r.expiresMinutes()));
    }

    // Admin-only: get a presigned download URL
    @GetMapping("/download")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> presignDownload(@RequestParam String key) {
        var r = presignService.presignDownload(key, 15);
        return ResponseEntity.ok(Map.of("success", true, "key", r.key(), "downloadUrl", r.url(), "expiresMinutes", r.expiresMinutes()));
    }
}
