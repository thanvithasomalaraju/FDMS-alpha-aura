package com.madfood.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String message;
    private Long userId;
    private String role;
    private Boolean success;
}
