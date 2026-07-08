package com.madfood.fdms.model;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "delivery_applications")
public class DeliveryApplication {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="full_name")
    private String fullName;
    private String phone;
    private String source;

    @Column(name="photo_path")
    private String photoPath;
    @Column(name="license_path")
    private String licensePath;
    @Column(name="rc_path")
    private String rcPath;
    @Column(name="aadhar_path")
    private String aadharPath;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    private Instant createdAt = Instant.now();

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
    public String getLicensePath() { return licensePath; }
    public void setLicensePath(String licensePath) { this.licensePath = licensePath; }
    public String getRcPath() { return rcPath; }
    public void setRcPath(String rcPath) { this.rcPath = rcPath; }
    public String getAadharPath() { return aadharPath; }
    public void setAadharPath(String aadharPath) { this.aadharPath = aadharPath; }
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
