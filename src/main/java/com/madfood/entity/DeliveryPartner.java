package com.madfood.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_partners")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPartner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String profilePhoto;
    private String licenseNumber;
    private String licenseImage;
    private String rcNumber;
    private String rcImage;
    private String aadharNumber;
    private String aadharImage;
    private Boolean isApproved = false;
    private Boolean isActive = false;
    private Double latitude;
    private Double longitude;
    private Double rating = 4.5;
    private Integer totalDeliveries = 0;
    private Double totalEarnings = 0.0;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
