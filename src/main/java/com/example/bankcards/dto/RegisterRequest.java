package com.example.bankcards.dto;

import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;

    // getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
