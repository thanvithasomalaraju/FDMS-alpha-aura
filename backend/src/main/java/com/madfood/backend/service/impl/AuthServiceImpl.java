package com.madfood.backend.service.impl;

import com.madfood.backend.dto.AuthRequest;
import com.madfood.backend.dto.AuthResponse;
import com.madfood.backend.dto.SignupRequest;
import com.madfood.backend.entity.*;
import com.madfood.backend.exception.BadRequestException;
import com.madfood.backend.exception.ResourceNotFoundException;
import com.madfood.backend.exception.UnauthorizedException;
import com.madfood.backend.jwt.JwtTokenProvider;
import com.madfood.backend.repository.*;
import com.madfood.backend.service.AuthService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private DeliveryPartnerRepository deliveryPartnerRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostConstruct
    @Transactional
    public void initDefaultData() {
        // Initialize Roles
        String[] roleNames = {"ROLE_CUSTOMER", "ROLE_RESTAURANT", "ROLE_DELIVERY_PARTNER", "ROLE_ADMIN"};
        for (String rName : roleNames) {
            if (roleRepository.findByName(rName).isEmpty()) {
                roleRepository.save(Role.builder().name(rName).build());
            }
        }

        // Initialize Default Admin (admin@madfoods.com / admin123)
        if (!userRepository.existsByEmail("admin@madfoods.com")) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));
            
            User adminUser = User.builder()
                    .email("admin@madfoods.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(adminRole)
                    .build();
            userRepository.save(adminUser);

            Admin admin = Admin.builder()
                    .user(adminUser)
                    .name("Mad Foods Admin")
                    .build();
            adminRepository.save(admin);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getName()))
        );
    }

    @Override
    @Transactional
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials!"));

        // Check if password matches
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials!");
        }

        // Check approvals for RESTAURANT
        if (user.getRole().getName().equals("ROLE_RESTAURANT")) {
            Restaurant restaurant = restaurantRepository.findByUser(user)
                    .orElseThrow(() -> new BadRequestException("Restaurant details not found!"));
            if (!"APPROVED".equalsIgnoreCase(restaurant.getStatus())) {
                throw new UnauthorizedException("Your restaurant login is only active after approval. Status: " + restaurant.getStatus());
            }
        }

        // Authenticate user in Security context
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);

        // Manage refresh token
        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.flush();
        String refTokenString = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refTokenString)
                .expiryDate(Instant.now().plusMillis(604800000)) // 7 days
                .build();
        refreshTokenRepository.save(refreshToken);

        // Build Response
        AuthResponse response = AuthResponse.builder()
                .token(jwt)
                .refreshToken(refTokenString)
                .email(user.getEmail())
                .role(user.getRole().getName().replace("ROLE_", ""))
                .userId(user.getId())
                .build();

        // Attach profiles
        if (user.getRole().getName().equals("ROLE_CUSTOMER")) {
            Customer customer = customerRepository.findByUser(user).orElse(null);
            if (customer != null) {
                response.setProfileId(customer.getId());
                response.setName(customer.getName());
            }
        } else if (user.getRole().getName().equals("ROLE_RESTAURANT")) {
            Restaurant restaurant = restaurantRepository.findByUser(user).orElse(null);
            if (restaurant != null) {
                response.setProfileId(restaurant.getId());
                response.setName(restaurant.getName());
            }
        } else if (user.getRole().getName().equals("ROLE_DELIVERY_PARTNER")) {
            DeliveryPartner partner = deliveryPartnerRepository.findByUser(user).orElse(null);
            if (partner != null) {
                response.setProfileId(partner.getId());
                response.setName(partner.getFullName());
            }
        } else if (user.getRole().getName().equals("ROLE_ADMIN")) {
            Admin admin = adminRepository.findByUser(user).orElse(null);
            if (admin != null) {
                response.setProfileId(admin.getId());
                response.setName(admin.getName());
            }
        }

        return response;
    }

    @Override
    @Transactional
    public AuthResponse register(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use!");
        }

        String dbRoleName = "ROLE_" + request.getRole().toUpperCase();
        Role role = roleRepository.findByName(dbRoleName)
                .orElseThrow(() -> new BadRequestException("Invalid role selected: " + request.getRole()));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();
        userRepository.save(user);

        // Create profiles depending on roles
        if (dbRoleName.equals("ROLE_CUSTOMER")) {
            Customer customer = Customer.builder()
                    .user(user)
                    .name(request.getName())
                    .phone(request.getPhone())
                    .build();
            customerRepository.save(customer);
            
            // Create empty cart for the customer
            Cart cart = Cart.builder().customer(customer).build();
            customer.getAddresses(); // initialize empty
        } else if (dbRoleName.equals("ROLE_RESTAURANT")) {
            Restaurant restaurant = Restaurant.builder()
                    .user(user)
                    .name(request.getName())
                    .ownerName(request.getName())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .status("PENDING")
                    .build();
            restaurantRepository.save(restaurant);
        } else if (dbRoleName.equals("ROLE_DELIVERY_PARTNER")) {
            DeliveryPartner partner = DeliveryPartner.builder()
                    .user(user)
                    .fullName(request.getName())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .status("PENDING")
                    .build();
            deliveryPartnerRepository.save(partner);
        }

        // Generate Token directly to bypass approval checks on registration
        String jwtToken = tokenProvider.generateTokenFromUsername(user.getEmail());
        String refTokenString = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refTokenString)
                .expiryDate(Instant.now().plusMillis(604800000)) // 7 days
                .build();
        refreshTokenRepository.save(refreshToken);

        Long profileId = null;
        if (dbRoleName.equals("ROLE_CUSTOMER")) {
            Customer customer = customerRepository.findByUser(user).orElse(null);
            if (customer != null) profileId = customer.getId();
        } else if (dbRoleName.equals("ROLE_RESTAURANT")) {
            Restaurant restaurant = restaurantRepository.findByUser(user).orElse(null);
            if (restaurant != null) profileId = restaurant.getId();
        } else if (dbRoleName.equals("ROLE_DELIVERY_PARTNER")) {
            DeliveryPartner partner = deliveryPartnerRepository.findByUser(user).orElse(null);
            if (partner != null) profileId = partner.getId();
        }

        return AuthResponse.builder()
                .token(jwtToken)
                .refreshToken(refTokenString)
                .email(user.getEmail())
                .role(user.getRole().getName().replace("ROLE_", ""))
                .userId(user.getId())
                .profileId(profileId)
                .name(request.getName())
                .build();
    }

    @Override
    @Transactional
    public void logout(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            refreshTokenRepository.deleteByUser(user);
        });
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(String token) {
        RefreshToken refToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token!"));

        if (refToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refToken);
            throw new UnauthorizedException("Refresh token expired. Please login again!");
        }

        User user = refToken.getUser();
        String newJwt = tokenProvider.generateTokenFromUsername(user.getEmail());

        AuthResponse response = AuthResponse.builder()
                .token(newJwt)
                .refreshToken(token)
                .email(user.getEmail())
                .role(user.getRole().getName().replace("ROLE_", ""))
                .userId(user.getId())
                .build();

        if (user.getRole().getName().equals("ROLE_CUSTOMER")) {
            customerRepository.findByUser(user).ifPresent(c -> {
                response.setProfileId(c.getId());
                response.setName(c.getName());
            });
        } else if (user.getRole().getName().equals("ROLE_RESTAURANT")) {
            restaurantRepository.findByUser(user).ifPresent(r -> {
                response.setProfileId(r.getId());
                response.setName(r.getName());
            });
        } else if (user.getRole().getName().equals("ROLE_DELIVERY_PARTNER")) {
            deliveryPartnerRepository.findByUser(user).ifPresent(d -> {
                response.setProfileId(d.getId());
                response.setName(d.getFullName());
            });
        } else if (user.getRole().getName().equals("ROLE_ADMIN")) {
            adminRepository.findByUser(user).ifPresent(a -> {
                response.setProfileId(a.getId());
                response.setName(a.getName());
            });
        }

        return response;
    }

    @Override
    public void forgotPassword(String email) {
        // Mock checking email and generating token (for forgot password)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No account registered with this email."));
        // Standard action would email a reset token. For simulation, it's logged.
        System.out.println("Reset token generated for user: " + user.getEmail());
    }

    @Override
    @Transactional
    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No user found with email: " + email));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
