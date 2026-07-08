package com.madfood.fdms.dto;

import com.madfood.fdms.model.Role;

public class AuthResponse {
    private String token;
    private String refreshToken;
    private Long userId;
    private String name;
    private String email;
    private Role role;

    public AuthResponse() {}
    public AuthResponse(String token, Long userId, String name, String email, Role role) {
        this.token = token; this.userId = userId; this.name = name; this.email = email; this.role = role;
    }
    // getters/setters
    public String getToken(){return token;} public void setToken(String t){this.token=t;}
    public String getRefreshToken(){return refreshToken;} public void setRefreshToken(String r){this.refreshToken=r;}
    public Long getUserId(){return userId;} public void setUserId(Long id){this.userId=id;}
    public String getName(){return name;} public void setName(String n){this.name=n;}
    public String getEmail(){return email;} public void setEmail(String e){this.email=e;}
    public Role getRole(){return role;} public void setRole(Role r){this.role=r;}
}
