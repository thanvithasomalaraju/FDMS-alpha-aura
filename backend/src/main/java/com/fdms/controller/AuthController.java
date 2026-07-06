package com.fdms.controller;

import com.fdms.dto.AuthRequest;
import com.fdms.dto.AuthResponse;
import com.fdms.dto.RegisterRequest;
import com.fdms.dto.UserDto;
import com.fdms.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register endpoint called with email: {}", request.getEmail());
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        log.info("Login endpoint called with email: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        log.info("Getting user by ID: {}", id);
        UserDto user = authService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
        log.info("Updating user with ID: {}", id);
        UserDto updatedUser = authService.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }
}
