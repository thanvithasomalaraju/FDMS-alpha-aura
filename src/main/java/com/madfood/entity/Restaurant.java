package com.madfood.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "restaurants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String restaurantName;

    private String cuisineType;
    private String description;
    private String logo;
    private String banner;
    private Double latitude;
    private Double longitude;
    private String address;
    private Double rating = 4.5;
    private Integer totalOrders = 0;
    private Double revenue = 0.0;
    private Boolean isOpen = true;
    private Boolean isApproved = false;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
