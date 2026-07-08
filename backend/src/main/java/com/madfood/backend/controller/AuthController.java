package com.madfood.backend.controller;

import com.madfood.backend.dto.AuthRequests;
import com.madfood.backend.model.User;
import com.madfood.backend.repository.UserRepository;
import com.madfood.backend.security.JwtTokenUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequests.RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username already exists"));
        }
        User u = new User();
        u.setUsername(req.getUsername());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        // by default: ROLE_USER
        u.setRoles("ROLE_USER");
        userRepository.save(u);
        return ResponseEntity.ok(Map.of("message", "User registered"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequests.LoginRequest req) {
        return userRepository.findByUsername(req.getUsername())
                .map(u -> {
                    if (passwordEncoder.matches(req.getPassword(), u.getPassword())) {
                        String token = jwtTokenUtil.generateToken(u.getUsername(), u.getRoles());
                        AuthRequests.LoginResponse res = new AuthRequests.LoginResponse();
                        res.setToken(token);
                        res.setUsername(u.getUsername());
                        return ResponseEntity.ok(res);
                    } else {
                        return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
                    }
                }).orElse(ResponseEntity.status(401).body(Map.of("message", "Invalid credentials")));
    }
}
