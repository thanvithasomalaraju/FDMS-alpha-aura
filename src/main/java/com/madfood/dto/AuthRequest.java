package com.madfood.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    private String email;
    private String password;
    private String role; // CUSTOMER, RESTAURANT, DELIVERY_PARTNER
}
