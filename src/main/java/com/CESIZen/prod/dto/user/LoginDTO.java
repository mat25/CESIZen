package com.CESIZen.prod.dto.user;

import jakarta.validation.constraints.NotBlank;

public class LoginDTO {
    @NotBlank(message = "Le nom d'utilisateur ne peut pas être vide.")
    private String username;
    @NotBlank(message = "Le mot de passe ne peut pas être vide.")
    private String password;

    public LoginDTO() {
    }

    public LoginDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}