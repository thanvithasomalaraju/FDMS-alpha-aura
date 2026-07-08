package com.madfood.fdms.controller;

import com.madfood.fdms.dto.AuthRequest;
import com.madfood.fdms.dto.AuthResponse;
import com.madfood.fdms.model.User;
import com.madfood.fdms.service.AuthService;
import com.madfood.fdms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired private AuthService authService;
    @Autowired private UserRepository userRepo;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        try {
            String token = authService.login(req.getEmail(), req.getPassword());
            User u = userRepo.findByEmail(req.getEmail()).orElseThrow();
            AuthResponse resp = new AuthResponse(token, u.getId(), u.getName(), u.getEmail(), u.getRole());
            return ResponseEntity.ok(Map.of("success", true, "data", resp));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String,String> body) {
        try {
            User u = authService.registerCustomer(body.get("name"), body.get("email"), body.get("password"), body.get("phone"));
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true, "data", Map.of("id", u.getId())));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }
    }
}
