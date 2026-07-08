package com.madfood.controller;

import com.madfood.dto.AuthRequest;
import com.madfood.dto.AuthResponse;
import com.madfood.entity.User;
import com.madfood.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8000"})
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        
        if (user.isEmpty()) {
            return ResponseEntity.ok(new AuthResponse(null, "User not found", null, null, false));
        }

        if (!passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            return ResponseEntity.ok(new AuthResponse(null, "Invalid password", null, null, false));
        }

        String token = "dummy_jwt_token_" + user.get().getId(); // Replace with actual JWT generation
        return ResponseEntity.ok(new AuthResponse(token, "Login successful", user.get().getId(), user.get().getRole().toString(), true));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.ok(new AuthResponse(null, "Email already exists", null, null, false));
        }

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setFullName(request.getEmail().split("@")[0]);
        newUser.setRole(User.UserRole.valueOf(request.getRole()));
        
        User saved = userRepository.save(newUser);
        return ResponseEntity.ok(new AuthResponse(null, "Registration successful", saved.getId(), saved.getRole().toString(), true));
    }
}
