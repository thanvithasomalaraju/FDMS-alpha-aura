package com.madfood.fdms.service;

import com.madfood.fdms.model.User;
import com.madfood.fdms.repository.UserRepository;
import com.madfood.fdms.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    public String login(String email, String password) {
        Optional<User> o = userRepo.findByEmail(email);
        if (o.isEmpty()) throw new IllegalArgumentException("Invalid credentials");
        User u = o.get();
        if (!passwordEncoder.matches(password, u.getPasswordHash())) throw new IllegalArgumentException("Invalid credentials");
        return jwtUtil.generateToken(u.getEmail());
    }

    public User registerCustomer(String name, String email, String password, String phone) {
        if (userRepo.findByEmail(email).isPresent()) throw new IllegalArgumentException("Email already used");
        User u = new User();
        u.setName(name); u.setEmail(email); u.setPasswordHash(passwordEncoder.encode(password)); u.setPhone(phone);
        u.setRole(com.madfood.fdms.model.Role.CUSTOMER); u.setStatus(com.madfood.fdms.model.Status.ACTIVE);
        return userRepo.save(u);
    }
}
