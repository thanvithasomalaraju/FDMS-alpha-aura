package com.madfood.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(LocalFileStorageService.class);

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Path p = Paths.get(uploadDir);
            if (!Files.exists(p)) Files.createDirectories(p);
            logger.info("LocalFileStorageService using upload dir: {}", p.toAbsolutePath());
        } catch (IOException e) {
            logger.error("Failed to create upload directory {}", uploadDir, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String store(MultipartFile file) throws IOException {
        String original = file.getOriginalFilename();
        String safe = (original == null) ? "file" : original.replaceAll("[^a-zA-Z0-9._-]", "_");
        String filename = UUID.randomUUID().toString() + "_" + safe;
        Path target = Paths.get(uploadDir).resolve(filename);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        logger.info("Stored file {} ({} bytes) -> {}", original, file.getSize(), target.toAbsolutePath());
        // Return the filename as the key (this is what admin download endpoint expects)
        return filename;
    }
}
