package com.madfood.backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String refreshToken;
    private String email;
    private String role;
    private Long userId;
    private Long profileId;
    private String name;
}
