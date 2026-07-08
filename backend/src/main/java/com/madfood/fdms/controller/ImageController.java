package com.madfood.fdms.controller;

import com.madfood.fdms.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
public class ImageController {
    @Autowired private FileStorageService storage;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, @RequestParam(required=false) String subfolder) {
        if (file == null || file.isEmpty()) return ResponseEntity.badRequest().body(Map.of("success", false, "message", "No file"));
        String path = storage.store(file, (subfolder==null||subfolder.isBlank()) ? "general" : subfolder);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true, "data", Map.of("url", path), "message", "Uploaded"));
    }
}
