package com.madfood.backend.config;

import com.madfood.backend.model.User;
import com.madfood.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String demoUsername = "demo";
            if (!userRepository.existsByUsername(demoUsername)) {
                User u = new User();
                u.setUsername(demoUsername);
                u.setPassword(passwordEncoder.encode("demo123"));
                u.setRoles("ROLE_USER");
                userRepository.save(u);
                System.out.println("Created demo user: demo / demo123");
            } else {
                System.out.println("Demo user already exists");
            }
        };
    }
}
