package com.madfood.backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    /**
     * Store the provided MultipartFile and return a key/path that can later be used to retrieve it.
     */
    String store(MultipartFile file) throws IOException;
}
