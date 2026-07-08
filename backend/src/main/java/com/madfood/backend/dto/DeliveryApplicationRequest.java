package com.madfood.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class DeliveryApplicationRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 120, message = "Full name must be at most 120 characters")
    private String fullName;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Phone must be numeric and 7-15 digits (optionally prefixed with +)")
    private String phone;

    @Size(max = 60, message = "Source must be at most 60 characters")
    private String source;

    // Files are validated in the controller (size/type). MultipartFile cannot be validated with javax annotations reliably.
    private MultipartFile photo;
    private MultipartFile license;
    private MultipartFile rc;
    private MultipartFile aadhar;

    // Alternatively, when using presigned S3 uploads the frontend will submit keys for already-uploaded objects.
    private String photoKey;
    private String licenseKey;
    private String rcKey;
    private String aadharKey;

    // Getters and setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public MultipartFile getPhoto() { return photo; }
    public void setPhoto(MultipartFile photo) { this.photo = photo; }

    public MultipartFile getLicense() { return license; }
    public void setLicense(MultipartFile license) { this.license = license; }

    public MultipartFile getRc() { return rc; }
    public void setRc(MultipartFile rc) { this.rc = rc; }

    public MultipartFile getAadhar() { return aadhar; }
    public void setAadhar(MultipartFile aadhar) { this.aadhar = aadhar; }

    public String getPhotoKey() { return photoKey; }
    public void setPhotoKey(String photoKey) { this.photoKey = photoKey; }

    public String getLicenseKey() { return licenseKey; }
    public void setLicenseKey(String licenseKey) { this.licenseKey = licenseKey; }

    public String getRcKey() { return rcKey; }
    public void setRcKey(String rcKey) { this.rcKey = rcKey; }

    public String getAadharKey() { return aadharKey; }
    public void setAadharKey(String aadharKey) { this.aadharKey = aadharKey; }
}
