package com.madfood.fdms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path rootLocation;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try { Files.createDirectories(rootLocation); } catch (IOException e) { throw new RuntimeException(e); }
    }

    public String store(MultipartFile file, String subfolder) {
        String clean = Paths.get(file.getOriginalFilename()).getFileName().toString();
        String filename = UUID.randomUUID().toString() + "-" + clean;
        Path dest = this.rootLocation.resolve(subfolder).resolve(filename);
        try {
            Files.createDirectories(dest.getParent());
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
            }
            // Return application path (served via ResourceHandler)
            return "/uploads/" + subfolder + "/" + filename;
        } catch (IOException e) { throw new RuntimeException("Failed to store file", e); }
    }
}
