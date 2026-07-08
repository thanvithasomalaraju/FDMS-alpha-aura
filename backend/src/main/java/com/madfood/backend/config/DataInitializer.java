package com.madfood.backend.config;

import com.madfood.backend.model.User;
import com.madfood.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Value("${app.create-dev-users:false}")
    private boolean createDevUsers;

    @Bean
    public CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!createDevUsers) {
                System.out.println("Skipping creation of dev users (app.create-dev-users is false). Set app.create-dev-users=true for local/dev builds if you want demo accounts.");
                return;
            }

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

            String adminUsername = "admin";
            if (!userRepository.existsByUsername(adminUsername)) {
                User a = new User();
                a.setUsername(adminUsername);
                a.setPassword(passwordEncoder.encode("admin123"));
                a.setRoles("ROLE_ADMIN");
                userRepository.save(a);
                System.out.println("Created admin user: admin / admin123 (dev only)");
            } else {
                System.out.println("Admin user already exists");
            }
        };
    }
}
